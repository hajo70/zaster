package de.spricom.zaster.services;

import de.spricom.zaster.data.Account;
import de.spricom.zaster.data.AccountCurrency;
import de.spricom.zaster.data.Currency;
import de.spricom.zaster.data.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CurrencyService currencyService;

    private Map<String, Currency> currencies;

    @BeforeEach
    void init() {
        currencies = Stream.of("EUR", "USD").map(this::createCurrency)
                .collect(Collectors.toMap(Currency::getCurrencyCode, c -> c));
    }

    private Currency createCurrency(String currencyCode) {
        var isoCurrency = java.util.Currency.getInstance(currencyCode);
        var currency = new Currency();
        currency.setCurrencyCode(isoCurrency.getCurrencyCode());
        currency.setCurrencyName(isoCurrency.getDisplayName());
        currency.setCurrencyType(CurrencyType.FIAT);
        return currencyService.saveCurrency(currency);
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

        var tree = accountService.findAllRootAccounts();
        assertThat(tree).hasSizeGreaterThanOrEqualTo(2).contains(root1, root2);
        assertThat(render(tree)).contains("""
                Root 1()
                  Main()
                    Sub 1(EUR)
                    Sub 2()
                      Leaf 1(EUR, USD)
                Root 2()
                """);
    }

    private String render(Collection<Account> tree) {
        StringBuilder sb = new StringBuilder(2048);
        List<Account> sorted = tree.stream().sorted(Comparator.comparing(Account::getAccountName)).toList();
        render(sb, 0, sorted);
        return sb.toString();
    }

    private void render(StringBuilder sb, int indent, Collection<Account> tree) {
        for (Account group : tree) {
            sb.append("  ".repeat(indent));
            sb.append(group.getAccountName());
            sb.append(group.getCurrencies().stream().map(AccountCurrency::getCurrency).map(Currency::getCurrencyCode)
                    .sorted().collect(Collectors.joining(", ", "(", ")")));
            sb.append("\n");
            if (group.getChildren() != null && !group.getChildren().isEmpty()) {
                render(sb, indent + 1, group.getChildren());
            }
        }
    }

    private Account createAccountGroup(Account parent, String accountName) {
        var account = new Account();
        account.setParent(parent);
        account.setAccountName(accountName);
        return accountService.saveAccount(account);
    }

    private AccountCurrency createAccount(Account group, String currencyCode) {
        var account = new AccountCurrency();
        account.setAccount(group);
        account.setCurrency(currencies.get(currencyCode));
        return accountService.saveAccountCurrency(account);
    }
}