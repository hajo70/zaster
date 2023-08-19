import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";

export class Account {
    data: AccountDto;
    parent: Account | null;
    children: Account[] = [];
    currencies: AccountCurrency[] = [];

    constructor(parent: Account | null, data: AccountDto, currencyLookup: (id: string) => CurrencyDto) {
        this.parent = parent;
        this.data = data;
        if (data.children) {
            this.children = data.children
                .map(child => new Account(this, child, currencyLookup));
            delete data.children;
        }
        if (data.currencies) {
            this.currencies = data.currencies
                .map(ac => new AccountCurrency(this, ac, currencyLookup(ac.currencyId)));
            delete data.currencies;
        }
    }

    get name() {
        return this.data.accountName;
    }

    lookUpAccountCurrency(id: string): AccountCurrency | undefined {
        let account = this.currencies.find(a => id === a.data.id.uuid);
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
}