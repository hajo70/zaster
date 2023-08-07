package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferRepository
        extends JpaRepository<TransferEntity, String>, JpaSpecificationExecutor<TransferEntity> {


}
