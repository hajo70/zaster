package de.spricom.zaster.init;

import de.spricom.zaster.data.Account;
import de.spricom.zaster.data.Currency;
import de.spricom.zaster.data.CurrencyType;
import de.spricom.zaster.services.AccountService;
import de.spricom.zaster.services.CurrencyService;
import de.spricom.zaster.services.UploadService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("initme")
@Disabled("run manually")
public class ZasterInitTool {

    @Autowired
    private ZasterInitProperties props;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UploadService uploadService;

    @Test
    void checkProperties() {
        assertThat(props).isNotNull();
        assertThat(props.getTenants()).isNotEmpty();
        props.getTenants().forEach((tenant) ->
                assertThat(tenant.getUsers()).isNotEmpty());
    }

    @Test
    void initDatabase() {
        props.getTenants().forEach(this::initTenant);
    }

    private void initTenant(ZasterInitProperties.Tenant tenant) {
        initIsoCurrencies(tenant.getIsoCurrencies());
        tenant.getCurrencies().forEach(this::initCurrency);
        tenant.getAccounts().forEach((value) -> initAccount(null, value));
        tenant.getImports().forEach(this::importFiles);
    }

    private void importFiles(ZasterInitProperties.Import importTask) {
        for (File file : importTask.getFiles()) {
            uploadService.importFile(importTask.getImporter(), new FileSystemResource(file));
        }
    }

    private void initIsoCurrencies(List<String> isoCurrencies) {
        for (String currencyCode : isoCurrencies) {
            var isoCurrency = java.util.Currency.getInstance(currencyCode);
            var currency = new Currency();
            currency.setCurrencyType(CurrencyType.FIAT);
            currency.setCurrencyCode(isoCurrency.getCurrencyCode());
            currency.setCurrencyName(isoCurrency.getDisplayName());
            currencyService.saveCurrency(currency);
        }
    }

    private void initCurrency(String currencyCode, ZasterInitProperties.Currency currency) {
        var entity = new Currency();
        entity.setCurrencyType(CurrencyType.valueOf(currency.getType().name()));
        entity.setCurrencyCode(currencyCode);
        entity.setCurrencyName(currency.getName());
        currencyService.saveCurrency(entity);
    }

    private void initAccount(Account parent, ZasterInitProperties.Account account) {
        var group = new Account();
        group.setParent(parent);
        group.setAccountName(account.getName());
        group.setAccountCode(account.getCode());
        var savedGroup = accountService.saveAccount(group);
        if (account.getAccounts() != null) {
            account.getAccounts().forEach(value -> initAccount(savedGroup, value));
        }
    }
}
