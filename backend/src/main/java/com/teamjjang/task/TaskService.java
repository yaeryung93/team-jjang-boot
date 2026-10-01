package com.teamjjang.task;

import com.teamjjang.common.ApiException;
import com.teamjjang.member.Member;
import com.teamjjang.member.MemberRepository;
import com.teamjjang.progress.TaskProgress;
import com.teamjjang.progress.TaskProgressRepository;
import com.teamjjang.project.ProjectService;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository tasks;
    private final MemberRepository members;
    private final ProjectService projects;
    private final TaskProgressRepository progress;
    public TaskService(TaskRepository tasks, MemberRepository members, ProjectService projects, TaskProgressRepository progress) {
        this.tasks=tasks;
        this.members=members;
        this.projects=projects;
        this.progress=progress;
    }
    public Task require(Long projectId, Long taskId) {
        return tasks.findByIdAndProject_Id(taskId, projectId).orElseThrow(() -> ApiException.notFound("작업"));
    }
    public List<TaskDtos.View> list(Long projectId) {
        projects.require(projectId);
        return tasks.findByProject_IdOrderByIdAsc(projectId).stream().map(TaskDtos.View::from).toList();
    }
    @Transactional
    public TaskDtos.View create(Long projectId, TaskDtos.Create input) {
        var project = projects.require(projectId);
        if (input.optimisticHours() > input.likelyHours() || input.likelyHours() > input.pessimisticHours()) {
            throw ApiException.badRequest("예상 시간은 낙관 ≤ 보통 ≤ 비관 순서여야 합니다.");
        }
        Member assignee = (input.assigneeId()==null) ? null : members.findByIdAndProject_Id(input.assigneeId(), projectId)
                .orElseThrow(() -> ApiException.badRequest("담당자는 해당 프로젝트의 팀원이어야 합니다."));
        return TaskDtos.View.from(tasks.save(new Task(project, assignee, input.title().trim(),
                input.optimisticHours(), input.likelyHours(), input.pessimisticHours())));
    }

    @Transactional
    public TaskDtos.View changeAssignee(Long projectId, Long taskId, TaskDtos.ChangeAssignee input) {
        Task task = require(projectId, taskId);
        Member assignee = null;
        if (input.assigneeId()!=null) {
            assignee = members.findByIdAndProject_Id(input.assigneeId(), projectId).orElse(null);
            if (assignee==null) {
                throw ApiException.badRequest("담당자는 해당 프로젝트의 팀원이어야 합니다.");
            }
        }
        task.changeAssignee(assignee);
        return TaskDtos.View.from(task);
    }

    @Transactional
    public TaskDtos.View update(Long projectId, Long taskId, TaskDtos.Update input) {
        Task task=require(projectId, taskId);
        if (input.optimisticHours()>input.likelyHours() || input.likelyHours()>input.pessimisticHours()){
            throw ApiException.badRequest("예상 시간은 낙관 ≤ 보통 ≤ 비관 순서여야 합니다.");
        }
        task.updateInfo(input.title().trim(), input.optimisticHours(), input.likelyHours(), input.pessimisticHours());
        return TaskDtos.View.from(task);
    }

    @Transactional
    public void delete(Long projectId, Long taskId) {
        Task task=require(projectId, taskId);
        boolean hasProgress=progress.existsByTask_Id(taskId);
        if(hasProgress){
            throw new ApiException(HttpStatus.CONFLICT, "진행률 기록이 있는 작업은 삭제할 수 없습니다.");
        }
        tasks.delete(task);
    }
}
