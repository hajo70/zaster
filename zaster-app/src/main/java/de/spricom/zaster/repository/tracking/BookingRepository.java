package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingRepository
        extends JpaRepository<BookingEntity, String>, JpaSpecificationExecutor<BookingEntity> {


}
