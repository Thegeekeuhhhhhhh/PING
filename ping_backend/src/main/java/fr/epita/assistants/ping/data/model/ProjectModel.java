package fr.epita.assistants.ping.data.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "projects_users", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "members")
    public List<UserModel> members;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

}
