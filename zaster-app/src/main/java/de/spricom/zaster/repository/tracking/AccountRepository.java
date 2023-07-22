package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository
        extends JpaRepository<AccountEntity, String>, JpaSpecificationExecutor<AccountEntity> {
    AccountEntity findByAccountGroupIdAndCurrencyId(String accountGroupId, String currencyId);
}
