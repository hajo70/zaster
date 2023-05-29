package de.spricom.zaster;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.currency.ZasterCurrency;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.repository.currency.CurrencyRepository;
import de.spricom.zaster.repository.management.TenantRepository;
import de.spricom.zaster.repository.tracking.AccountRepository;
import de.spricom.zaster.repository.tracking.BookingRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

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

    private TenantEntity tenant;

    private Map<String, CurrencyEntity> currencies = new TreeMap<>();
    private Map<String, AccountEntity> accounts = new TreeMap<>();

    @Test
    void testDatabaseSchema() {
        tenant = createTenant();
        var currency = createCurrency();
        var account = createAccount(currency);
        createBooking(account, TrackingDateTime.now(), BigDecimal.valueOf(421234, 4));
        createBooking(account, TrackingDateTime.now(), MAX_AMOUNT);
        createBooking(account, TrackingDateTime.now(), SMALLEST_AMOUNT);
        createBooking(account, TrackingDateTime.now(), MAX_AMOUNT.negate());
        Assertions.assertThatCode(() ->
                        createBooking(account, TrackingDateTime.now(), MAX_AMOUNT.add(SMALLEST_AMOUNT)))
                .isInstanceOf(DataIntegrityViolationException.class);
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

    private BookingEntity createBooking(AccountEntity account, TrackingDateTime ts, BigDecimal amount) {
        var booking = new BookingEntity();
        booking.setAccount(account);
        booking.setBookedAt(ts);
        booking.setAmount(amount);
        booking = bookingRepository.save(booking);
        return booking;
    }
}
