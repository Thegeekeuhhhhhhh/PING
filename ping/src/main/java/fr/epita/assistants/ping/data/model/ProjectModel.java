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
@Table(name = "projects")
@Getter
@Setter
public class ProjectModel {

    @Column(name = "name")
    String name; // varchar(255)


    @Column(name = "path")
    String path; // varchar(255)


    @Column(name = "owner_id")
    UserModel owner_id; // uuid (Foreign key)

    @Id
    @Column(name = "id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    UUID id;

}
