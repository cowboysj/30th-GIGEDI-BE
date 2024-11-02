package gigedi.dev.domain.auth.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gigedi.dev.domain.auth.domain.Figma;

public interface FigmaRepository extends JpaRepository<Figma, Long> {
    Optional<Figma> findByMemberId(Long memberId);
}