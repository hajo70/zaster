package de.spricom.zaster.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.repository.SettingsService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthenticatedUser {

    private final SettingsService settingsService;
    private final AuthenticationContext authenticationContext;

    public Optional<UserEntity> get() {
        return authenticationContext.getAuthenticatedUser(Jwt.class)
                .flatMap(userDetails -> settingsService.findByUsername(userDetails.getSubject()));
    }

    public void logout() {
        authenticationContext.logout();
    }

    public TenantEntity getCurrentTenant() {
        return get().map(UserEntity::getTenant)
                .orElseThrow(() -> new IllegalStateException("Not logged in"));
    }
}
