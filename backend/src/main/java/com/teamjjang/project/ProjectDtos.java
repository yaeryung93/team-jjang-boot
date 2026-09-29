package com.teamjjang.project;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public final class ProjectDtos {
    private ProjectDtos() {}
    public record Create(@NotBlank @Size(max = 100) String name, @NotNull @Size(max = 2000) String description, @NotNull @FutureOrPresent LocalDate deadline) {}
    public record Update(@NotBlank @Size(max=100) String name, @NotNull @Size(max=2000) String description){}
    public record View(Long id, String name, String description, LocalDate deadline) {
        public static View from(Project project) {
            return new View(project.getId(), project.getName(), project.getDescription(), project.getDeadline());
        }
    }
}
