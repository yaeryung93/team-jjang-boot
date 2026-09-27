package com.teamjjang.progress;

import com.teamjjang.task.TaskService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskProgressService {
    private final TaskProgressRepository progress;
    private final TaskService tasks;
    public TaskProgressService(TaskProgressRepository progress, TaskService tasks) {
        this.progress = progress; this.tasks = tasks;
    }
    public List<TaskProgressDtos.View> list(Long projectId, Long taskId) {
        tasks.require(projectId, taskId);
        return progress.findByTask_IdOrderByIdDesc(taskId).stream().map(TaskProgressDtos.View::from).toList();
    }
    @Transactional
    public TaskProgressDtos.View create(Long projectId, Long taskId, TaskProgressDtos.Create input) {
        return TaskProgressDtos.View.from(progress.save(new TaskProgress(tasks.require(projectId, taskId), input.percent(), input.note())));
    }
}
