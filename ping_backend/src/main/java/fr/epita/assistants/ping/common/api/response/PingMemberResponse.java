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
public class PingMemberResponse {
    public String login;
    public String name;
    public String role;
    public String status;
    public UUID id;
}