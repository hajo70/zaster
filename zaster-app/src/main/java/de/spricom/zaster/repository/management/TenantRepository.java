package de.spricom.zaster.repository.management;

import de.spricom.zaster.entities.management.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<TenantEntity, String> {
}
