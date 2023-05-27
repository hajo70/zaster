package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.endpoints.model.Currency;
import dev.hilla.Endpoint;

import java.util.ArrayList;
import java.util.List;

@Endpoint
@AnonymousAllowed
public class CurrenciesEndpoint {
    private List<Currency> currencies = new ArrayList<>();

    public CurrenciesEndpoint() {
        currencies.add(createCurrency("EUR", "Euro"));
        currencies.add(createCurrency("USD", "Dollar"));
        currencies.add(createCurrency("BTC", "Bitcoin"));
    }

    private Currency createCurrency(String currencyCode, String currencyName) {
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        currency.setName(currencyName);
        return currency;
    }

    public List<Currency> findAllCurrencies() {
        return currencies;
    }
}
