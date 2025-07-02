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
import fr.epita.assistants.ping.data.repository.DangerRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.common.api.response.GetFileResponse;
import fr.epita.assistants.ping.data.model.DangerModel;
import fr.epita.assistants.ping.data.model.UserModel;

import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class DangerService {
    @Inject
    DangerRepository dangerRepository;

    public List<DangerModel> getDangers() {
        return dangerRepository.getDangers();
    }

    public DangerModel getDanger(UUID id) {
        return dangerRepository.getDanger(id);
    }

    public DangerModel addDanger(String place, Integer number, String type, String description) {
        return dangerRepository.addDanger(place, number, type, description);
        /*
         * for (UserModel temp : userRepository.listUsers()) {
         * if (temp.id.equals(owner.id)) {
         * var tmp = dangerRepository.addDanger(name, temp, path);
         * createDirectory(tmp.id);
         * return tmp;
         * }
         * }
         * return null;
         */
    }

    public void deleteDanger(UUID id) {
        dangerRepository.deleteDanger(id);
    }
}
