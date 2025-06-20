package fr.epita.assistants.ping.data.model;

import java.util.UUID;

import fr.epita.assistants.ping.domain.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

import io.vertx.mutiny.ext.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModel {

    @Column(name = "name")
    public String name; // varchar(255)

    @Column(name = "path")
    public String path; // varchar(255)

    @ManyToOne
    @JoinColumn(name = "owner_id")
    public UserModel owner; // uuid (Foreign key)

    @OneToMany
    @Column(name = "members")
    public List<UserModel> members;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

}
