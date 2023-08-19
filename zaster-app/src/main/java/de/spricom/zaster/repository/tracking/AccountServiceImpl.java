package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.repository.AccountService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private final AccountCurrencyRepository accountCurrencyRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<AccountEntity> findAllRootAccounts(TenantEntity tenant) {
        var accounts = accountRepository.findAccounts(tenant.getId());
        for (AccountEntity account : accounts) {
            if (account.getParent() != null) {
                if (account.getParent().getChildren() == null) {
                    account.getParent().setChildren(
                            new TreeSet<>(Comparator.comparing(AccountEntity::getAccountName)));
                }
                account.getParent().getChildren().add(account);
            }
        }
        return accounts.stream()
                .filter(account -> account.getParent() == null)
                .collect(Collectors.toList());
    }

    @Override
    public AccountEntity getAccount(String accountId) {
        return accountRepository.getReferenceById(accountId);
    }

    @Override
    public AccountEntity saveAccount(AccountEntity account) {
        log.info("saving account: {}", account);
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(String accountId) {
        log.info("deleting account: {}", accountId);
        accountRepository.deleteById(accountId);
    }

    @Override
    public AccountCurrencyEntity saveAccountCurrency(AccountCurrencyEntity accountCurrency) {
        log.info("saving accountCurrency: {}", accountCurrency);
        return accountCurrencyRepository.save(accountCurrency);
    }

    @Override
    public void deleteAccountCurrency(String accountId) {
        log.info("deleting account: {}", accountId);
        accountCurrencyRepository.deleteById(accountId);
    }

    @Override
    public AccountCurrencyEntity getOrCreateAccountCurrency(TenantEntity tenant, String accountCode, String accountName, CurrencyEntity currency) {
        var account = accountRepository.findByTenantIdAndAccountCode(tenant.getId(), accountCode);
        AccountCurrencyEntity accountCurrency = null;
        if (account == null) {
            account = new AccountEntity();
            account.setTenant(tenant);
            account.setAccountCode(accountCode);
            account.setAccountName(accountName);
            account = accountRepository.save(account);
        } else {
            accountCurrency = accountCurrencyRepository.findByAccountIdAndCurrencyId(account.getId(), currency.getId());
        }
        if (accountCurrency == null) {
            accountCurrency = new AccountCurrencyEntity();
            accountCurrency.setAccount(account);
            accountCurrency.setCurrency(currency);
            accountCurrency = accountCurrencyRepository.save(accountCurrency);
        }
        return accountCurrency;
    }
}
