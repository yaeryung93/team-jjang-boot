package com.teamjjang.project;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;
    public ProjectController(ProjectService service) { this.service = service; }
    @GetMapping
    public List<ProjectDtos.View> list() { return service.list(); }
    @GetMapping("/{id}")
    public ProjectDtos.View get(@PathVariable Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<ProjectDtos.View> create(@Valid @RequestBody ProjectDtos.Create input) {
        var result = service.create(input);
        return ResponseEntity.created(URI.create("/api/projects/" + result.id())).body(result);
    }
}
