package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
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

    private final AccountRepository accountRepository;
    private final AccountGroupRepository accountGroupRepository;

    @Override
    public List<AccountGroupEntity> findAllRootAccountGroups(TenantEntity tenant) {
        var groups = accountGroupRepository.findAccountGroups(tenant.getId());
        for (AccountGroupEntity group : groups) {
            if (group.getParent() != null) {
                if (group.getParent().getChildren() == null) {
                    group.getParent().setChildren(
                            new TreeSet<>(Comparator.comparing(AccountGroupEntity::getAccountName)));
                }
                group.getParent().getChildren().add(group);
            }
        }
        return groups.stream()
                .filter(group -> group.getParent() == null)
                .collect(Collectors.toList());
    }

    @Override
    public AccountGroupEntity getAccountGroup(String accountGroupId) {
        return accountGroupRepository.getReferenceById(accountGroupId);
    }

    @Override
    public AccountGroupEntity saveAccountGroup(AccountGroupEntity accountGroup) {
        log.info("saving account group: {}", accountGroup);
        return accountGroupRepository.save(accountGroup);
    }

    @Override
    public void deleteAccountGroup(String accountGroupId) {
        log.info("deleting account group: {}", accountGroupId);
        accountGroupRepository.deleteById(accountGroupId);
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
    public AccountEntity getOrCreateAccount(TenantEntity tenant, String accountCode, String accountName, CurrencyEntity currency) {
        var group = accountGroupRepository.findByTenantIdAndAccountCode(tenant.getId(), accountCode);
        AccountEntity account = null;
        if (group == null) {
            group = new AccountGroupEntity();
            group.setTenant(tenant);
            group.setAccountCode(accountCode);
            group.setAccountName(accountName);
            group = accountGroupRepository.save(group);
        } else {
            account = accountRepository.findByAccountGroupIdAndCurrencyId(group.getId(), currency.getId());
        }
        if (account == null) {
            account = new AccountEntity();
            account.setAccountGroup(group);
            account.setCurrency(currency);
            account = accountRepository.save(account);
        }
        return account;
    }
}
