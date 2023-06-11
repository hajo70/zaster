package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Endpoint
@AnonymousAllowed
public class CurrencyEndpoint {

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    private CurrencyService currencyService;

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
