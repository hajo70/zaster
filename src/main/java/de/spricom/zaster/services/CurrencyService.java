package de.spricom.zaster.services;

import de.spricom.zaster.data.Currency;
import de.spricom.zaster.data.CurrencyRepository;
import de.spricom.zaster.data.CurrencyType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public Page<Currency> list(Pageable pageable) {
        return currencyRepository.findAll(pageable);
    }

    public Page<Currency> list(Pageable pageable, Specification<Currency> filter) {
        return currencyRepository.findAll(filter, pageable);
    }

    public Optional<Currency> getCurrency(String currencyId) {
        return currencyRepository.findById(currencyId);
    }

    public Currency saveCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public void deleteCurrency(Currency currency) {
        currencyRepository.delete(currency);
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
