package fr.epita.assistants.ping.domain.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.common.api.response.GetFileResponse;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;

    // @ConfigProperty(name = "PROJECT_DEFAULT_PATH")
    String path = "/tmp/projects";

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
        for (UserModel temp : userRepository.listUsers()) {
            if (temp.id.equals(owner.id)) {
                return projectRepository.addProject(name, temp, path);
            }
        }
        return null;
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

    public List<GetFileResponse> ls(UUID id) {
        Path lol = Paths.get(path + "/" + id.toString());
        if (!Files.exists(lol)) {
            return null;
        }

        ArrayList<GetFileResponse> res = new ArrayList<GetFileResponse>();

        try {
            Stream<Path> stream = Files.list(lol);
            stream.forEach(elt -> {
                res.add(new GetFileResponse(elt.getFileName().toString(), elt.toString(), Files.isDirectory(lol)));
            });
        } catch (IOException e) {
            System.out.println("WTF CA MARCHE PLUS");
        }

        return res;
    }
}
