package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.MemberModel;
import fr.epita.assistants.ping.data.model.MemberModel;
import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MemberRepository implements PanacheRepository<MemberModel> {

    @Transactional
    public List<MemberModel> getMembers() {
        return listAll();
    }

    @Transactional
    public MemberModel getMember(UUID id) {
        List<MemberModel> temp = listAll();
        for (MemberModel p : temp) {
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Transactional
    public MemberModel getMember_by_login(String login) {
        List<MemberModel> temp = listAll();
        for (MemberModel p : temp) {
            if (p.login.equals(login)) {
                return p;
            }
        }
        return null;
    }

    @Transactional
    public MemberModel addMember(String login, String name, String role, String status) {
        MemberModel pm = new MemberModel();
        pm.login = login;
        pm.name = name;
        pm.role = role;
        pm.status = status;
        persist(pm);
        return pm;
    }

    @Transactional
    public void deleteMember(UUID id) {
        if (getMember(id) != null) {
            delete(getMember(id));
        }
    }
}