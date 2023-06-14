package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.repository.AccountsService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.repository.management.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Currency;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private TenantRepository tenantRepository;

    private TenantEntity tenant;
    private Map<String, CurrencyEntity> currencies;

    @BeforeEach
    void init(TestInfo testInfo) {
        TenantEntity tenant = new TenantEntity();
        tenant.setName(testInfo.getDisplayName());
        this.tenant = tenantRepository.save(tenant);
        currencies = Stream.of("EUR", "USD")
                .map(this::createCurrency)
                .collect(Collectors.toMap(CurrencyEntity::getCurrencyCode, c -> c));
    }

    private CurrencyEntity createCurrency(String currencyCode) {
        var currency = Currency.getInstance(currencyCode);
        var entity = new CurrencyEntity();
        entity.setTenant(tenant);
        entity.setCurrencyCode(currency.getCurrencyCode());
        entity.setCurrencyName(currency.getDisplayName());
        entity.setCurrencyType(CurrencyType.FIAT);
        return currencyService.saveCurrency(entity);
    }
    @Test
    void testAccountGroups() {
        var root1 = createAccountGroup(null, "Root 1");
        var root2 = createAccountGroup(null, "Root 2");
        var main = createAccountGroup(root1, "Main");
        var sub1 = createAccountGroup(main, "Sub 1");
        var sub2 = createAccountGroup(main, "Sub 2");
        var leaf1 = createAccountGroup(sub2, "Leaf 1");
        var leaf1eur = createAccount(leaf1, "EUR");
        var leaf1usd = createAccount(leaf1, "USD");
        var sub1eur = createAccount(sub1, "EUR");
    }

    private AccountGroupEntity createAccountGroup(AccountGroupEntity parent, String accountName) {
        var group = new AccountGroupEntity();
        group.setTenant(tenant);
        group.setParent(parent);
        group.setAccountName(accountName);
        return accountsService.saveAccountGroup(group);
    }

    private AccountEntity createAccount(AccountGroupEntity group, String currencyCode) {
        var account = new AccountEntity();
        account.setAccountGroup(group);
        account.setCurrency(currencies.get(currencyCode));
        return accountsService.saveAccount(account);
    }
}