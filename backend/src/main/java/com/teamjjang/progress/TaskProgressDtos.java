package com.teamjjang.progress;

import jakarta.validation.constraints.*;
import java.time.Instant;

public final class TaskProgressDtos {
    private TaskProgressDtos() {}
    public record Create(@NotNull @Min(0) @Max(100) Integer percent,
                         @NotNull @Size(max = 1000) String note) {}
    public record View(Long id, int percent, String note, Instant recordedAt) {
        public static View from(TaskProgress progress) {
            return new View(progress.getId(), progress.getPercent(), progress.getNote(), progress.getRecordedAt());
        }
    }
}
