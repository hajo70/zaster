import {makeAutoObservable, observable} from "mobx";
import {TrackingEndpoint} from "Frontend/generated/endpoints.ts";
import {Booking} from "Frontend/model/tracking/Booking.ts";
import {accountingStore} from "Frontend/stores/app-store.ts";

export class TrackingStore {
    transactions: Booking[] = [];

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
        this.transactions = transactions.map(tx => new Booking(tx, accountingStore.getAccount));
    }
}