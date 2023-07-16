package de.spricom.zaster.init;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.managment.UserRole;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.repository.ManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

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
    private ManagementService managementService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AccountService accountService;

    private TenantEntity currentTenant;

    @Test
    void checkProperties() {
        assertThat(props).isNotNull();
        assertThat(props.getTenants()).isNotEmpty();
        props.getTenants().forEach((key, tenant) ->
                assertThat(tenant.getUsers()).as(key).isNotEmpty());
    }

    @Test
    void initDatabase() {
        props.getTenants().values().forEach(this::initTenant);
    }

    private void initTenant(ZasterInitProperties.Tenant tenant) {
        var tenantEntity = new TenantEntity();
        tenantEntity.setName(tenant.getName());
        var users = tenant.getUsers().entrySet().stream()
                .map(entry -> this.asUserEntity(entry.getKey(), entry.getValue()))
                .toList();
        var firstUser = users.get(0);
        firstUser.setTenant(tenantEntity);
        var savedUser = managementService.createTenant(firstUser);
        currentTenant = savedUser.getTenant();
        for (int i = 1; i < users.size(); i++) {
            var user = users.get(i);
            user.setTenant(currentTenant);
            managementService.saveUser(user);
        }
        initIsoCurrencies(tenant.getIsoCurrencies());
        tenant.getCurrencies().forEach(this::initCurrency);
        tenant.getAccounts().forEach((key, value) -> initAccount(null, value));
    }

    private ApplicationUserEntity asUserEntity(String username, ZasterInitProperties.User user) {
        var entity = new ApplicationUserEntity();
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
            currency.setCurrencyName(isoCurrency.getDisplayName());
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

    private void initAccount(AccountGroupEntity parent, ZasterInitProperties.Account account) {
        var group = new AccountGroupEntity();
        group.setTenant(currentTenant);
        group.setParent(parent);
        group.setAccountName(account.getName());
        var savedGroup = accountService.saveAccountGroup(group);
        if (account.getAccounts() != null) {
            account.getAccounts().forEach((key, value) -> initAccount(savedGroup, value));
        }
    }
}
