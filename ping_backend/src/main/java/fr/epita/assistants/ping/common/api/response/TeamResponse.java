package fr.epita.assistants.ping.common.api.response;

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
public class TeamResponse {
    public String name;
    public String color;
    public String status;
    public List<PingMemberResponse> lm;
    public List<WaypointResponse> lw;
    public UUID id;
}