package com.teamjjang.task;

import com.teamjjang.member.Member;
import com.teamjjang.project.Project;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Member assignee;
    @Column(nullable = false, length = 200)
    private String title;
    // Whole working hours. Initial total estimate, not a remaining-time estimate.
    @Column(nullable = false)
    private int optimisticHours;
    @Column(nullable = false)
    private int likelyHours;
    @Column(nullable = false)
    private int pessimisticHours;
    protected Task() {}
    public Task(Project project, Member assignee, String title, int optimisticHours,
                int likelyHours, int pessimisticHours) {
        if (optimisticHours < 1 || optimisticHours > likelyHours || likelyHours > pessimisticHours) {
            throw new IllegalArgumentException("Invalid three-point estimate");
        }
        this.project = project; this.assignee = assignee; this.title = title;
        this.optimisticHours = optimisticHours; this.likelyHours = likelyHours;
        this.pessimisticHours = pessimisticHours;
    }
    public Long getId() { return id; }
    public Long getProjectId() { return project.getId(); }
    public Long getAssigneeId() { return assignee == null ? null : assignee.getId(); }
    public String getTitle() { return title; }
    public int getOptimisticHours() { return optimisticHours; }
    public int getLikelyHours() { return likelyHours; }
    public int getPessimisticHours() { return pessimisticHours; }
}
