import {makeAutoObservable, observable} from "mobx";
import {TrackingEndpoint} from "Frontend/generated/endpoints.ts";
import {Transaction} from "Frontend/model/tracking/Transaction.ts";
import {accountingStore} from "Frontend/stores/app-store.ts";

export class TrackingStore {
    transactions: Transaction[] = [];

    constructor() {
        makeAutoObservable(
            this,
            {
                initFromServer: false,
                transactions: observable.shallow
            },
            { autoBind: true }
        );
    }

    async initFromServer() {
        const transactions = await TrackingEndpoint.getTransactions();
        this.transactions = transactions.map(tx => new Transaction(tx, accountingStore.getAccount));
    }
}