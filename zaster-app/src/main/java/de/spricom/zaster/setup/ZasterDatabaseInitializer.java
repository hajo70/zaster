package de.spricom.zaster.setup;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.managment.UserRole;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.repository.currency.CurrencyRepository;
import de.spricom.zaster.repository.management.ApplicationUserRepository;
import de.spricom.zaster.repository.management.TenantRepository;
import de.spricom.zaster.repository.tracking.AccountGroupRepository;
import de.spricom.zaster.repository.tracking.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ZasterDatabaseInitializer implements ApplicationRunner {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private AccountGroupRepository accountGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (tenantRepository.count() == 0) {
            initializeDatabase();
        }
    }

    private void initializeDatabase() {
        var tenant = new TenantEntity();
        tenant.setName("Local Installation");
        tenantRepository.save(tenant);
        createUser(tenant, "applicationUser", "John Normal", UserRole.USER);
        createUser(tenant, "admin", "Emma Powerful", UserRole.USER, UserRole.ADMIN);
        var currencies = createCurrencies(tenant);
        var root = createRootAccountGroup(tenant, "Gesamt");
        createAccount(root, "Bankkonto", currencies.get("EUR"));
        createAccount(root, "Barkasse", currencies.get("EUR"));
        createAccount(root, "Einnahmen", currencies.get("EUR"));
        var group = createAccount(root, "Ausgaben", currencies.get("EUR"));
        createAccount(group, currencies.get("USD"));
        createAccount(group, "Krypto", currencies.get("EUR"));
        createAccount(group, "Eis", currencies.get("EUR"));
    }

    private TenantEntity createTenant() {
        var tenant = new TenantEntity();
        tenant.setName("Local installation");
        return tenantRepository.save(tenant);
    }

    private ApplicationUserEntity createUser(TenantEntity tenant, String username, String name, UserRole... roles) {
        var user = new ApplicationUserEntity();
        user.setTenant(tenant);
        user.setUsername(username);
        user.setName(name);
        user.setHashedPassword(passwordEncoder.encode(username));
        user.setUserRoles(EnumSet.copyOf(Arrays.asList(roles)));
        return applicationUserRepository.save(user);
    }

    private Map<String, CurrencyEntity> createCurrencies(TenantEntity tenant) {
        var currencies = new HashMap<String, CurrencyEntity>();
        Set<Currency> availableCurrencies = Currency.getAvailableCurrencies();
        for (Currency availableCurrency : availableCurrencies) {
            var currency = new CurrencyEntity();
            currency.setTenant(tenant);
            currency.setCurrencyType(CurrencyType.FIAT);
            currency.setCurrencyCode(availableCurrency.getCurrencyCode());
            currency.setCurrencyName(availableCurrency.getDisplayName());
            currencies.put(currency.getCurrencyCode(), currencyRepository.save(currency));
        }
        return currencies;
    }

    private AccountGroupEntity createRootAccountGroup(TenantEntity tenant, String name) {
        var group = new AccountGroupEntity();
        group.setTenant(tenant);
        group.setAccountName(name);
        return accountGroupRepository.save(group);
    }

    private AccountGroupEntity createAccount(AccountGroupEntity parent, String name, CurrencyEntity currency) {
        var group = new AccountGroupEntity();
        group.setTenant(parent.getTenant());
        group.setParent(parent);
        group.setAccountName(name);
        group = accountGroupRepository.save(group);
        createAccount(group, currency);
        return group;
    }

    private AccountEntity createAccount(AccountGroupEntity group, CurrencyEntity currency) {
        var account = new AccountEntity();
        account.setAccountGroup(group);
        account.setCurrency(currency);
        return accountRepository.save(account);
    }
}
