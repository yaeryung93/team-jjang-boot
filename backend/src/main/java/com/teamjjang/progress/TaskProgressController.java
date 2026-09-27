package com.teamjjang.progress;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/progress")
public class TaskProgressController {
    private final TaskProgressService service;
    public TaskProgressController(TaskProgressService service) { this.service = service; }
    @GetMapping
    public List<TaskProgressDtos.View> list(@PathVariable Long projectId, @PathVariable Long taskId) {
        return service.list(projectId, taskId);
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TaskProgressDtos.View create(@PathVariable Long projectId, @PathVariable Long taskId,
                                        @Valid @RequestBody TaskProgressDtos.Create input) {
        return service.create(projectId, taskId, input);
    }
}
