package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountCurrencyRepository
        extends JpaRepository<AccountCurrencyEntity, String>, JpaSpecificationExecutor<AccountCurrencyEntity> {
    AccountCurrencyEntity findByAccountIdAndCurrencyId(String accountGroupId, String currencyId);
}
