package de.spricom.zaster.repository;

import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;

import java.util.List;

public interface CurrencyService {
    List<CurrencyEntity> findAllCurrencies(TenantEntity tenant);

    CurrencyEntity saveCurrency(CurrencyEntity currency);

    void deleteCurrencyById(String currencyId);

    CurrencyEntity getOrCreateCurrencyByCode(TenantEntity tenant, String currencyCode);
}
