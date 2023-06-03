package de.spricom.zaster.security;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.repository.management.ApplicationUserRepository;
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

    private final ApplicationUserRepository applicationUserRepository;

    public UserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUserEntity applicationUser = applicationUserRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException("No applicationUser present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(applicationUser.getUsername(), applicationUser.getHashedPassword(),
                    getAuthorities(applicationUser));
        }
    }

    private static List<GrantedAuthority> getAuthorities(ApplicationUserEntity applicationUser) {
        return applicationUser.getUserRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }

}
