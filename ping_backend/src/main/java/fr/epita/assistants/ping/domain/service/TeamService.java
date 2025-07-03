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
import fr.epita.assistants.ping.data.repository.TeamRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.data.repository.WaypointRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.common.api.response.GetFileResponse;
import fr.epita.assistants.ping.data.model.MemberModel;
import fr.epita.assistants.ping.data.model.TeamModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.model.WaypointModel;

import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class TeamService {
    @Inject
    TeamRepository teamRepository;

    @Inject
    WaypointRepository waypointRepository;

    @Inject
    MemberService memberService;

    public List<TeamModel> getTeams() {
        return teamRepository.getTeams();
    }

    public TeamModel getTeam(UUID id) {
        return teamRepository.getTeam(id);
    }

    public TeamModel addTeam(String name, String color, String status, List<UUID> lm, List<WaypointModel> lw) {
        List<MemberModel> rm = new ArrayList<MemberModel>();
        for (UUID t : lm) {
            rm.add(memberService.getMember(t));
        }
        List<WaypointModel> persistedWaypoints = new ArrayList<>();
        for (WaypointModel waypoint : lw) {
            if (waypoint.getId() == null) { 
                persistedWaypoints.add(waypointRepository.save(waypoint));
            } else {
                persistedWaypoints.add(waypoint);
            }
        }
        return teamRepository.addTeam(name, color, status, rm, lw);
        /*
         * for (UserModel temp : userRepository.listUsers()) {
         * if (temp.id.equals(owner.id)) {
         * var tmp = teamRepository.addTeam(name, temp, path);
         * createDirectory(tmp.id);
         * return tmp;
         * }
         * }
         * return null;
         */
    }

    public void deleteTeam(UUID id) {
        teamRepository.deleteTeam(id);
    }

    public MemberModel addMember(String login, String name) {
        MemberModel m = memberService.getMember_by_login(login);
        if (m == null) {
            return null;
        }
        if (teamRepository.addMember(m, name)) {
            return memberService.getMember_by_login(login);
        }

        return null;
    }
}
