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
import fr.epita.assistants.ping.data.repository.WaypointRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.common.api.response.GetFileResponse;
import fr.epita.assistants.ping.data.model.WaypointModel;
import fr.epita.assistants.ping.data.model.UserModel;

import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class WaypointService {
    @Inject
    WaypointRepository waypointRepository;

    public List<WaypointModel> getWaypoints() {
        return waypointRepository.getWaypoints();
    }

    public WaypointModel getWaypoint(UUID id) {
        return waypointRepository.getWaypoint(id);
    }

    public WaypointModel addWaypoint(String name, Float lat, Float lng, Integer order, Boolean completed) {
        return waypointRepository.addWaypoint(name, lat, lng, order, completed);
        /*
         * for (UserModel temp : userRepository.listUsers()) {
         * if (temp.id.equals(owner.id)) {
         * var tmp = waypointRepository.addWaypoint(name, temp, path);
         * createDirectory(tmp.id);
         * return tmp;
         * }
         * }
         * return null;
         */
    }

    public void deleteWaypoint(UUID id) {
        waypointRepository.deleteWaypoint(id);
    }
}
