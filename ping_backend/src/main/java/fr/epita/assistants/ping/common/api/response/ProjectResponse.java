package fr.epita.assistants.ping.common.api.response;

import java.util.ArrayList;
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
public class ProjectResponse {
    public UUID id;
    public String name;
    public List<MemberResponse> members;
    public MemberResponse owner;
}