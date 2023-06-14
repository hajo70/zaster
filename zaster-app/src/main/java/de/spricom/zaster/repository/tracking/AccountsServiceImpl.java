package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements de.spricom.zaster.repository.AccountsService {
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
