package de.spricom.zaster.repository;

import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<AccountEntity> findAllRootAccounts(TenantEntity tenant);

    Optional<AccountEntity> findAccountByCode(TenantEntity tenant, String accountCode);

    AccountEntity getAccount(String accountGroupId);

    AccountEntity saveAccount(AccountEntity accountGroup);

    void deleteAccount(String accountGroupId);

    AccountCurrencyEntity saveAccountCurrency(AccountCurrencyEntity account);

    void deleteAccountCurrency(String accountId);

    AccountCurrencyEntity getOrCreateAccountCurrency(TenantEntity tenant, String accountCode, String accountName, CurrencyEntity currency);

    AccountCurrencyEntity getOrCreateAccountCurrency(AccountEntity account, CurrencyEntity currency);
}
