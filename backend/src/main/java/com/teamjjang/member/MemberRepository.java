package com.teamjjang.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByProject_IdOrderByIdAsc(Long projectId);
    Optional<Member> findByIdAndProject_Id(Long id, Long projectId);
}
