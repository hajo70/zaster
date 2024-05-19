package de.spricom.zaster.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountCurrencyRepository
        extends JpaRepository<AccountCurrency, String>, JpaSpecificationExecutor<AccountCurrency> {
    AccountCurrency findByAccountIdAndCurrencyId(String accountGroupId, String currencyId);
}
