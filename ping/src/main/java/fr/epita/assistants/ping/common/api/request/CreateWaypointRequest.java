package fr.epita.assistants.ping.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateWaypointRequest {
    public String name;
    public Float lat;
    public Float lng;
    public Integer order;
    public Boolean completed;
}