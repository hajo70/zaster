package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountGroupRepository extends JpaRepository<AccountGroupEntity, String> {

    @Query("SELECT g FROM AccountGroupEntity g " +
            "LEFT JOIN FETCH g.parent " +
            "LEFT JOIN FETCH g.accounts " +
            "WHERE g.tenant.id = :tenantId")
    List<AccountGroupEntity> findAccountGroups(@Param("tenantId") String tenantId);
}
