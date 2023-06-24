import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity.ts";
import {makeAutoObservable, observable, runInAction} from "mobx";
import {AccountingEndpoint} from "Frontend/generated/endpoints.ts";
import {AccountGroup} from "Frontend/model/tracking/AccountGroup.ts";

export class AccountingStore {
    currencies: CurrencyEntity[] = [];
    rootAccountGroups: AccountGroup[] = [];

    constructor() {
        makeAutoObservable(
            this,
            {
                initFromServer: false,
                currencies: observable.shallow,
                rootAccountGroups: false
            },
            { autoBind: true }
        );
    }

    async initFromServer() {
        console.log("loading accounting data...");
        const data = await AccountingEndpoint.getAccountingData();

        runInAction(() => {
           this.currencies = data.currencies;
           this.rootAccountGroups = data.rootAccountGroups.map(group => new AccountGroup(null, group, this.getCurrency));
        });
    }

    getCurrency(id: string) {
        let currency = this.currencies.find(currency => currency.id === id);
        if (!currency) {
            throw new Error("There is no currency with id = " + id);
        }
        return currency;
    }

    getAccount(id: string) {
        for (const group of this.rootAccountGroups) {
            let account = group.findAccount(id);
            if (account) {
                return account;
            }
        }
        throw new Error("There is no account with id = " + id);
    }
}