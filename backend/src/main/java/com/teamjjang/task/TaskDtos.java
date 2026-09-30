package com.teamjjang.task;

import jakarta.validation.constraints.*;

public final class TaskDtos {
    private TaskDtos() {}
    public record Create(@NotBlank @Size(max = 200) String title, @Positive Long assigneeId, @NotNull @Min(1) @Max(100000) Integer optimisticHours, @NotNull @Min(1) @Max(100000) Integer likelyHours, @NotNull @Min(1) @Max(100000) Integer pessimisticHours) {}
    public record View(Long id, Long projectId, Long assigneeId, String title, int optimisticHours, int likelyHours, int pessimisticHours) {
        public static View from(Task task) {
            return new View(task.getId(), task.getProjectId(), task.getAssigneeId(), task.getTitle(),
                    task.getOptimisticHours(), task.getLikelyHours(), task.getPessimisticHours());
        }
    }
    public record ChangeAssignee(@Positive Long assigneeId){}
    public record Update(@NotBlank @Size(max=200) String title, @NotNull @Min(1) @Max(100000) Integer optimisticHours,@NotNull @Min(1) @Max(100000) Integer likelyHours, @NotNull @Min(1) @Max(100000) Integer pessimisticHours){}
}
