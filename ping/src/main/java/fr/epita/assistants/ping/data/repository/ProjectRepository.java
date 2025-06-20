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
    public List<ProjectModel> getProjects() {
        return listAll();
    }

    @Transactional
    public ProjectModel getProject(UUID id) {
        List<ProjectModel> temp = listAll();
        for (ProjectModel p : temp) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Transactional
    public List<ProjectModel> getUserProjects(UUID id) {
        ArrayList<ProjectModel> prout = new ArrayList<ProjectModel>();
        for (ProjectModel um : listAll()) {
            for (UserModel caca : um.members) {
                if (caca.id.equals(id)) {
                    prout.add(um);
                }
            }
        }
        return prout;
    }

    @Transactional
    public ProjectModel addProject(String name, UserModel owner, String path) {
        ProjectModel pm = new ProjectModel();
        pm.path = path;
        ArrayList<UserModel> temp = new ArrayList<UserModel>();
        temp.add(new UserModel(owner.avatar, owner.displayName, owner.isAdmin,
                owner.login, owner.password, owner.id));
        pm.members = temp;
        pm.owner = owner;
        pm.name = name;
        persist(pm);
        return pm;
    }

    @Transactional
    public ProjectModel updateProject(String name, UUID oldId, UUID newId) {
        ProjectModel p = getProject(oldId);
        if (p == null) {
            return null;
        }

        // Check si le nouveau proprio fait partie des membres
        Boolean ok = false;
        for (UserModel x : p.members) {
            if (x.id.equals(newId)) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            return null;
        }

        if (name.length() > 0) {
            p.name = name;
        }
        if (newId != null) {
            p.id = newId;
        }
        return p;
    }

    @Transactional
    public void deleteProject(UUID id) {
        if (getProject(id) != null) {
            delete(getProject(id));
        }
    }
}