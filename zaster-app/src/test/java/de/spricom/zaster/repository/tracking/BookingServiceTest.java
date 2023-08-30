package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.repository.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class BookingServiceTest {

    @Autowired
    private TrackingDataFactory trackingDataFactory;

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
}
