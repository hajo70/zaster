package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
        extends JpaRepository<TransactionEntity, String>, JpaSpecificationExecutor<TransactionEntity> {

    @Query("select case when count(*) > 0 then true else false end" +
            " from TransactionEntity t" +
            " where t.md5 = :md5 and t.id != :accountId")
    boolean existsByTenantAndMd5(@Param("accountId") String accountId, @Param("md5") String md5);
}
