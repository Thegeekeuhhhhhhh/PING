package fr.epita.assistants.ping.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JWTResponse {
    public UUID sub;
    public Boolean groups;
    public Date iat;
    public Date exp;
}