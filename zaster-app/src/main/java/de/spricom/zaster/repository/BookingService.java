package de.spricom.zaster.repository;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.ImportEntity;
import de.spricom.zaster.entities.tracking.TransferEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BookingService {
    boolean addBooking(ImportEntity imported, AccountCurrencyEntity account, BookingRecord bookingRecord);
    boolean addSnapshot(ImportEntity imported, AccountCurrencyEntity account, SnapshotRecord snapshotRecord);

    List<BookingEntity> loadAllBookings(TenantEntity tenant);

    BookingEntity loadBooking(String bookingId);

    BookingEntity saveBooking(BookingEntity booking);

    void deleteBooking(BookingEntity booking);

    TransferEntity saveTransfer(TransferEntity transfer);

    void deleteTransfer(TransferEntity transfer);

    record BookingRecord(
        TrackingDateTime submittedAt,
        TrackingDateTime bookedAt,
        String partnerCode,
        String partnerName,
        String description,
        BigDecimal amount,
        Map<String, String> details,
        String md5
    ) {}

    record SnapshotRecord(
            TrackingDateTime takenAt,
            BigDecimal balance
    ) {}
}
