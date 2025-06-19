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
    public UserModel updateUser(UUID id, String displayName, String password, String avatar) {
        UserModel user = GetUser(id);
        System.out.println("Test");
        System.out.println(user.avatar);
        if (user == null) {
            return null;
        }

        // MAXIME
        // LISTALL ->FIND A LA MAIN

        if (password.length() > 0) {
            user.password = password;
        }
        if (displayName.length() > 0) {
            user.displayName = displayName;
        }
        if (avatar.length() > 0) {
            user.avatar = avatar;
        }
        return user;
    }

    @Transactional
    public UserModel GetUser(UUID id) {
        for (UserModel um : listAll()) {
            if (um.id.equals(id)) {
                return um;
            }
        }
        return null;
    }

    @Transactional
    public boolean DeleteUser(UUID id) {
        if (GetUser(id) != null) {
            delete(GetUser(id));
            return true;
        }
        return false;
    }

    @Transactional
    public List<UserModel> listUsers() {
        return listAll();
    }
}