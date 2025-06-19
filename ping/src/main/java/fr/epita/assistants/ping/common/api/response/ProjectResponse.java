package fr.epita.assistants.ping.common.api.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectResponse {
    public String name;
    public ArrayList<MemberResponse> members;
    public MemberResponse owner;
}