package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.tracking.AccountDto;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Endpoint
@PermitAll
@Log4j2
@AllArgsConstructor
public class AccountingEndpoint {

    private final AuthenticatedUser authenticatedUser;
    private final AccountService accountService;

    public AccountDto saveAccount(AccountDto group) {
        AccountEntity entity = new AccountEntity();
        DtoUtils.setId(entity, group.id());
        entity.setAccountName(group.accountName());
        entity.setAccountCode(group.accountCode());
        entity.setTenant(authenticatedUser.getCurrentTenant());
        if (StringUtils.isNotEmpty(group.parentId())) {
            entity.setParent(accountService.getAccount(group.parentId()));
        }
        return DtoUtils.toAccountDto(accountService.saveAccount(entity));
    }

    public void deleteAccountGroupById(String accountGroupId) {
        accountService.deleteAccount(accountGroupId);
    }
}
