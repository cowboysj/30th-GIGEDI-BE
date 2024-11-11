package gigedi.dev.domain.archive.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gigedi.dev.domain.archive.dao.ArchiveRepository;
import gigedi.dev.domain.archive.domain.Archive;
import gigedi.dev.domain.archive.dto.request.ArchiveCreateRequest;
import gigedi.dev.domain.archive.dto.response.ArchiveInfoResponse;
import gigedi.dev.domain.auth.domain.Figma;
import gigedi.dev.domain.file.domain.File;
import gigedi.dev.global.common.constants.FigmaConstants;
import gigedi.dev.global.error.exception.CustomException;
import gigedi.dev.global.error.exception.ErrorCode;
import gigedi.dev.global.util.FigmaUtil;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ArchiveService {
    private final ArchiveRepository archiveRepository;
    private final ArchiveTitleService archiveTitleService;
    private final FigmaUtil figmaUtil;

    public ArchiveInfoResponse createArchive(ArchiveCreateRequest request) {
        File currentFile = figmaUtil.getCurrentFile();
        Figma currentFigma = figmaUtil.getCurrentFigma();

        if (currentFile.getArchiveCount() >= FigmaConstants.MaxCount) {
            throw new CustomException(ErrorCode.ARCHIVE_EXCEED_LIMIT);
        }

        String archiveTitle = request.archiveTitle();

        if (archiveTitle == null || archiveTitle.trim().isEmpty()) {
            archiveTitle = FigmaConstants.NEW_ARCHIVE_NAME;
        }

        archiveTitle = archiveTitleService.generateUniqueTitle(archiveTitle);
        Archive createdArchive =
                archiveRepository.save(
                        Archive.createArchive(archiveTitle, currentFile, currentFigma));
        currentFile.increaseArchiveCount();
        return ArchiveInfoResponse.from(createdArchive);
    }

    public Archive getArchiveById(Long archiveId) {
        return archiveRepository
                .findByArchiveIdAndDeletedAtIsNull(archiveId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARCHIVE_NOT_FOUND));
    }
}
