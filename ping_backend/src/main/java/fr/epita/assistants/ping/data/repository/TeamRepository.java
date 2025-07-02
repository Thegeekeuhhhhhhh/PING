package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.MemberModel;
import fr.epita.assistants.ping.data.model.TeamModel;
import fr.epita.assistants.ping.data.model.TeamModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.model.WaypointModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<TeamModel> {

    @Transactional
    public List<TeamModel> getTeams() {
        return listAll();
    }

    @Transactional
    public TeamModel getTeam(UUID id) {
        List<TeamModel> temp = listAll();
        for (TeamModel p : temp) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Transactional
    public TeamModel addTeam(String name, String color, String status, List<MemberModel> lm, List<WaypointModel> lw) {
        TeamModel pm = new TeamModel();
        pm.name = name;
        pm.color = color;
        pm.status = status;
        pm.members = lm;
        pm.waypoints = lw;
        persist(pm);
        return pm;
    }

    @Transactional
    public void deleteTeam(UUID id) {
        if (getTeam(id) != null) {
            delete(getTeam(id));
        }
    }

    @Transactional
    public Boolean addMember(MemberModel user, String name) {
        TeamModel m = null;
        List<TeamModel> temp = listAll();
        for (TeamModel p : temp) {
            if (p.name.equals(name)) {
                m = p;
            }
        }
        if (m == null) {
            return false;
        }
        m.members.add(user);
        return true;
    }
}