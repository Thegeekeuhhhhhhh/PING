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
public class ExecuteFeatureRequest {
    public String feature;
    public String command;
    public List<String> params;
}
