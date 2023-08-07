package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository
        extends JpaRepository<AccountEntity, String>, JpaSpecificationExecutor<AccountEntity> {

    @Query("SELECT g FROM AccountEntity g " +
            "LEFT JOIN FETCH g.parent " +
            "LEFT JOIN FETCH g.currencies " +
            "WHERE g.tenant.id = :tenantId")
    List<AccountEntity> findAccounts(@Param("tenantId") String tenantId);

    AccountEntity findByTenantIdAndAccountCode(String tenantId, String accountCode);
}
