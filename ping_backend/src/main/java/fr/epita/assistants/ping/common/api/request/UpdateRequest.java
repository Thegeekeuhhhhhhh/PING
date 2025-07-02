package fr.epita.assistants.ping.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateRequest {
    public String password;
    public String displayName;
    public String avatar;
}