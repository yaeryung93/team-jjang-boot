package com.teamjjang.project;

import com.teamjjang.common.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projects;
    public ProjectService(ProjectRepository projects) {
        this.projects = projects;
    }
    public Project require(Long id) {
        return projects.findById(id).orElseThrow(() -> ApiException.notFound("프로젝트"));
    }
    public List<ProjectDtos.View> list() {
        return projects.findAll(Sort.by("id")).stream().map(ProjectDtos.View::from).toList();
    }
    public ProjectDtos.View get(Long id) {
        return ProjectDtos.View.from(require(id));
    }
    @Transactional
    public ProjectDtos.View create(ProjectDtos.Create input) {
        return ProjectDtos.View.from(projects.save(new Project(input.name().trim(), input.description(), input.deadline())));
    }
    @Transactional
    public ProjectDtos.View update(Long id, ProjectDtos.Update input){
        Project project=require(id);
        project.updateInfo(input.name().trim(), input.description().trim());
        return ProjectDtos.View.from(project);
    }

}
