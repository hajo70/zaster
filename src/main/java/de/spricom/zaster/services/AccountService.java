package de.spricom.zaster.services;

import de.spricom.zaster.data.Account;
import de.spricom.zaster.data.AccountCurrency;
import de.spricom.zaster.data.AccountCurrencyRepository;
import de.spricom.zaster.data.AccountRepository;
import de.spricom.zaster.data.Currency;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountService {
    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    public static final Comparator<Account> ACCOUNT__COMPARATOR = Comparator.comparing(Account::getAccountName);

    private final AccountCurrencyRepository accountCurrencyRepository;
    private final AccountRepository accountRepository;

    public Page<Account> list(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Page<Account> list(Pageable pageable, Specification<Account> filter) {
        return accountRepository.findAll(filter, pageable);
    }

    public List<Account> findAllRootAccounts() {
        var accounts = accountRepository.findAccounts();
        for (Account account : accounts) {
            if (account.getParent() != null) {
                if (account.getParent().getChildren() == null) {
                    account.getParent().setChildren(
                            new TreeSet<>(ACCOUNT__COMPARATOR));
                }
                account.getParent().getChildren().add(account);
            }
        }
        return accounts.stream()
                .filter(account -> account.getParent() == null)
                .sorted(ACCOUNT__COMPARATOR)
                .collect(Collectors.toList());
    }

    public Optional<Account> findAccountByCode(String accountCode) {
        return Optional.ofNullable(accountRepository.findByAccountCode(accountCode));
    }

    public Optional<Account> getAccount(String accountId) {
        return accountRepository.findById(accountId);
    }

    public Account saveAccount(Account account) {
        log.info("saving account: {}", account);
        return accountRepository.save(account);
    }

    public void deleteAccount(Account account) {
        log.info("deleting account: {}", account);
        accountRepository.delete(account);
    }

    public AccountCurrency saveAccountCurrency(AccountCurrency accountCurrency) {
        log.info("saving accountCurrency: {}", accountCurrency);
        return accountCurrencyRepository.save(accountCurrency);
    }

    public void deleteAccountCurrency(String accountId) {
        log.info("deleting account: {}", accountId);
        accountCurrencyRepository.deleteById(accountId);
    }

    public AccountCurrency getOrCreateAccountCurrency(String accountCode, String accountName, Currency currency) {
        var account = accountRepository.findByAccountCode(accountCode);
        if (account == null) {
            account = new Account();
            account.setAccountCode(accountCode);
            account.setAccountName(accountName);
            account = accountRepository.save(account);
        }
        return getOrCreateAccountCurrency(account, currency);
    }

    public AccountCurrency getOrCreateAccountCurrency(Account account, Currency currency) {
        var accountCurrency = accountCurrencyRepository.findByAccountIdAndCurrencyId(account.getId(), currency.getId());
        if (accountCurrency == null) {
            accountCurrency = new AccountCurrency();
            accountCurrency.setAccount(account);
            accountCurrency.setCurrency(currency);
            accountCurrency = accountCurrencyRepository.save(accountCurrency);
        }
        return accountCurrency;
    }
}
