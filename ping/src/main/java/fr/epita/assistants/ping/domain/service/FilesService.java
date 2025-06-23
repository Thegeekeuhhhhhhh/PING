package fr.epita.assistants.ping.domain.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import jakarta.inject.Inject;

import java.nio.file.StandardCopyOption;
@ApplicationScoped
public class FilesService {

    @ConfigProperty(name = "PROJECT_DEFAULT_PATH", defaultValue = "")
    String projectsPath;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;

    public byte[] getFileContent(String path) {
        Path p = Paths.get(path);
        try {
            return Files.readAllBytes(p);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteFileOrFolderRec(File f) {
        File[] files = f.listFiles();
        if (files != null) {
            for (File g : files) {
                deleteFileOrFolderRec(g);
            }
        }
        f.delete();
    }

    public void deleteFileOrFolder(String path) {
        deleteFileOrFolderRec(new File(path));
    }

    public void clearFolder(String path) {
        File f = new File(path);
        File[] files = f.listFiles();
        if (files != null) {
            for (File g : files) {
                deleteFileOrFolderRec(g);
            }
        }
    }

    public void launchDelete(String path, UUID id) {
        Path p1 = Paths.get(projectsPath + "/" + id.toString() + "/" + path);
        Path p2 = Paths.get(projectsPath + "/" + id.toString());
        if (p1.equals(p2)) {
            clearFolder(projectsPath + "/" + id.toString() + "/" + path);
        } else {
            deleteFileOrFolder(projectsPath + "/" + id.toString() + "/" + path);
        }
    }

    public Boolean moveFile(String p, String p2) {
        File file = new File(projectsPath + "/" + p);
        if (!file.exists()){
            return false;
        }
        Path source = Paths.get(projectsPath + "/" + p);
        Path target = Paths.get(projectsPath + "/" + p2);
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
