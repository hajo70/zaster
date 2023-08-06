package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
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
    public List<AccountEntity> findAllRootAccountGroups(TenantEntity tenant) {
        var groups = accountRepository.findAccountGroups(tenant.getId());
        for (AccountEntity group : groups) {
            if (group.getParent() != null) {
                if (group.getParent().getChildren() == null) {
                    group.getParent().setChildren(
                            new TreeSet<>(Comparator.comparing(AccountEntity::getAccountName)));
                }
                group.getParent().getChildren().add(group);
            }
        }
        return groups.stream()
                .filter(group -> group.getParent() == null)
                .collect(Collectors.toList());
    }

    @Override
    public AccountEntity getAccountGroup(String accountGroupId) {
        return accountRepository.getReferenceById(accountGroupId);
    }

    @Override
    public AccountEntity saveAccountGroup(AccountEntity accountGroup) {
        log.info("saving account group: {}", accountGroup);
        return accountRepository.save(accountGroup);
    }

    @Override
    public void deleteAccountGroup(String accountGroupId) {
        log.info("deleting account group: {}", accountGroupId);
        accountRepository.deleteById(accountGroupId);
    }

    @Override
    public AccountCurrencyEntity saveAccount(AccountCurrencyEntity account) {
        log.info("saving account: {}", account);
        return accountCurrencyRepository.save(account);
    }

    @Override
    public void deleteAccount(String accountId) {
        log.info("deleting account: {}", accountId);
        accountCurrencyRepository.deleteById(accountId);
    }

    @Override
    public AccountCurrencyEntity getOrCreateAccount(TenantEntity tenant, String accountCode, String accountName, CurrencyEntity currency) {
        var group = accountRepository.findByTenantIdAndAccountCode(tenant.getId(), accountCode);
        AccountCurrencyEntity account = null;
        if (group == null) {
            group = new AccountEntity();
            group.setTenant(tenant);
            group.setAccountCode(accountCode);
            group.setAccountName(accountName);
            group = accountRepository.save(group);
        } else {
            account = accountCurrencyRepository.findByAccountIdAndCurrencyId(group.getId(), currency.getId());
        }
        if (account == null) {
            account = new AccountCurrencyEntity();
            account.setAccount(group);
            account.setCurrency(currency);
            account = accountCurrencyRepository.save(account);
        }
        return account;
    }
}
