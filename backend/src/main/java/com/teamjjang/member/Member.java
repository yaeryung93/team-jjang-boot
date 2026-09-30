package com.teamjjang.member;

import com.teamjjang.project.Project;
import com.teamjjang.task.TaskDtos;
import jakarta.persistence.*;

/** A participant in one project; not an authentication account. */
@Entity
@Table(name = "members")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, length = 50)
    private String role;
    protected Member() {}
    public Member(Project project, String name, String role) {
        this.project = project; this.name = name; this.role = role;
    }
    public Long getId() { return id; }
    public Long getProjectId() { return project.getId(); }
    public String getName() { return name; }
    public String getRole() { return role; }
}
