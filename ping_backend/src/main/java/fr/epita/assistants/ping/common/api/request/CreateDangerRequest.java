package fr.epita.assistants.ping.common.api.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateDangerRequest {
    public String place;
    public Integer number;
    public String type;
    public String description;
}
