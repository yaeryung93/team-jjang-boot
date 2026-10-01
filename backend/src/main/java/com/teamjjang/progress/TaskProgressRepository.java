package com.teamjjang.progress;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskProgressRepository extends JpaRepository<TaskProgress, Long> {
    List<TaskProgress> findByTask_IdOrderByIdDesc(Long taskId);
    boolean existsByTask_Id(Long taskId);
}
