import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";

export class Account {
    data: AccountDto;
    parent: Account | null;
    children: Account[] = [];
    accountCurrencies: AccountCurrency[] = [];

    filteredChildren: Account[] = [];

    constructor(parent: Account | null, data: AccountDto, currencyLookup: (id: string) => CurrencyDto) {
        this.parent = parent;
        this.data = data;
        if (data.children) {
            this.children = data.children
                .map(child => new Account(this, child, currencyLookup));
            delete data.children;
        }
        if (data.currencies) {
            this.accountCurrencies = data.currencies
                .map(ac => new AccountCurrency(this, ac, currencyLookup(ac.currencyId)));
            delete data.currencies;
        }
    }

    matchesFilter(filter: (a: Account) => boolean): boolean {
        if (this.children) {
            this.filteredChildren = this.children.filter(filter);
        }
        return this.filteredChildren.length > 0 || filter(this);
    }

    lookupAccountCurrencies(currency: CurrencyDto): AccountCurrency[] {
        return this.accountCurrencies.filter(ac => ac.currency === currency)
            .concat(this.children.flatMap(a => a.lookupAccountCurrencies(currency)));
    }

    lookUpAccountCurrency(id: string): AccountCurrency | undefined {
        let account = this.accountCurrencies.find(a => id === a.data.id.uuid);
        if (account) {
            return account;
        }
        for (const child of this.children) {
            account = child.lookUpAccountCurrency(id);
            if (account) {
                return account;
            }
        }
        return undefined;
    }

    get currencies(): CurrencyDto[] {
        return [...new Set(this.accountCurrencies.map(c => c.currency)
            .concat(this.children.flatMap(a => a.currencies)))];
    }

    get accountName() {
        return this.data.accountName;
    }

    get accountCode() {
        return this.data.accountCode;
    }

    get hasChildren() {
        return this.children.length > 0;
    }

    get parentAccountName() {
        return this.parent?.accountName;
    }
}