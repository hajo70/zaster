package de.spricom.zaster.repository;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.ImportEntity;
import de.spricom.zaster.entities.tracking.TransactionEntity;

import java.math.BigDecimal;
import java.util.Map;

public interface BookingService {
    TransactionEntity createTransaction(TransactionEntity tx);

    boolean addTransaction(ImportEntity imported, AccountEntity account, BookingRecord bookingRecord);
    boolean addSnapshot(ImportEntity imported, AccountEntity account, SnapshotRecord snapshotRecord);

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
