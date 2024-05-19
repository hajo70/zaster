package de.spricom.zaster.services;

import de.spricom.zaster.data.Currency;
import de.spricom.zaster.data.CurrencyRepository;
import de.spricom.zaster.data.CurrencyType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public List<Currency> findAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency saveCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public void deleteCurrencyById(String currencyId) {
        currencyRepository.deleteById(currencyId);
    }

    public Currency getOrCreateCurrencyByCode(String currencyCode) {
        var currency = currencyRepository.findByCurrencyCode(currencyCode);
        if (currency == null) {
            var isoCurrency = java.util.Currency.getInstance(currencyCode);
            currency = new Currency();
            currency.setCurrencyType(CurrencyType.FIAT);
            currency.setCurrencyCode(isoCurrency.getCurrencyCode());
            currency.setCurrencyName(isoCurrency.getDisplayName(Locale.GERMANY));
            currency = currencyRepository.save(currency);
        }
        return currency;
    }
}
