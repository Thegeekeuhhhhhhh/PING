package fr.epita.assistants.ping.common.api.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WaypointResponse {
    public String name;
    public Float lat;
    public Float lng;
    public Integer order;
    public Boolean completed;
    public UUID id;
}