package fr.epita.assistants.ping.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    public String id;
    public String login;
    public String displayName;
    public Boolean isAdmin;
    public String avatar;
}