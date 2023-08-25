package de.spricom.zaster.security;

import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.repository.SettingsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SettingsService settingsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = settingsService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No applicationUser present with username: " + username));
        return new User(user.getUsername(), user.getHashedPassword(), getAuthorities(user));
    }

    private static List<GrantedAuthority> getAuthorities(UserEntity user) {
        return user.getUserRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
