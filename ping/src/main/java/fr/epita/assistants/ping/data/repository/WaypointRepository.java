package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.WaypointModel;
import fr.epita.assistants.ping.data.model.WaypointModel;
import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WaypointRepository implements PanacheRepository<WaypointModel> {

    @Transactional
    public List<WaypointModel> getWaypoints() {
        return listAll();
    }

    @Transactional
    public WaypointModel getWaypoint(UUID id) {
        List<WaypointModel> temp = listAll();
        for (WaypointModel p : temp) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Transactional
    public WaypointModel addWaypoint(String name, Float lat, Float lng, Integer order, Boolean completed) {
        WaypointModel pm = new WaypointModel();
        pm.name = name;
        pm.lat = lat;
        pm.lng = lng;
        pm.order = order;
        pm.completed = completed;
        persist(pm);
        return pm;
    }

    @Transactional
    public void deleteWaypoint(UUID id) {
        if (getWaypoint(id) != null) {
            delete(getWaypoint(id));
        }
    }
}