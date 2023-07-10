package de.spricom.zaster.endpoints;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;

import java.util.List;

@Endpoint
@PermitAll
@AllArgsConstructor
public class CurrencyEndpoint {

    private final AuthenticatedUser authenticatedUser;
    private final CurrencyService currencyService;

    public List<CurrencyEntity> findAllCurrencies() {
        return currencyService.findAllCurrencies(authenticatedUser.getCurrentTenant());
    }

    public CurrencyEntity saveCurrency(CurrencyEntity currency) {
        if (currency.getTenant() == null) {
            currency.setTenant(authenticatedUser.getCurrentTenant());
        }
        if (currency.getCurrencyType() == null) {
            currency.setCurrencyType(CurrencyType.FIAT);
        }
        currency.setCurrencyCode(currency.getCurrencyCode().toUpperCase());
        return currencyService.saveCurrency(currency);
    }

    public void deleteCurrencyById(String currencyId) {
        currencyService.deleteCurrencyById(currencyId);
    }
}
