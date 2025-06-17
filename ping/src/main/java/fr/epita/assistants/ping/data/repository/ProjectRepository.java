package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProjectRepository implements PanacheRepository<ProjectModel> {

    @Transactional
    public List<ProjectModel> GetProjects() {
        return listAll();
    }

    @Transactional
    public ProjectModel getProject(Long id) {
        ProjectModel project = findById(id);
        if (project == null) {
            return null;
        }
        return project;
    }
}