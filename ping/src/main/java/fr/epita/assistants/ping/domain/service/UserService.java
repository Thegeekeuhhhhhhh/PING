package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.data.model.UserModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public UserModel addUser(String avatar, String displayName, Boolean isAdmin, String login, String password) {
        return userRepository.addUserInDatabase(avatar, displayName, isAdmin, login, password);
    }

    public UserModel checkUser(String login, String password) {
        return userRepository.checkLogin(login, password);
    }

    public UserModel updateUser(UUID id, String displayName, String avatar, String password) {
        return userRepository.updateUser(id, displayName, password, avatar);
    }

    public UserModel getUser(UUID id) {
        return userRepository.getUser(id);
    }

    public boolean deleteUser(UUID id) {
        return userRepository.deleteUser(id);
    }

    public List<UserModel> listUsers() {
        return userRepository.listUsers();
    }

    // public List<UserModel> add_User(String avatar, String displayName, Boolean
    // isAdmin, String login, String password){
    // return true;
    // }
}