package com.example.ldap.service;

import com.example.ldap.repository.User;
import com.example.ldap.repository.UserRepository;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class UserService {

    private UserRepository userRepository;

    private final LdapTemplate ldapTemplate;

    public UserService (final UserRepository userRepository,
            final LdapTemplate ldapTemplate) {
        this.userRepository = userRepository;
        this.ldapTemplate = ldapTemplate;
    }

    public Boolean authenticate(final User user) {
        User userLoged = userRepository.findByUsernameAndPassword(user.getUsername(), digestSHA(user.getPassword()));
        return userLoged != null;
    }

    /**
     * Retrieves all the persons in the ldap server
     * @return list of person names
     */
    public List<String> getAllPersonNames() {
        return ldapTemplate.search(
                query().where("objectclass").is("groupOfUniqueNames"),
                (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());
    }

    public List<String> search(final String username) {
        List<User> userList = userRepository.findByUsernameLikeIgnoreCase(username);
        if (userList == null) {
            return Collections.emptyList();
        }
        return userList.stream().map(User::getUsername).collect(Collectors.toList());
    }

    public void create(final User user) {
        User newUser = new User(user.getUsername(),digestSHA(user.getPassword()));
        newUser.setId(LdapUtils.emptyLdapName());
        userRepository.save(newUser);
    }

    public void modify(final String username, final String password) {
        User user = userRepository.findByUsername(username);
        user.setPassword(digestSHA(password));
        userRepository.save(user);
    }

    private String digestSHA(final String password) {
        String base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(password.getBytes());
            base64 = Base64.getEncoder()
                    .encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return "{SHA}" + base64;
    }
}
