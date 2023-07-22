package de.spricom.zaster.repository.currency;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.repository.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public List<CurrencyEntity> findAllCurrencies(TenantEntity tenant) {
        return currencyRepository.findAllByTenantId(tenant.getId());
    }

    @Override
    public CurrencyEntity saveCurrency(CurrencyEntity currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public void deleteCurrencyById(String currencyId) {
        currencyRepository.deleteById(currencyId);
    }

    @Override
    public CurrencyEntity getOrCreateCurrencyByCode(TenantEntity tenant, String currencyCode) {
        var currency = currencyRepository.findByTenantIdAndCurrencyCode(tenant.getId(), currencyCode);
        if (currency == null) {
            var isoCurrency = Currency.getInstance(currencyCode);
            currency = new CurrencyEntity();
            currency.setTenant(tenant);
            currency.setCurrencyType(CurrencyType.FIAT);
            currency.setCurrencyCode(isoCurrency.getCurrencyCode());
            currency.setCurrencyName(isoCurrency.getDisplayName(tenant.getLocale()));
            currency = currencyRepository.save(currency);
        }
        return currency;
    }
}
