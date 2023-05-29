package de.spricom.zaster;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.currency.ZasterCurrency;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.SnapshotEntity;
import de.spricom.zaster.entities.tracking.TransactionEntity;
import de.spricom.zaster.repository.currency.CurrencyRepository;
import de.spricom.zaster.repository.management.TenantRepository;
import de.spricom.zaster.repository.tracking.AccountRepository;
import de.spricom.zaster.repository.tracking.BookingRepository;
import de.spricom.zaster.repository.tracking.SnapshotRepository;
import de.spricom.zaster.repository.tracking.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("initdb")
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
    private BookingRepository bookingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    private TenantEntity tenant;

    private Map<String, CurrencyEntity> currencies = new TreeMap<>();
    private Map<String, AccountEntity> accounts = new TreeMap<>();

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
        var account = new AccountEntity();
        account.setTenant(tenant);
        account.setCurrency(currency);
        account.setAccountName("Postbank Girokonto");
        account = accountRepository.save(account);
        accounts.put(account.getAccountName(), account);
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
        createBooking(transaction, accounts.get("Postbank Girokonto"), TrackingDateTime.now(), new BigDecimal(500.1));
        createBooking(transaction, accounts.get("Postbank Girokonto"), TrackingDateTime.now(), new BigDecimal(499.98));
        createBooking(transaction, accounts.get("Postbank Girokonto"), TrackingDateTime.now(), new BigDecimal(0.01));
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
