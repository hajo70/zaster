import {makeObservable, observable} from "mobx";
import {Account} from "Frontend/model/tracking/Account.ts";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";

export class AccountingStore {
    _currencies: CurrencyDto[] = [];
    _rootAccounts: Account[] = [];

    constructor() {
        makeObservable(
            this,
            {
                _currencies: observable.shallow
            }
        )
    }

    set currencies(currencies: CurrencyDto[]) {
        this._currencies = currencies;
    }

    set rootAccounts(rootAccounts: AccountDto[]) {
        this._rootAccounts = rootAccounts
            .map(group => new Account(null, group, this.getCurrency));
    }

    getCurrency = (uuid: string) => {
        let currency = this._currencies
            .find(currency => currency.id?.uuid === uuid);
        if (!currency) {
            throw new Error("There is no currency with id = " + uuid);
        }
        return currency;
    }

    getAccount = (id: string) => {
        for (const account of this._rootAccounts) {
            let match = account.lookUpAccountCurrency(id);
            if (match) {
                return match;
            }
        }
        throw new Error("There is no account with id = " + id);
    }

    lookUpAccountCurrency(id: string): AccountCurrency {
        for (const account of this._rootAccounts) {
            const accountCurrency = account.lookUpAccountCurrency(id);
            if (accountCurrency) {
                return accountCurrency;
            }
        }
        throw new Error("There is no account-currency " + id);
    }
}