package de.spricom.zaster.repository.currency;

import de.spricom.zaster.entities.settings.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String>, JpaSpecificationExecutor<CurrencyEntity> {

    List<CurrencyEntity> findAllByTenantId(String tenantId);

    CurrencyEntity findByTenantIdAndCurrencyCode(String tenantId, String currencyCode);
}
