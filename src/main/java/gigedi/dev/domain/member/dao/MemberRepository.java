package gigedi.dev.domain.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import gigedi.dev.domain.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {}
