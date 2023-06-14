package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements de.spricom.zaster.repository.AccountsService {
    private final AccountRepository accountRepository;
    private final AccountGroupRepository accountGroupRepository;

    @Override
    public List<AccountGroupEntity> findAllRootAccountGroups(TenantEntity tenant) {
        var groups = accountGroupRepository.findAll();
        return groups;
    }

    @Override
    public AccountGroupEntity saveAccountGroup(AccountGroupEntity accountGroup) {
        return accountGroupRepository.save(accountGroup);
    }

    @Override
    public void deleteAccountGroup(String accountGroupId) {
        accountGroupRepository.deleteById(accountGroupId);
    }

    @Override
    public AccountEntity saveAccount(AccountEntity account) {
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(String accountId) {
        accountRepository.deleteById(accountId);
    }
}
