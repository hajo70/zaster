package de.spricom.zaster.init;

import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.enums.settings.UserRole;
import de.spricom.zaster.enums.tracking.CurrencyType;
import de.spricom.zaster.importing.ImportHandlingService;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.repository.SettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.Currency;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("initme")
public class ZasterInitTool {

    @Autowired
    private ZasterInitProperties props;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ImportHandlingService importHandlingService;

    private TenantEntity currentTenant;

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
        var tenantEntity = new TenantEntity();
        tenantEntity.setName(tenant.getName());
        tenantEntity.setLocale(tenant.getLocale());
        tenantEntity.setTimezone(tenant.getTimezone());
        var users = tenant.getUsers().entrySet().stream()
                .map(entry -> this.asUserEntity(entry.getKey(), entry.getValue()))
                .toList();
        var firstUser = users.get(0);
        firstUser.setTenant(tenantEntity);
        var savedUser = settingsService.createTenant(firstUser);
        currentTenant = savedUser.getTenant();
        for (int i = 1; i < users.size(); i++) {
            var user = users.get(i);
            user.setTenant(currentTenant);
            settingsService.saveUser(user);
        }
        initIsoCurrencies(tenant.getIsoCurrencies());
        tenant.getCurrencies().forEach(this::initCurrency);
        tenant.getAccounts().forEach((value) -> initAccount(null, value));
        tenant.getImports().forEach(this::importFiles);
    }

    private void importFiles(ZasterInitProperties.Import importTask) {
        for (File file : importTask.getFiles()) {
            importHandlingService.importFile(currentTenant, importTask.getImporter(), new FileSystemResource(file));
        }
    }

    private UserEntity asUserEntity(String username, ZasterInitProperties.User user) {
        var entity = new UserEntity();
        entity.setUsername(username);
        entity.setName(user.getName());
        entity.setHashedPassword(passwordEncoder.encode(username));
        entity.setUserRoles(EnumSet.of(UserRole.valueOf(user.getRole().name())));
        return entity;
    }

    private void initIsoCurrencies(List<String> isoCurrencies) {
        for (String currencyCode : isoCurrencies) {
            var isoCurrency = Currency.getInstance(currencyCode);
            var currency = new CurrencyEntity();
            currency.setTenant(currentTenant);
            currency.setCurrencyType(CurrencyType.FIAT);
            currency.setCurrencyCode(isoCurrency.getCurrencyCode());
            currency.setCurrencyName(isoCurrency.getDisplayName(currentTenant.getLocale()));
            currencyService.saveCurrency(currency);
        }
    }

    private void initCurrency(String currencyCode, ZasterInitProperties.Currency currency) {
        var entity = new CurrencyEntity();
        entity.setTenant(currentTenant);
        entity.setCurrencyType(CurrencyType.valueOf(currency.getType().name()));
        entity.setCurrencyCode(currencyCode);
        entity.setCurrencyName(currency.getName());
        currencyService.saveCurrency(entity);
    }

    private void initAccount(AccountEntity parent, ZasterInitProperties.Account account) {
        var group = new AccountEntity();
        group.setTenant(currentTenant);
        group.setParent(parent);
        group.setAccountName(account.getName());
        group.setAccountCode(account.getCode());
        var savedGroup = accountService.saveAccount(group);
        if (account.getAccounts() != null) {
            account.getAccounts().forEach(value -> initAccount(savedGroup, value));
        }
    }
}
