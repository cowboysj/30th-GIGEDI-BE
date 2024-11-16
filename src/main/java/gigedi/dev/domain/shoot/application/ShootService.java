package gigedi.dev.domain.shoot.application;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import gigedi.dev.domain.auth.domain.Figma;
import gigedi.dev.domain.shoot.dao.ShootRepository;
import gigedi.dev.domain.shoot.dao.ShootStatusRepository;
import gigedi.dev.domain.shoot.domain.Shoot;
import gigedi.dev.domain.shoot.domain.ShootStatus;
import gigedi.dev.domain.shoot.domain.Status;
import gigedi.dev.domain.shoot.dto.response.GetShootResponse;
import gigedi.dev.global.error.exception.CustomException;
import gigedi.dev.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShootService {
    private final ShootRepository shootRepository;
    private final ShootStatusRepository shootStatusRepository;
    private final ShootStatusService shootStatusService;

    public List<GetShootResponse> getShoot(Long blockId) {
        List<Shoot> shoots = shootRepository.findAllByBlock_BlockIdAndDeletedAtIsNull(blockId);
        return shoots.stream()
                .map(
                        shoot -> {
                            return GetShootResponse.from(
                                    shoot,
                                    getUsersByStatus(shoot, Status.YET),
                                    getUsersByStatus(shoot, Status.DOING),
                                    getUsersByStatus(shoot, Status.DONE));
                        })
                .toList();
    }

    private List<GetShootResponse.User> getUsersByStatus(Shoot shoot, Status status) {
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

    @Transactional
    public void deleteShoot(Long shootId) {
        Shoot shoot = findValidShoot(shootId);
        shoot.deleteShoot();
    }

    @Transactional
    public void updateShootStatus(Long shootId, Status newStatus) {
        validateStatus(newStatus);
        Shoot shoot = findValidShoot(shootId);
        ShootStatus shootStatus = shootStatusService.getShootStatusByShootId(shoot.getShootId());
        shootStatus.updateStatus(newStatus);
    }

    private void validateStatus(Status status) {
        if (status == null || !EnumSet.allOf(Status.class).contains(status)) {
            throw new CustomException(ErrorCode.INVALID_STATUS);
        }
    }

    @Transactional
    public Shoot findValidShoot(Long shootId) {
        return shootRepository
                .findByShootIdAndDeletedAtIsNull(shootId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOOT_NOT_FOUND));
    }
}