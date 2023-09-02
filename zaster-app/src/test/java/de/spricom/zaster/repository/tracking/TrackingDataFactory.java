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
import java.util.Arrays;
import java.util.Currency;
import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        return createBooking(description,
                createTransfer(debitor, currency, amount),
                createTransfer(creditor, currency, amount.negate()));
    }

    public TransferEntity createTransfer(AccountEntity account, CurrencyEntity currency, BigDecimal amount) {
        var accountCurrency = accountService.getOrCreateAccountCurrency(account, currency);
        var transfer = new TransferEntity();
        transfer.setAccountCurrency(accountCurrency);
        transfer.setTransferredAt(TrackingDateTime.now());
        transfer.setAmount(amount);
        return transfer;
    }

    public BookingEntity createBooking(String description, TransferEntity... transfers) {
        var booking = new BookingEntity();
        for (TransferEntity transfer : transfers) {
            if (transfer.getTransferredAt() == null) {
                transfer.setTransferredAt(TrackingDateTime.now());
            }
        }
        booking.setTransfers(Arrays.stream(transfers).collect(Collectors.toSet()));
        booking.setBookedAt(TrackingDateTime.now());
        booking.setDescription(description);
        return bookingService.saveBooking(booking);
    }

    public UserEntity createTenantWithBookings() {
        UserEntity user = createUserWithTenant();
        TenantEntity tenant = user.getTenant();
        CurrencyEntity eur = createCurrency(tenant, "EUR");
        AccountEntity parent = createAccount(tenant, null, "Banks");
        AccountEntity bank1 = createAccount(parent, "111", "Bank 1");
        AccountEntity bank2 = createAccount(parent, "222", "Bank 2");
        createBooking(bank1, bank2, BigDecimal.valueOf(25.25), eur, "Booking 1");
        createBooking(bank1, bank2, BigDecimal.valueOf(0.98), eur, "Booking 2");
        return user;
    }
}
