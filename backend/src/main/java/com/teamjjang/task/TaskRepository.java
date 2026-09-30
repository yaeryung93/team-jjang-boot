package com.teamjjang.task;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject_IdOrderByIdAsc(Long projectId);
    Optional<Task> findByIdAndProject_Id(Long id, Long projectId);

}

