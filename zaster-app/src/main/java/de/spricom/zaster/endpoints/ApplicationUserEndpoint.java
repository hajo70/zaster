package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.entities.managment.UserEntity;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Endpoint
@AnonymousAllowed
public class ApplicationUserEndpoint {

    @Autowired
    private AuthenticatedUser authenticatedUser;

    public Optional<UserEntity> getAuthenticatedUser() {
        return authenticatedUser.get();
    }
}
