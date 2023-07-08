package de.spricom.zaster.setup;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.managment.UserRole;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.TransactionEntity;
import de.spricom.zaster.repository.currency.CurrencyRepository;
import de.spricom.zaster.repository.management.ApplicationUserRepository;
import de.spricom.zaster.repository.management.TenantRepository;
import de.spricom.zaster.repository.tracking.AccountGroupRepository;
import de.spricom.zaster.repository.tracking.AccountRepository;
import de.spricom.zaster.repository.tracking.BookingRepository;
import de.spricom.zaster.repository.tracking.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@AllArgsConstructor
public class ZasterDatabaseInitializer implements ApplicationRunner {

    private final TenantRepository tenantRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final CurrencyRepository currencyRepository;
    private final AccountGroupRepository accountGroupRepository;
    private final AccountRepository accountRepository;
    private final BookingRepository bookingRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (tenantRepository.count() == 0) {
            initializeDatabase();
        }
    }

    private void initializeDatabase() {
        var tenant = createTenant();
        createUser(tenant, "user", "John Normal", UserRole.USER);
        createUser(tenant, "admin", "Emma Powerful", UserRole.USER, UserRole.ADMIN);
        var store = new Store(createCurrencies(tenant));
        var root = createRootAccountGroup(tenant, "Gesamt");
        createAccount(root, "Bankkonto", store.getCurrency("EUR"));
        createAccount(root, "Barkasse", store.getCurrency("EUR"), store.getCurrency("USD"));
        createAccount(root, "Einnahmen", store.getCurrency("EUR"));
        var group = createAccount(root, "Ausgaben", store.getCurrency("EUR"), store.getCurrency("USD"));
        createAccount(group, "Krypto", store.getCurrency("EUR"), store.getCurrency("USD"));
        createAccount(group, "Eis", store.getCurrency("EUR"));
        accountGroupRepository.findAccountGroups(tenant.getId()).stream().forEach(store::add);

        createTransaction(store,"Eis essen", "Barkasse", "Eis", "EUR", 6.2);
        createTransaction(store,"Kleines Eis", "Barkasse", "Eis", "EUR", 4.8);
        createTransaction(store,"Umbuchung", "Bankkonto", "Krypto", "EUR", 200);
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

    private AccountGroupEntity createAccount(AccountGroupEntity parent, String name, CurrencyEntity... currencies) {
        var group = new AccountGroupEntity();
        group.setTenant(parent.getTenant());
        group.setParent(parent);
        group.setAccountName(name);
        group = accountGroupRepository.save(group);
        for (CurrencyEntity currency : currencies) {
            createAccount(group, currency);
        }
        return group;
    }

    private AccountEntity createAccount(AccountGroupEntity group, CurrencyEntity currency) {
        var account = new AccountEntity();
        account.setAccountGroup(group);
        account.setCurrency(currency);
        return accountRepository.save(account);
    }

    private void createTransaction(Store store, String description, String from, String to, String currenyCode, double amount) {
        createTransaction(description,
                store.getAccount(from, currenyCode),
                store.getAccount(to, currenyCode),
                BigDecimal.valueOf(amount));
    }

    private void createTransaction(String description, AccountEntity from, AccountEntity to, BigDecimal amount) {
        var tx = new TransactionEntity();
        tx.setSubmittedAt(TrackingDateTime.now());
        tx.setDescription(description);
        tx = transactionRepository.save(tx);
        createBooking(tx, from, amount.negate());
        createBooking(tx, to, amount);
    }

    private BookingEntity createBooking(TransactionEntity tx, AccountEntity account, BigDecimal amount) {
        var booking = new BookingEntity();
        booking.setBookedAt(TrackingDateTime.now());
        booking.setAmount(amount);
        booking.setAccount(account);
        booking.setTransaction(tx);
        return bookingRepository.save(booking);
    }

    static class Store {
        private final Map<String, CurrencyEntity> currencies;
        private final Map<String, AccountGroupEntity> accountGroups = new HashMap<>();

        public Store(Map<String, CurrencyEntity> currencies) {
            this.currencies = currencies;
        }

        void add(AccountGroupEntity accountGroup) {
            accountGroups.put(accountGroup.getAccountName(), accountGroup);
        }

        CurrencyEntity getCurrency(String currencyCode) {
            return currencies.get(currencyCode);
        }

        AccountEntity getAccount(String accountName, String currencyCode) {
            AccountGroupEntity accountGroup = accountGroups.get(accountName);
            return accountGroup.getAccounts().stream()
                    .filter(ac -> currencyCode.equals(ac.getCurrency().getCurrencyCode()))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("There is no "
                            + accountName + "-" + currencyCode + " account!"));
        }
    }
}
