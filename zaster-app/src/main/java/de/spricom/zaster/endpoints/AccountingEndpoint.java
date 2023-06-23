package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.dtos.tracking.AccountDto;
import de.spricom.zaster.dtos.tracking.AccountGroupDto;
import de.spricom.zaster.dtos.tracking.AccountingDataDto;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Endpoint
@AnonymousAllowed
public class AccountingEndpoint {

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CurrencyService currencyService;

    public AccountingDataDto getAccountingData() {
        TenantEntity tenant = authenticatedUser.getCurrentTenant();
        return new AccountingDataDto(
            currencyService.findAllCurrencies(tenant),
            findAllRootAccountGroups()
        );
    }

    public List<AccountGroupDto> findAllRootAccountGroups() {
        return accountService.findAllRootAccountGroups(authenticatedUser.getCurrentTenant())
                .stream().map(this::createAccountGroup)
                .toList();
    }

    private AccountGroupDto createAccountGroup(AccountGroupEntity entity) {
        return new AccountGroupDto(DtoUtils.id(entity),
                entity.getAccountName(),
                entity.getAccounts() == null || entity.getAccounts().isEmpty()
                        ? null
                        : entity.getAccounts().stream()
                        .map(this::createAccount)
                        .toList(),
        Optional.ofNullable(entity.getParent()).map(AccountGroupEntity::getId).orElse(null),
                entity.getChildren() == null || entity.getChildren().isEmpty()
                        ? null
                        : entity.getChildren().stream()
                        .map(this::createAccountGroup)
                        .toList());
    }

    private AccountDto createAccount(AccountEntity entity) {
        return new AccountDto(DtoUtils.id(entity), entity.getCurrency().getId());
    }

    public AccountGroupDto saveAccountGroup(AccountGroupDto group) {
        AccountGroupEntity entity = new AccountGroupEntity();
        DtoUtils.setId(entity, group.id());
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
