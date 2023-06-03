package de.spricom.zaster.repository.currency;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.repository.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
