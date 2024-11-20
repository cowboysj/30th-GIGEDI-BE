package gigedi.dev.domain.file.dao;

import java.util.List;
import java.util.Optional;

import gigedi.dev.domain.auth.domain.Figma;
import gigedi.dev.domain.file.domain.Authority;

public interface AuthorityRepositoryCustom {
    List<Authority> findRelatedAuthorities(Long memberId);

    Optional<Authority> findByFileAndActiveFigma(Long fileId, List<Figma> figmaList);
}