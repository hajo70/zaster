package de.spricom.zaster.init;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.SnapshotEntity;
import de.spricom.zaster.entities.tracking.TransferEntity;
import de.spricom.zaster.enums.tracking.CurrencyType;
import de.spricom.zaster.enums.tracking.ZasterCurrency;
import de.spricom.zaster.repository.settings.CurrencyRepository;
import de.spricom.zaster.repository.settings.TenantRepository;
import de.spricom.zaster.repository.tracking.AccountCurrencyRepository;
import de.spricom.zaster.repository.tracking.AccountRepository;
import de.spricom.zaster.repository.tracking.BookingRepository;
import de.spricom.zaster.repository.tracking.SnapshotRepository;
import de.spricom.zaster.repository.tracking.TransferRepository;
import de.spricom.zaster.util.RemoteInMemConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@SpringBootTest
@ActiveProfiles("testdb")
@Import(RemoteInMemConfig.class)
public class DatabaseSetupTest {
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9".repeat(25) + "." + "9".repeat(15));
    private static final BigDecimal SMALLEST_AMOUNT = new BigDecimal("0." + "0".repeat(14) + "1");

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountCurrencyRepository accountCurrencyRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    private TenantEntity tenant;

    private final Map<String, CurrencyEntity> currencies = new TreeMap<>();
    private final Map<String, AccountCurrencyEntity> accounts = new TreeMap<>();

    @AfterEach
    void shutDown() {
        System.out.println("shutting down...");
    }

    @Test
    void testDatabaseSchema() {
        tenant = createTenant();
        var currency = createCurrency();
        var account = createAccount(currency);
        createSnapshot(account, TrackingDateTime.now(), BigDecimal.valueOf(421234, 4));
        createSnapshot(account, TrackingDateTime.now(), MAX_AMOUNT);
        createSnapshot(account, TrackingDateTime.now(), SMALLEST_AMOUNT);
        createSnapshot(account, TrackingDateTime.now(), MAX_AMOUNT.negate());
        Assertions.assertThatCode(() ->
                        createSnapshot(account, TrackingDateTime.now(), MAX_AMOUNT.add(SMALLEST_AMOUNT)))
                .isInstanceOf(DataIntegrityViolationException.class);
        createTransaction();
    }

    private TenantEntity createTenant() {
        var tenant = new TenantEntity();
        return tenantRepository.save(tenant);
    }

    private CurrencyEntity createCurrency() {
        var currency = new CurrencyEntity();
        currency.setTenant(tenant);
        currency.setCurrencyCode("EUR");
        currency.setCurrencyName("Euro");
        currency.setCurrencyType(CurrencyType.FIAT);
        currency.setZasterCurrency(ZasterCurrency.EUR);
        currency = currencyRepository.save(currency);
        currencies.put(currency.getCurrencyCode(), currency);
        return currency;
    }

    private AccountCurrencyEntity createAccount(CurrencyEntity currency) {
        var accountGroup = new AccountEntity();
        accountGroup.setTenant(tenant);
        accountGroup.setAccountName("My bank account");
        accountGroup = accountRepository.save(accountGroup);
        var account = new AccountCurrencyEntity();
        account.setAccount(accountGroup);
        account.setCurrency(currency);
        account = accountCurrencyRepository.save(account);
        accounts.put(account.getAccount().getAccountName(), account);
        return account;
    }

    private SnapshotEntity createSnapshot(AccountCurrencyEntity account, TrackingDateTime ts, BigDecimal amount) {
        var snapshot = new SnapshotEntity();
        snapshot.setAccountCurrency(account);
        snapshot.setTakenAt(ts);
        snapshot.setBalance(amount);
        snapshot = snapshotRepository.save(snapshot);
        return snapshot;
    }

    private BookingEntity createTransaction() {
        var transaction = new BookingEntity();
        transaction.setDescription("Sample transaction");
        transaction.setBookedAt(TrackingDateTime.now());
        transaction = bookingRepository.save(transaction);
        AccountCurrencyEntity account = accounts.values().stream().findAny().get();
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("500.1"));
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("499.98"));
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("0.01"));
        return transaction;
    }

    private TransferEntity createBooking(BookingEntity transaction, AccountCurrencyEntity account, TrackingDateTime ts, BigDecimal amount) {
        var booking = new TransferEntity();
        booking.setBooking(transaction);
        booking.setAccountCurrency(account);
        booking.setTransferredAt(ts);
        booking.setAmount(amount);
        booking = transferRepository.save(booking);
        return booking;
    }
}
