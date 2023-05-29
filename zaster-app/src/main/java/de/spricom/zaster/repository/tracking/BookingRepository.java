package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, String> {
}
