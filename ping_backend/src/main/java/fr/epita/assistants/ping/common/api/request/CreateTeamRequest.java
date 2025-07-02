package fr.epita.assistants.ping.common.api.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateTeamRequest {
    public String name;
    public String color;
    public String status;
    public List<UUID> lm;
    public List<UUID> lw;
}