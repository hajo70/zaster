package de.spricom.zaster.repository.importing;

import de.spricom.zaster.entities.tracking.ImportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImportRepository extends JpaRepository<ImportEntity, String>, JpaSpecificationExecutor<ImportEntity> {
}
