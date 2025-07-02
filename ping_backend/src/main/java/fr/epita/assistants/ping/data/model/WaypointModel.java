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
@Table(name = "waypoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaypointModel {

    @Column(name = "name")
    public String name; // varchar(255)

    @Column(name = "lat")
    public Float lat; // varchar(255)

    @Column(name = "lng")
    public Float lng;

    @Column(name = "ordre") // order interdit
    public Integer order;

    @Column(name = "completed")
    public Boolean completed;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
}
