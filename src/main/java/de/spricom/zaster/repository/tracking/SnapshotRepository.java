package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.SnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SnapshotRepository
        extends JpaRepository<SnapshotEntity, String>, JpaSpecificationExecutor<SnapshotEntity> {

    @Query("""
            from SnapshotEntity s
            where s.accountCurrency.account.tenant.id = :tenantId
            """)
    List<SnapshotEntity> findAllByTenant(@Param("tenantId") String tenantId);
}
