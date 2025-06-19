package fr.epita.assistants.ping.domain.service;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import jakarta.inject.Inject;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    public List<ProjectModel> GetProjects() {
        return projectRepository.GetProjects();
    }

    public ProjectModel AddProject(String name, UserModel owner) {
        return projectRepository.AddProject(name, owner);
    }

    public List<ProjectModel> GetUserProjects(UUID id) {
        return projectRepository.GetUserProjects(id);
    }
}
