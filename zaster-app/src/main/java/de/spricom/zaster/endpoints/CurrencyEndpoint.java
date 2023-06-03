package de.spricom.zaster.endpoints;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Endpoint
public class CurrencyEndpoint {

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    private CurrencyService currencyService;

    private CurrencyEntity createCurrency(String currencyCode, String currencyName) {
        var currency = new CurrencyEntity();
        currency.setTenant(authenticatedUser.getCurrentTenant());
        currency.setCurrencyType(CurrencyType.FIAT);
        currency.setCurrencyCode(currencyCode);
        currency.setCurrencyName(currencyName);
        return currencyService.saveCurrency(currency);
    }

    public List<CurrencyEntity> findAllCurrencies() {
        return currencyService.findAllCurrencies(authenticatedUser.getCurrentTenant());
    }
}
