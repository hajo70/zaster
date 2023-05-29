package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
}
