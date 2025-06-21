package fr.epita.assistants.ping.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetFileResponse {
    public String name;
    public String path;
    public Boolean isDirectory;
}