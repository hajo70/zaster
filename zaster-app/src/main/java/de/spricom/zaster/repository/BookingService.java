package de.spricom.zaster.repository;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.ImportEntity;

import java.math.BigDecimal;
import java.util.Map;

public interface BookingService {
    BookingEntity createTransaction(BookingEntity tx);

    boolean addTransaction(ImportEntity imported, AccountCurrencyEntity account, BookingRecord bookingRecord);
    boolean addSnapshot(ImportEntity imported, AccountCurrencyEntity account, SnapshotRecord snapshotRecord);

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
