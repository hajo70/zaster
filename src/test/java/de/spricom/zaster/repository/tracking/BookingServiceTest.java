package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.TransferEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import de.spricom.zaster.repository.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class BookingServiceTest {

    @Autowired
    private TrackingDataFactory trackingDataFactory;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BookingService bookingService;

    private UserEntity user;

    @BeforeEach
    void init() {
        user = trackingDataFactory.createTenantWithBookings();
    }

    @Test
    void dump() {
        List<BookingEntity> bookings = bookingService.loadAllBookings(user.getTenant());
        for (BookingEntity booking : bookings) {
            System.out.println(booking);
            booking.getTransfers().forEach(System.out::println);
        }
    }

    @Test
    void testBookingsHaveTransfers() {
        List<BookingEntity> bookings = bookingService.loadAllBookings(user.getTenant());
        assertThat(bookings).isNotEmpty();
        for (BookingEntity booking : bookings) {
            assertThat(booking.getTransfers()).as("booking: " + booking.getDescription()).isNotEmpty();
        }
    }

    @Test
    void testRemoveTransfers() {
        var roots = accountService.findAllRootAccounts(user.getTenant());
        var parent = roots.get(0);
        var banks = parent.getChildren().stream().toList();
        var fees = trackingDataFactory.createAccount(parent, null, "Fees");
        var eur = currencyService.getOrCreateCurrencyByCode(user.getTenant(), "EUR");
        var usd = currencyService.getOrCreateCurrencyByCode(user.getTenant(), "USD");
        var booking = trackingDataFactory.createBooking(
                "Transfer with different currencies and fee",
                trackingDataFactory.createTransfer(banks.get(0), eur, BigDecimal.valueOf(50.0)),
                trackingDataFactory.createTransfer(banks.get(1), usd, BigDecimal.valueOf(55.10)),
                trackingDataFactory.createTransfer(fees, usd, BigDecimal.valueOf(0.97))
        );
        assertThat(bookingService.loadBooking(booking.getId()).getTransfers()).hasSize(3);

        TransferEntity fee = booking.getTransfers().stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ONE) < 0)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("fee not found"));
        bookingService.deleteTransfer(fee);

        booking = bookingService.loadBooking(booking.getId());
        assertThat(booking.getTransfers()).hasSize(2);

        booking.getTransfers().add(trackingDataFactory.createTransfer(fees, eur, BigDecimal.valueOf(0.20)));
        booking = bookingService.saveBooking(booking);
        assertThat(bookingService.loadBooking(booking.getId()).getTransfers()).hasSize(3);
    }
}
