package com.teamjjang.task;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }
    @GetMapping
    public List<TaskDtos.View> list(@PathVariable Long projectId) { return service.list(projectId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TaskDtos.View create(@PathVariable Long projectId, @Valid @RequestBody TaskDtos.Create input) {
        return service.create(projectId, input);
    }
    @PatchMapping("/{taskId}/assignee")
    public TaskDtos.View changeAssignee(@PathVariable Long projectId, @PathVariable Long taskId, @Valid @RequestBody TaskDtos.ChangeAssignee input) {
        return service.changeAssignee(projectId, taskId, input);
    }
    @PatchMapping("/{taskId}")
    public TaskDtos.View update(@PathVariable Long projectId, @PathVariable Long taskId, @Valid @RequestBody TaskDtos.Update input) {
        return service.update(projectId, taskId, input);
    }
}
