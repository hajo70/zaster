import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity.ts";
import {makeObservable, observable, runInAction} from "mobx";
import {AccountingEndpoint} from "Frontend/generated/endpoints.ts";
import {Account} from "Frontend/model/tracking/Account.ts";

export class AccountingStore {
    currencies: CurrencyEntity[] = [];
    rootAccountGroups: Account[] = [];

    constructor() {
        makeObservable(
            this,
            {
                currencies: observable.shallow
            }
        )
    }

    async initFromServer() {
        console.log("loading accounting data...");
        const data = await AccountingEndpoint.getAccountingData();

        runInAction(() => {
           this.currencies = data.currencies;
           this.rootAccountGroups = data.rootAccounts.map(group => new Account(null, group, this.getCurrency));
        });
    }

    getCurrency = (id: string) => {
        let currency = this.currencies.find(currency => currency.id === id);
        if (!currency) {
            throw new Error("There is no currency with id = " + id);
        }
        return currency;
    }

    getAccount = (id: string) => {
        for (const group of this.rootAccountGroups) {
            let account = group.findAccount(id);
            if (account) {
                return account;
            }
        }
        throw new Error("There is no account with id = " + id);
    }
}