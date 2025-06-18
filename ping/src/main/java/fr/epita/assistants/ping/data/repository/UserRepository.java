package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserModel> {

    @Transactional
    public UserModel add_User_in_Database(String avatar, String displayName, Boolean isAdmin, String login,
            String password) {
        if (find("login", login).count() > 0) {
            return null;
        }
        UserModel prout = new UserModel();
        prout.avatar = avatar;
        prout.displayName = displayName;
        prout.isAdmin = isAdmin;
        prout.login = login;
        prout.password = password;
        persist(prout);
        return prout;
    }

    @Transactional
    public UserModel checkLogin(String login, String password) {
        System.out.println("Start");
        List<UserModel> temp = listAll();
        System.out.println("Le plus dur est fait");
        for (UserModel u : temp) {
            if (u.login.equals(login)) {
                if (u.password.equals(password)) {
                    return u;
                }
                System.out.println("Dommage");
            }
        }

        return null;
    }

    @Transactional
    public UserModel updateUser(Long id, String displayName, String password, String avatar) {
        UserModel user = findById(id);
        if (user == null) {
            return null;
        }
        user.password = password;
        user.displayName = displayName;
        user.avatar = avatar;
        return user;
    }

    @Transactional
    public UserModel GetUser(Long id) {
        UserModel user = findById(id);
        if (user == null) {
            return null;
        }
        return user;
    }

    @Transactional
    public boolean DeleteUser(Long id) {
        return deleteById(id);
    }

    @Transactional
    public List<UserModel> listUsers() {
        return listAll();
    }
}