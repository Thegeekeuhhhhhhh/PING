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
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamModel {

    @Column(name = "name")
    public String name; // varchar(255)

    @Column(name = "color")
    public String color; // varchar(255)

    @Column(name = "status")
    public String status;

    @Column(name = "members")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "teams_members", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "member_id"))
    public List<MemberModel> members;

    @Column(name = "waypoints")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "teams_waypoints", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "waypoint_id"))
    public List<WaypointModel> waypoints;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
}
