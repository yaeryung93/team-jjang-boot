package com.teamjjang.progress;

import com.teamjjang.task.Task;
import jakarta.persistence.*;
import java.time.Instant;

/** Append-only progress snapshots. The greatest id is the current snapshot. */
@Entity
@Table(name = "task_progress", indexes = @Index(name = "idx_progress_task_id", columnList = "task_id,id"))
public class TaskProgress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @Column(nullable = false)
    private int percent;
    @Column(nullable = false, length = 1000)
    private String note;
    @Column(nullable = false)
    private Instant recordedAt;
    protected TaskProgress() {}
    public TaskProgress(Task task, int percent, String note) {
        if (percent < 0 || percent > 100) throw new IllegalArgumentException("Invalid progress");
        this.task = task; this.percent = percent; this.note = note; this.recordedAt = Instant.now();
    }
    public Long getId() { return id; }
    public int getPercent() { return percent; }
    public String getNote() { return note; }
    public Instant getRecordedAt() { return recordedAt; }
}
