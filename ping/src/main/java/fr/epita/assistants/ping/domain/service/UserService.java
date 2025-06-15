package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.data.model.UserModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public Boolean add_User(String avatar, String displayName, Boolean isAdmin, String login, String password){
        return true;
    }

    public List<UserModel> add_User(String avatar, String displayName, Boolean isAdmin, String login, String password){
        return true;
    }
}