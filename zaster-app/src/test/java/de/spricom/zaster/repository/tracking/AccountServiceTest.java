package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.currency.CurrencyType;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.AccountGroupEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.repository.management.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private TenantRepository tenantRepository;

    private TenantEntity tenant;
    private Map<String, CurrencyEntity> currencies;

    @BeforeEach
    void init(TestInfo testInfo) {
        TenantEntity tenant = new TenantEntity();
        tenant.setName(testInfo.getDisplayName());
        this.tenant = tenantRepository.save(tenant);
        currencies = Stream.of("EUR", "USD")
                .map(this::createCurrency)
                .collect(Collectors.toMap(CurrencyEntity::getCurrencyCode, c -> c));
    }

    private CurrencyEntity createCurrency(String currencyCode) {
        var currency = Currency.getInstance(currencyCode);
        var entity = new CurrencyEntity();
        entity.setTenant(tenant);
        entity.setCurrencyCode(currency.getCurrencyCode());
        entity.setCurrencyName(currency.getDisplayName());
        entity.setCurrencyType(CurrencyType.FIAT);
        return currencyService.saveCurrency(entity);
    }

    @Test
    void testAccountGroups() {
        var root1 = createAccountGroup(null, "Root 1");
        var root2 = createAccountGroup(null, "Root 2");
        var main = createAccountGroup(root1, "Main");
        var sub1 = createAccountGroup(main, "Sub 1");
        var sub2 = createAccountGroup(main, "Sub 2");
        var leaf1 = createAccountGroup(sub2, "Leaf 1");
        var leaf1eur = createAccount(leaf1, "EUR");
        var leaf1usd = createAccount(leaf1, "USD");
        var sub1eur = createAccount(sub1, "EUR");

        var tree = accountService.findAllRootAccountGroups(tenant);
        assertThat(tree).hasSize(2).contains(root1, root2);
        assertThat(render(tree)).isEqualTo("""
                Root 1()
                  Main()
                    Sub 1(EUR)
                    Sub 2()
                      Leaf 1(EUR, USD)
                Root 2()
                """);
    }

    private String render(Collection<AccountGroupEntity> tree) {
        StringBuilder sb = new StringBuilder(2048);
        List<AccountGroupEntity> sorted = tree.stream()
                .sorted(Comparator.comparing(AccountGroupEntity::getAccountName))
                .toList();
        render(sb, 0, sorted);
        return sb.toString();
    }

    private void render(StringBuilder sb, int indent, Collection<AccountGroupEntity> tree) {
        for (AccountGroupEntity group : tree) {
            sb.append("  ".repeat(indent));
            sb.append(group.getAccountName());
            sb.append(group.getAccounts().stream()
                    .map(AccountEntity::getCurrency)
                    .map(CurrencyEntity::getCurrencyCode)
                    .sorted()
                    .collect(Collectors.joining(", ", "(", ")")));
            sb.append("\n");
            if (group.getChildren() != null && !group.getChildren().isEmpty()) {
                render(sb, indent + 1, group.getChildren());
            }
        }
    }

    private AccountGroupEntity createAccountGroup(AccountGroupEntity parent, String accountName) {
        var group = new AccountGroupEntity();
        group.setTenant(tenant);
        group.setParent(parent);
        group.setAccountName(accountName);
        return accountService.saveAccountGroup(group);
    }

    private AccountEntity createAccount(AccountGroupEntity group, String currencyCode) {
        var account = new AccountEntity();
        account.setAccountGroup(group);
        account.setCurrency(currencies.get(currencyCode));
        return accountService.saveAccount(account);
    }
}