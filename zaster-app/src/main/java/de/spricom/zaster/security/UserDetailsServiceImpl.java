package de.spricom.zaster.security;

import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.repository.management.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity applicationUser = userRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException("No applicationUser present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(applicationUser.getUsername(), applicationUser.getHashedPassword(),
                    getAuthorities(applicationUser));
        }
    }

    private static List<GrantedAuthority> getAuthorities(UserEntity applicationUser) {
        return applicationUser.getUserRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }

}
