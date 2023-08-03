package de.spricom.zaster.init;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.currency.ZasterCurrency;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.*;
import de.spricom.zaster.repository.currency.CurrencyRepository;
import de.spricom.zaster.repository.management.TenantRepository;
import de.spricom.zaster.repository.tracking.*;
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
    private AccountGroupRepository accountGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    private TenantEntity tenant;

    private final Map<String, CurrencyEntity> currencies = new TreeMap<>();
    private final Map<String, AccountEntity> accounts = new TreeMap<>();

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

    private AccountEntity createAccount(CurrencyEntity currency) {
        var accountGroup = new AccountGroupEntity();
        accountGroup.setTenant(tenant);
        accountGroup.setAccountName("My bank account");
        accountGroup = accountGroupRepository.save(accountGroup);
        var account = new AccountEntity();
        account.setAccountGroup(accountGroup);
        account.setCurrency(currency);
        account = accountRepository.save(account);
        accounts.put(account.getAccountGroup().getAccountName(), account);
        return account;
    }

    private SnapshotEntity createSnapshot(AccountEntity account, TrackingDateTime ts, BigDecimal amount) {
        var snapshot = new SnapshotEntity();
        snapshot.setAccount(account);
        snapshot.setTakenAt(ts);
        snapshot.setBalance(amount);
        snapshot = snapshotRepository.save(snapshot);
        return snapshot;
    }

    private TransactionEntity createTransaction() {
        var transaction = new TransactionEntity();
        transaction.setDescription("Sample transaction");
        transaction.setSubmittedAt(TrackingDateTime.now());
        transaction = transactionRepository.save(transaction);
        AccountEntity account = accounts.values().stream().findAny().get();
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("500.1"));
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("499.98"));
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("0.01"));
        return transaction;
    }

    private BookingEntity createBooking(TransactionEntity transaction, AccountEntity account, TrackingDateTime ts, BigDecimal amount) {
        var booking = new BookingEntity();
        booking.setTransaction(transaction);
        booking.setAccount(account);
        booking.setBookedAt(ts);
        booking.setAmount(amount);
        booking = bookingRepository.save(booking);
        return booking;
    }
}
