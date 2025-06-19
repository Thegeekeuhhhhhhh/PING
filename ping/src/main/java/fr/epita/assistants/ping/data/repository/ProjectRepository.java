package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProjectRepository implements PanacheRepository<ProjectModel> {

    @Transactional
    public List<ProjectModel> GetProjects() {
        return listAll();
    }

    @Transactional
    public List<ProjectModel> GetUserProjects(UUID id) {
        ArrayList<ProjectModel> prout = new ArrayList<ProjectModel>();
        for (ProjectModel um : listAll()) {
            for (UserModel caca : um.members) {
                prout.add(um);
            }
        }
        return prout;
    }

    @Transactional
    public ProjectModel getProject(UUID id) {
        for (ProjectModel um : listAll()) {
            if (um.id.equals(id)) {
                return um;
            }
        }
        return null;
    }

    @Transactional
    public ProjectModel AddProject(String name, UserModel owner) {
        ProjectModel pm = new ProjectModel();
        pm.members = new ArrayList<UserModel>(List.of(owner));
        pm.owner = owner;
        pm.name = name;
        persist(pm);
        return pm;
    }
}