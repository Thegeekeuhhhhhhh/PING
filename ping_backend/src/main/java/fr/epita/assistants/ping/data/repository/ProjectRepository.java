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
    public ProjectModel getProject_by_name(String name) {
        List<ProjectModel> temp = listAll();
        for (ProjectModel p : temp) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        return null;
    }

    @Transactional
    public List<ProjectModel> getUserProjects(UUID id) {
        List<ProjectModel> prout = new ArrayList<ProjectModel>();
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
        System.out.println("on est la ici");
        if (p == null) {
            return null;
        }

        System.out.println("On est la");

        // Check si le nouveau proprio fait partie des membres
        Boolean ok = false;
        if (newId != null) {
            for (UserModel x : p.members) {
                if (x.id.equals(newId)) {
                    if (newId != null) {
                        p.owner = x;
                    }
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return null;
            }
        }
        if (name.length() > 0 && !name.isBlank()) {
            p.name = name;
        }
        return p;
    }

    @Transactional
    public void deleteProject(UUID id) {
        if (getProject(id) != null) {
            delete(getProject(id));
        }
    }

    @Transactional
    public void addUserToProject(UUID id, UserModel user) {
        ProjectModel p = getProject(id);
        p.members.add(user);
    }

    @Transactional
    public Boolean deleteUserFromProject(UUID id, UserModel user) {
        ProjectModel p = getProject(id);
        Boolean changed = false;
        for (UserModel t : p.members) {
            if (t.id.equals(user.id)) {
                p.members.remove(t);
                changed = true;
                break;
            }
        }

        persist(p);
        return changed;
    }
}