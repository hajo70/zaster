package de.spricom.zaster.repository.importing;

import de.spricom.zaster.entities.tracking.FileSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileSourceRepository extends JpaRepository<FileSourceEntity, String>, JpaSpecificationExecutor<FileSourceEntity> {

    @Query("SELECT f FROM FileSourceEntity f " +
            "LEFT JOIN FETCH f.imported i " +
            "WHERE f.md5 = :md5 AND i.tenant.id = :tenantId")
    Optional<FileSourceEntity> findByMd5(@Param("tenantId") String tenantId, @Param("md5") String md5);

}
