package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.TransferEntity;
import de.spricom.zaster.enums.settings.UserRole;
import de.spricom.zaster.enums.tracking.CurrencyType;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.repository.SettingsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
@Transactional
public class TrackingDataFactory {

    private final SettingsService settingsService;
    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final BookingService bookingService;

    public UserEntity createUserWithTenant() {
        int no = ThreadLocalRandom.current().nextInt(1000000);
        var tenant = new TenantEntity();
        tenant.setName("Tenant " + tenant);
        tenant.setLocale(Locale.US);
        tenant.setTimezone(ZoneId.of("US/Hawaii"));
        var user = new UserEntity();
        user.setTenant(tenant);
        user.setUsername("user" + no);
        user.setName("Test User " + no);
        user.setHashedPassword("XXX");
        user.setUserRoles(EnumSet.of(UserRole.ADMIN, UserRole.USER));
        return settingsService.createTenant(user);
    }

    public CurrencyEntity createCurrency(TenantEntity tenant, String currencyCode) {
        var currency = new CurrencyEntity();
        currency.setTenant(tenant);
        currency.setCurrencyCode(currencyCode);
        currency.setCurrencyName(Currency.getInstance(currencyCode).getDisplayName(tenant.getLocale()));
        currency.setCurrencyType(CurrencyType.ISO_4217);
        return currencyService.saveCurrency(currency);
    }

    public AccountEntity createAccount(TenantEntity tenant, String accountCode, String accountName) {
        return createAccount(tenant, null, accountCode, accountName);
    }

    public AccountEntity createAccount(AccountEntity parent, String accountCode, String accountName) {
        return createAccount(parent.getTenant(), parent, accountCode, accountName);
    }

    private AccountEntity createAccount(TenantEntity tenant, AccountEntity parent, String accountCode, String accountName) {
        var account = new AccountEntity();
        account.setTenant(tenant);
        account.setParent(parent);
        account.setAccountCode(accountCode);
        account.setAccountName(accountName);
        return accountService.saveAccount(account);
    }

    public BookingEntity createBooking(AccountEntity debitor, AccountEntity creditor,
                                       BigDecimal amount, CurrencyEntity currency, String description) {
        var debitorAccountCurrency = accountService.getOrCreateAccountCurrency(debitor, currency);
        var creditorAccountCurrency = accountService.getOrCreateAccountCurrency(creditor, currency);
        var debitorTransfer = new TransferEntity();
        debitorTransfer.setAccountCurrency(debitorAccountCurrency);
        debitorTransfer.setTransferredAt(TrackingDateTime.now());
        debitorTransfer.setAmount(amount.negate());
        var creditorTransfer = new TransferEntity();
        creditorTransfer.setAccountCurrency(creditorAccountCurrency);
        creditorTransfer.setTransferredAt(TrackingDateTime.now());
        creditorTransfer.setAmount(amount);
        var booking = new BookingEntity();
        booking.setTransfers(Set.of(debitorTransfer, creditorTransfer));
        booking.setBookedAt(TrackingDateTime.now());
        booking.setDescription(description);
        return bookingService.saveBooking(booking);
    }

    public UserEntity createTenantWithBookings() {
        UserEntity user = createUserWithTenant();
        CurrencyEntity eur = createCurrency(user.getTenant(), "EUR");
        CurrencyEntity usd = createCurrency(user.getTenant(), "USD");
        AccountEntity bank1 = createAccount(user.getTenant(), "111", "Bank 1");
        AccountEntity bank2 = createAccount(user.getTenant(), "222", "Bank 2");
        createBooking(bank1, bank2, BigDecimal.valueOf(25.25), eur, "Booking 1");
        createBooking(bank1, bank2, BigDecimal.valueOf(0.98), eur, "Booking 2");
        return user;
    }
}
