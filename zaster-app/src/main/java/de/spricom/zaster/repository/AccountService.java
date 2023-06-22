package de.spricom.zaster.repository;

import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;

import java.util.List;

public interface AccountService {
    List<AccountGroupEntity> findAllRootAccountGroups(TenantEntity tenant);

    AccountGroupEntity getAccountGroup(String accountGroupId);

    AccountGroupEntity saveAccountGroup(AccountGroupEntity accountGroup);

    void deleteAccountGroup(String accountGroupId);

    AccountEntity saveAccount(AccountEntity account);

    void deleteAccount(String accountId);
}
