package fr.epita.assistants.ping.domain.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.Stack;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import fr.epita.assistants.ping.data.repository.MemberRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.common.api.response.GetFileResponse;
import fr.epita.assistants.ping.data.model.MemberModel;
import fr.epita.assistants.ping.data.model.UserModel;

import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class MemberService {
    @Inject
    MemberRepository memberRepository;

    public List<MemberModel> getMembers() {
        return memberRepository.getMembers();
    }

    public MemberModel getMember(UUID id) {
        return memberRepository.getMember(id);
    }
    public MemberModel getMember_by_login(String login) {
        return memberRepository.getMember_by_login(login);
    }

    public MemberModel addMember(String login, String name, String role, String status) {
        return memberRepository.addMember(login, name, role, status);
        /*
         * for (UserModel temp : userRepository.listUsers()) {
         * if (temp.id.equals(owner.id)) {
         * var tmp = memberRepository.addMember(name, temp, path);
         * createDirectory(tmp.id);
         * return tmp;
         * }
         * }
         * return null;
         */
    }

    public void deleteMember(UUID id) {
        memberRepository.deleteMember(id);
    }
}
