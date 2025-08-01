package fr.epita.assistants.ping.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateMemberRequest {
    public String login;
    public String name;
    public String role;
    public String status;
}