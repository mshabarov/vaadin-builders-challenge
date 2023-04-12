package org.teamhq.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.teamhq.data.Role;
import org.teamhq.data.entity.User;
import org.teamhq.data.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        username = username.trim();
        if (!username.endsWith("@vaadin.com")) {
            throw new UsernameNotFoundException("No Vaadiner present with username: " + username);
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.save(createNewUser(username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(),
                getAuthorities(user));
    }

    private User createNewUser(String username) {
        User newUser = new User();
        newUser.setName(username.replace("@vaadin.com", ""));
        newUser.setUsername(username);
        newUser.setEmail(username);
        if(newUser.getName().equals("annakata")) {
            newUser.setRoles(Set.of(Role.ADMIN, Role.USER));
        } else {
            newUser.setRoles(Set.of(Role.USER));
        }
        newUser.setHashedPassword(passwordEncoder.encode("12345"));
        return newUser;
    }

    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }

}
