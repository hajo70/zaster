package de.spricom.zaster.repository.settings;

import de.spricom.zaster.entities.settings.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<TenantEntity, String> {
}
