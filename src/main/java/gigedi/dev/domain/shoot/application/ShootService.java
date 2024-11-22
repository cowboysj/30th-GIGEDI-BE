package gigedi.dev.domain.shoot.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gigedi.dev.domain.auth.domain.Figma;
import gigedi.dev.domain.block.application.BlockService;
import gigedi.dev.domain.block.domain.Block;
import gigedi.dev.domain.discord.application.AlarmService;
import gigedi.dev.domain.file.domain.File;
import gigedi.dev.domain.shoot.dao.ShootRepository;
import gigedi.dev.domain.shoot.dao.ShootStatusRepository;
import gigedi.dev.domain.shoot.domain.Shoot;
import gigedi.dev.domain.shoot.domain.Status;
import gigedi.dev.domain.shoot.dto.response.GetShootResponse;
import gigedi.dev.global.error.exception.CustomException;
import gigedi.dev.global.error.exception.ErrorCode;
import gigedi.dev.global.util.FigmaUtil;
import gigedi.dev.global.util.ShootUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShootService {
    private final ShootRepository shootRepository;
    private final ShootStatusRepository shootStatusRepository;
    private final FigmaUtil figmaUtil;
    private final BlockService blockService;
    private final ShootTagService shootTagService;
    private final AlarmService alarmService;

    @Transactional(readOnly = true)
    public List<GetShootResponse> getShoot(Long blockId) {
        List<Shoot> shoots = shootRepository.findAllByBlock_BlockIdAndDeletedAtIsNull(blockId);
        return shoots.stream()
                .map(
                        shoot -> {
                            return GetShootResponse.of(
                                    shoot,
                                    getUsersByStatus(shoot, Status.YET),
                                    getUsersByStatus(shoot, Status.DOING),
                                    getUsersByStatus(shoot, Status.DONE));
                        })
                .toList();
    }

    @Transactional
    public void deleteShoot(Long shootId) {
        Shoot shoot = findValidShoot(shootId);
        shoot.deleteShoot();
    }

    @Transactional
    public GetShootResponse createShoot(Long blockId, String content) {
        Block block = blockService.getBlockById(blockId);
        final Figma figma = figmaUtil.getCurrentFigma();
        Shoot shoot = Shoot.createShoot(content, figma, block);
        shootRepository.save(shoot);
        List<String> tags = processTags(content, shoot);
        alarmService.sendAlarmToDiscord(tags, block, figma, content);

        return GetShootResponse.of(shoot, null, null, null);
    }

    private List<String> processTags(String content, Shoot shoot) {
        File currentFile = figmaUtil.getCurrentFile();
        List<String> tags = ShootUtil.extractTags(content);
        shootTagService.createShootTags(shoot, tags, currentFile);
        return tags;
    }

    public List<GetShootResponse.User> getUsersByStatus(Shoot shoot, Status status) {
        return shootStatusRepository
                .findByShoot_ShootIdAndStatus(shoot.getShootId(), status)
                .stream()
                .map(
                        shootStatus -> {
                            Figma figma = shootStatus.getFigma();
                            return new GetShootResponse.User(
                                    figma.getFigmaId(),
                                    figma.getFigmaName(),
                                    figma.getFigmaProfile());
                        })
                .collect(Collectors.toList());
    }

    public Shoot findValidShoot(Long shootId) {
        return shootRepository
                .findByShootIdAndDeletedAtIsNull(shootId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOOT_NOT_FOUND));
    }
}
