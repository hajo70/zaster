import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";

export class Account {
    data: AccountDto;
    parent: Account | null;
    children: Account[] = [];
    accounts: AccountCurrency[] = [];

    constructor(parent: Account | null, data: AccountDto, currencyLookup: (id: string) => CurrencyDto) {
        this.parent = parent;
        this.data = data;
        if (data.children) {
            data.children.map(child => new Account(this, child, currencyLookup));
            delete data.children;
        }
        if (data.accounts) {
            data.accounts.map(account => new AccountCurrency(this, account, currencyLookup(account.currencyId)));
            delete data.accounts;
        }
    }

    get name() {
        return this.data.accountName;
    }

    findAccount(id: string): AccountCurrency | undefined {
        let account = this.accounts.find(a => id === a.data.id.uuid);
        if (account) {
            return account;
        }
        for (const child of this.children) {
            account = child.findAccount(id);
            if (account) {
                return account;
            }
        }
        return undefined;
    }
}