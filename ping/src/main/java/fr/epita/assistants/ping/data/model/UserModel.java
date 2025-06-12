package fr.epita.assistants.ping.data.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Column(name = "avatar")
    public String avatar; // varchar(255)

    @Column(name = "display_name")
    public String display_name; // varchar(255)

    @Column(name = "is_admin")
    public Boolean is_admin; // boolean

    @Column(name = "login")
    public String login; // varchar(255)

    @Column(name = "password")
    public String password; // varchar(255)

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

}
