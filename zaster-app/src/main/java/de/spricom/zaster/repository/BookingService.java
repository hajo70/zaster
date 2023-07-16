package de.spricom.zaster.repository;

import de.spricom.zaster.entities.tracking.TransactionEntity;

public interface BookingService {
    TransactionEntity createTransaction(TransactionEntity tx);
}
