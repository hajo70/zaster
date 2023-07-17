package de.spricom.zaster.repository;

import de.spricom.zaster.entities.tracking.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface BookingService {
    TransactionEntity createTransaction(TransactionEntity tx);

    record BookingRecord(
        LocalDate submittedAt,
        LocalDate bookedAt,
        String partnerCode,
        String partnerName,
        String description,
        BigDecimal amount,
        Map<String, String> details,
        String md5
    ) {}
}
