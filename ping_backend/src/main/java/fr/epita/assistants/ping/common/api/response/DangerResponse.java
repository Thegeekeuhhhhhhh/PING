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
public class DangerResponse {
    public UUID id;
    public String location;
    public String type;
    public String description;
}