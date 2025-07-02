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
public class UserResponse {
    public UUID id;
    public String login;
    public String displayName;
    public Boolean isAdmin;
    public String avatar;
}