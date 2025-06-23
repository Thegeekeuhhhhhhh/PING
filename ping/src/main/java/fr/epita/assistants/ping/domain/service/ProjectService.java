package fr.epita.assistants.ping.domain.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.Stack;
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

import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;
    @ConfigProperty(name = "PROJECT_DEFAULT_PATH", defaultValue = "")
    String path;

    public void createDirectory(UUID id) {
        if (id == null) {
            System.out.println("PAS D ID"); // On prend pas de risques
            return;
        }
        new File(path + "/" + id.toString()).mkdirs();
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
        for (UserModel temp : userRepository.listUsers()) {
            if (temp.id.equals(owner.id)) {
                var tmp = projectRepository.addProject(name, temp, path);
                createDirectory(tmp.id);
                return tmp;
            }
        }
        return null;
    }

    public List<ProjectModel> getUserProjects(UUID id) {
        return projectRepository.getUserProjects(id);
    }

    public ProjectModel updateProject(String name, UUID oldId, UUID newId) {
        ProjectModel temp = projectRepository.updateProject(name, oldId, newId);
        if (temp == null) {
            return null;
        }
        return temp;
    }

    public void deleteProject(UUID id) {
        projectRepository.deleteProject(id);
        delete(id.toString());
    }

    public List<GetFileResponse> ls(UUID id) {
        Path lol = Paths.get(path + "/" + id.toString());
        if (!Files.exists(lol)) {
            return new ArrayList<GetFileResponse>();
        }

        ArrayList<GetFileResponse> res = new ArrayList<GetFileResponse>();

        try {
            Stream<Path> stream = Files.list(lol);
            stream.forEach(elt -> {
                res.add(new GetFileResponse(elt.getFileName().toString(), elt.toString(),
                        Files.isDirectory(Paths.get(elt.toString()))));
            });
        } catch (IOException e) {
            System.out.println("WTF CA MARCHE PLUS");
        }

        return res;
    }

    public Boolean createFolder(String p) {
        System.out.println("=============================");
        System.out.println(path + "/" + p);
        System.out.println("=============================");
        File theDir = new File(path + "/" + p);
        if (!theDir.exists()) {
            theDir.mkdirs();
            return true;
        }
        return false;
    }

    public Boolean moveFolder(String p, String p2) {
        File theDir = new File(path + "/" + p);
        if (!theDir.exists()) {
            return false;
        }
        Path source = Paths.get(path + "/" + p);
        Path target = Paths.get(path + "/" + p2);
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean delete(String p) {
        Path lol = Paths.get(path + "/" + p);
        if (!Files.exists(lol)) {
            return false;
        }

        Stack<Path> s = new Stack<>();
        Stack<Path> del = new Stack<>();
        try {
            s.push(lol);
            while (!s.isEmpty()) {
                Path tmp = s.pop();
                if (!tmp.toString().equals("/")) // je sais pas car j ai un probleme de VS CODE
                {
                    del.push(tmp);
                }
                if (Files.isDirectory(tmp)) {
                    Files.list(tmp).forEach(elt -> {
                        s.push(elt);
                    });
                }
            }
            while (!del.isEmpty()) {
                Path tmp = del.pop();
                Files.delete(tmp);
            }
        } catch (IOException e) {
            System.out.println("WTF CA MARCHE PLUS");
        }

        return true;
    }

    public void addUserToProject(UUID id, UserModel user) {
        projectRepository.addUserToProject(id, user);
    }

    public Boolean deleteUserFromProject(UUID id, UserModel user) {
        return projectRepository.deleteUserFromProject(id, user);
    }
}
