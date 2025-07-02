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
@Table(name = "dangers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DangerModel {

    @Column(name = "place")
    public String place; // varchar(255)

    @Column(name = "description")
    public String description; // varchar(255)

    @Column(name = "number")
    public Integer number;

    @Column(name = "type")
    public String type;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
}
