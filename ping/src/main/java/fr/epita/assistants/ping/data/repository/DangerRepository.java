package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.DangerModel;
import fr.epita.assistants.ping.data.model.DangerModel;
import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DangerRepository implements PanacheRepository<DangerModel> {

    @Transactional
    public List<DangerModel> getDangers() {
        return listAll();
    }

    @Transactional
    public DangerModel getDanger(UUID id) {
        List<DangerModel> temp = listAll();
        for (DangerModel p : temp) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Transactional
    public DangerModel addDanger(String place, Integer number, String type, String description) {
        DangerModel pm = new DangerModel();
        pm.place = place;
        pm.number = number;
        pm.type = type;
        pm.description = description;
        persist(pm);
        return pm;
    }

    @Transactional
    public void deleteDanger(UUID id) {
        if (getDanger(id) != null) {
            delete(getDanger(id));
        }
    }
}