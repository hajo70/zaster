package de.spricom.zaster.services;

import de.spricom.zaster.data.Account;
import de.spricom.zaster.data.Booking;
import de.spricom.zaster.data.Currency;
import de.spricom.zaster.data.Transfer;
import de.spricom.zaster.entities.common.TrackingDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class TrackingDataFactory {

    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final BookingService bookingService;

    public Currency createCurrency(String currencyCode) {
        return currencyService.getOrCreateCurrencyByCode(currencyCode);
    }

    public Account createAccount(String accountCode, String accountName) {
        return createAccount(null, accountCode, accountName);
    }

    public Account createAccount(Account parent, String accountCode, String accountName) {
        var account = new Account();
        account.setParent(parent);
        account.setAccountCode(accountCode);
        account.setAccountName(accountName);
        return accountService.saveAccount(account);
    }

    public Booking createBooking(Account debitor, Account creditor,
                                 BigDecimal amount, Currency currency, String description) {
        return createBooking(description,
                createTransfer(debitor, currency, amount),
                createTransfer(creditor, currency, amount.negate()));
    }

    public Transfer createTransfer(Account account, Currency currency, BigDecimal amount) {
        var accountCurrency = accountService.getOrCreateAccountCurrency(account, currency);
        var transfer = new Transfer();
        transfer.setAccountCurrency(accountCurrency);
        transfer.setTransferredAt(TrackingDateTime.now());
        transfer.setAmount(amount);
        return transfer;
    }

    public Booking createBooking(String description, Transfer... transfers) {
        var booking = new Booking();
        for (Transfer transfer : transfers) {
            if (transfer.getTransferredAt() == null) {
                transfer.setTransferredAt(TrackingDateTime.now());
            }
        }
        booking.setTransfers(Arrays.stream(transfers).collect(Collectors.toSet()));
        booking.setBookedAt(TrackingDateTime.now());
        booking.setDescription(description);
        return bookingService.saveBooking(booking);
    }

    public void createTenantWithBookings() {
        Currency eur = createCurrency("EUR");
        Account parent = createAccount(null, "Banks");
        Account bank1 = createAccount(parent, "111", "Bank 1");
        Account bank2 = createAccount(parent, "222", "Bank 2");
        createBooking(bank1, bank2, BigDecimal.valueOf(25.25), eur, "Booking 1");
        createBooking(bank1, bank2, BigDecimal.valueOf(0.98), eur, "Booking 2");
    }
}
