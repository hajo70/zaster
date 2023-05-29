package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.SnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<SnapshotEntity, String> {
}
