package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.tracking.AccountDto;
import de.spricom.zaster.dtos.tracking.AccountGroupDto;
import de.spricom.zaster.dtos.tracking.AccountingDataDto;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Endpoint
@PermitAll
@Log4j2
@AllArgsConstructor
public class AccountingEndpoint {

    private final AuthenticatedUser authenticatedUser;
    private final AccountService accountService;
    private final CurrencyService currencyService;

    public AccountingDataDto getAccountingData() {
        log.info("Loading accounting data...");
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

    private AccountGroupDto createAccountGroup(AccountEntity entity) {
        return new AccountGroupDto(DtoUtils.id(entity),
                entity.getAccountName(),
                entity.getCurrencies() == null || entity.getCurrencies().isEmpty()
                        ? null
                        : entity.getCurrencies().stream()
                        .map(this::createAccount)
                        .toList(),
        Optional.ofNullable(entity.getParent()).map(AccountEntity::getId).orElse(null),
                entity.getChildren() == null || entity.getChildren().isEmpty()
                        ? null
                        : entity.getChildren().stream()
                        .map(this::createAccountGroup)
                        .toList());
    }

    private AccountDto createAccount(AccountCurrencyEntity entity) {
        return new AccountDto(DtoUtils.id(entity), entity.getCurrency().getId());
    }

    public AccountGroupDto saveAccountGroup(AccountGroupDto group) {
        AccountEntity entity = new AccountEntity();
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
