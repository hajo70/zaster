package de.spricom.zaster.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.repository.management.ApplicationUserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final ApplicationUserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, ApplicationUserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    public Optional<ApplicationUserEntity> get() {
        return authenticationContext.getAuthenticatedUser(Jwt.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getSubject()));
    }

    public void logout() {
        authenticationContext.logout();
    }

    public TenantEntity getCurrentTenant() {
        return get().map(ApplicationUserEntity::getTenant)
                .orElseThrow(() -> new IllegalStateException("Not logged in"));
    }
}
