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

    public UserModel add_User(String avatar, String displayName, Boolean isAdmin, String login, String password) {
        return userRepository.add_User_in_Database(avatar, displayName, isAdmin, login, password);
    }

    public UserModel checkUser(String login, String password) {
        return userRepository.checkLogin(login, password);
    }

    public UserModel updateUser(Long id, String displayName, String avatar, String password) {
        return userRepository.updateUser(id, displayName, password, avatar);
    }

    public UserModel GetUser(Long id) {
        return userRepository.GetUser(id);
    }

    public boolean DeleteUser(Long id) {
        return userRepository.DeleteUser(id);
    }

    public List<UserModel> listUsers() {
        return userRepository.listUsers();
    }

    // public List<UserModel> add_User(String avatar, String displayName, Boolean
    // isAdmin, String login, String password){
    // return true;
    // }
}