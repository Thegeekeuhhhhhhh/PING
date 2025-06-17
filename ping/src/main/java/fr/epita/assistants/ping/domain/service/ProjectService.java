package fr.epita.assistants.ping.domain.service;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import jakarta.inject.Inject;
import fr.epita.assistants.ping.data.model.ProjectModel;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    public List<ProjectModel> GetProjects() {
        return projectRepository.GetProjects();
    }
}
