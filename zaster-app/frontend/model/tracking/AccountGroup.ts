import AccountGroupDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountGroupDto.ts";
import {Account} from "Frontend/model/tracking/Account.ts";
import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity.ts";

export class AccountGroup {
    data: AccountGroupDto;
    parent: AccountGroup | null;
    children: AccountGroup[] = [];
    accounts: Account[] = [];

    constructor(parent: AccountGroup | null, data: AccountGroupDto, currencyLookup: (id: string) => CurrencyEntity) {
        this.parent = parent;
        this.data = data;
        if (data.children) {
            data.children.map(child => new AccountGroup(this, child, currencyLookup));
            delete data.children;
        }
        if (data.accounts) {
            data.accounts.map(account => new Account(this, account, currencyLookup(account.currencyId)));
            delete data.accounts;
        }
    }

    get name() {
        return this.data.accountName;
    }

    findAccount(id: string): Account | undefined {
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