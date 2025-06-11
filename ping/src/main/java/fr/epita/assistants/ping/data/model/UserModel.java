package fr.epita.assistants.ping.data.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserModel {
    
    @Column(name = "avatar")
    String avatar; // varchar(255)

    @Column(name = "display_name")
    String display_name; // varchar(255)

    @Column(name = "is_admin")
    boolean is_admin; // boolean

    @Column(name = "login")
    String login; // varchar(255)

    @Column(name = "password")
    String password; // varchar(255)

    @Id
    @Column(name = "id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    UUID id;

    
}
