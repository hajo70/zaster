package de.spricom.zaster.init;

import de.spricom.zaster.data.*;
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

    private final Map<String, Currency> currencies = new TreeMap<>();
    private final Map<String, AccountCurrency> accounts = new TreeMap<>();

    @AfterEach
    void shutDown() {
        System.out.println("shutting down...");
    }

    @Test
    void testDatabaseSchema() {
        var currency = createCurrency();
        var account = createAccount(currency);
        createSnapshot(account, TrackingDateTime.now(), BigDecimal.valueOf(421234, 4));
        createSnapshot(account, TrackingDateTime.now(), MAX_AMOUNT);
        createSnapshot(account, TrackingDateTime.now(), SMALLEST_AMOUNT);
        createSnapshot(account, TrackingDateTime.now(), MAX_AMOUNT.negate());
        Assertions
                .assertThatCode(() -> createSnapshot(account, TrackingDateTime.now(), MAX_AMOUNT.add(SMALLEST_AMOUNT)))
                .isInstanceOf(DataIntegrityViolationException.class);
        createTransaction();
    }

    private Currency createCurrency() {
        var currency = new Currency();
        currency.setCurrencyCode("EUR");
        currency.setCurrencyName("Euro");
        currency.setCurrencyType(CurrencyType.FIAT);
        currency.setZasterCurrency(ZasterCurrency.EUR);
        currency = currencyRepository.save(currency);
        currencies.put(currency.getCurrencyCode(), currency);
        return currency;
    }

    private AccountCurrency createAccount(Currency currency) {
        var accountGroup = new Account();
        accountGroup.setAccountName("My bank account");
        accountGroup = accountRepository.save(accountGroup);
        var account = new AccountCurrency();
        account.setAccount(accountGroup);
        account.setCurrency(currency);
        account = accountCurrencyRepository.save(account);
        accounts.put(account.getAccount().getAccountName(), account);
        return account;
    }

    private Snapshot createSnapshot(AccountCurrency account, TrackingDateTime ts, BigDecimal amount) {
        var snapshot = new Snapshot();
        snapshot.setAccountCurrency(account);
        snapshot.setTakenAt(ts);
        snapshot.setBalance(amount);
        snapshot = snapshotRepository.save(snapshot);
        return snapshot;
    }

    private Booking createTransaction() {
        var transaction = new Booking();
        transaction.setDescription("Sample transaction");
        transaction.setBookedAt(TrackingDateTime.now());
        transaction = bookingRepository.save(transaction);
        AccountCurrency account = accounts.values().stream().findAny().get();
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("500.1"));
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("499.98"));
        createBooking(transaction, account, TrackingDateTime.now(), new BigDecimal("0.01"));
        return transaction;
    }

    private Transfer createBooking(Booking transaction, AccountCurrency account, TrackingDateTime ts,
            BigDecimal amount) {
        var booking = new Transfer();
        booking.setBooking(transaction);
        booking.setAccountCurrency(account);
        booking.setTransferredAt(ts);
        booking.setAmount(amount);
        booking = transferRepository.save(booking);
        return booking;
    }
}
