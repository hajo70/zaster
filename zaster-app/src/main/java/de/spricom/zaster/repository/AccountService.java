package de.spricom.zaster.repository;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;

import java.util.List;

public interface AccountService {
    List<AccountEntity> findAllRootAccountGroups(TenantEntity tenant);

    AccountEntity getAccountGroup(String accountGroupId);

    AccountEntity saveAccountGroup(AccountEntity accountGroup);

    void deleteAccountGroup(String accountGroupId);

    AccountCurrencyEntity saveAccount(AccountCurrencyEntity account);

    void deleteAccount(String accountId);

    AccountCurrencyEntity getOrCreateAccount(TenantEntity tenant, String accountCode, String accountName, CurrencyEntity currency);
}
