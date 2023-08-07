package de.spricom.zaster.repository;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;

import java.util.List;

public interface AccountService {
    List<AccountEntity> findAllRootAccounts(TenantEntity tenant);

    AccountEntity getAccount(String accountGroupId);

    AccountEntity saveAccount(AccountEntity accountGroup);

    void deleteAccount(String accountGroupId);

    AccountCurrencyEntity saveAccountCurrency(AccountCurrencyEntity account);

    void deleteAccountCurrency(String accountId);

    AccountCurrencyEntity getOrCreateAccountCurrency(TenantEntity tenant, String accountCode, String accountName, CurrencyEntity currency);
}
