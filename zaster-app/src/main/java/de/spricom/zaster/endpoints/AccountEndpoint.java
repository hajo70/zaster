package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
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
        return new AccountGroup(
                entity.getId(),
                entity.getVersion(),
                entity.getAccountName(),
                Optional.ofNullable(entity.getAccounts()).orElse(Collections.emptySet())
                        .stream()
                        .map(AccountEntity::getCurrency)
                        .map(CurrencyEntity::getCurrencyCode)
                        .toList(),
                Optional.ofNullable(entity.getParent()).map(AccountGroupEntity::getId).orElse(null),
                entity.getChildren() == null || entity.getChildren().isEmpty()
                        ? null
                        : entity.getChildren().stream()
                        .map(this::createAccountGroup)
                        .toList());
    }

    public AccountGroup saveAccountGroup(AccountGroup group) {
        AccountGroupEntity entity = new AccountGroupEntity();
        entity.setId(group.id());
        entity.setVersion(group.version());
        entity.setAccountName(group.accountName());
        entity.setTenant(authenticatedUser.getCurrentTenant());
        if (StringUtils.isNotEmpty(group.parentId())) {
            entity.setParent(accountService.getAccountGroup(group.parentId()));
        }
        return createAccountGroup(accountService.saveAccountGroup(entity));
    }

    public void deleteAccountGroupById(String accountGroupId) {
        accountService.deleteAccountGroup(accountGroupId);
    }
}
