package de.spricom.zaster;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.repository.currency.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("initdb")
public class DatabaseSetupTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    void testDatabaseSchema() {
        createCurrency();
    }

    private void createCurrency() {
        var currency = new CurrencyEntity();
        currency.setCurrencyCode("EUR");
        currency.setCurrencyName("Euro");
        currencyRepository.save(currency);
    }
}
