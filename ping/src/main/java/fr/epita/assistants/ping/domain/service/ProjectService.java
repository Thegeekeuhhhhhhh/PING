package fr.epita.assistants.ping.domain.service;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import jakarta.inject.Inject;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    @ConfigProperty(name = "PROJECT_DEFAULT_PATH")
    String path;

    public void createDirectory(UUID id) {
        if (id == null) {
            System.out.println("PAS D ID"); // On prend pas de risques
            return;
        }
        new File(path + "/" + id.toString()).mkdirs();
    }

    public void modifyDirectoryName(UUID oldId, UUID newId) {
        if (oldId == null || newId == null) {
            System.out.println("PAS D ID"); // On prend pas de risques
            return;
        }
        new File(path + "/" + oldId.toString()).renameTo(new File(path + "/" + newId.toString()));
    }

    public void deleteDirectory(UUID id) {
        if (id == null) {
            System.out.println("PAS D ID"); // On prend pas de risques
            return;
        }
        try {
            FileUtils.deleteDirectory(new File(path + "/" + id.toString()));
        } catch (Exception e) {
            System.out.println("SOUCI AVEC LE TRUC LA AU SECOURS");
        }
    }

    public List<ProjectModel> getProjects() {
        return projectRepository.getProjects();
    }

    public ProjectModel getProject(UUID id) {
        return projectRepository.getProject(id);
    }

    public ProjectModel addProject(String name, UserModel owner) {
        createDirectory(owner.id);
        return projectRepository.addProject(name, owner);
    }

    public List<ProjectModel> getUserProjects(UUID id) {
        return projectRepository.getUserProjects(id);
    }

    public ProjectModel updateProject(String name, UUID oldId, UUID newId) {
        ProjectModel temp = projectRepository.updateProject(name, oldId, newId);
        modifyDirectoryName(oldId, temp.id);
        return temp;
    }

    public void deleteProject(UUID id) {
        projectRepository.deleteProject(id);
    }
}
