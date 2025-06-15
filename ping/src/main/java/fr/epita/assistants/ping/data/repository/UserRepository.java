package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserModel> {

    @Transactional
    public Boolean add_User_in_Database(String avatar, String displayName, Boolean isAdmin, String login, String password){
        return true;
    }
}