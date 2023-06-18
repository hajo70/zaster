package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Endpoint
@AnonymousAllowed
public class AccountEndpoint {

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    private AccountService accountService;

    public List<AccountGroup> findAllRootAccountGroups() {
        return accountService.findAllRootAccountGroups(authenticatedUser.getCurrentTenant())
                .stream().map(this::createAccountGroup)
                .toList();
    }

    private AccountGroup createAccountGroup(AccountGroupEntity entity) {
        return new AccountGroup(entity,
                Optional.ofNullable(entity.getParent()).map(AccountGroupEntity::getId).orElse(null),
                entity.getChildren().stream().map(this::createAccountGroup).toList());
    }
}
