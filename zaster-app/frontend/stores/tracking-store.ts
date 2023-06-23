import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity.ts";
import {makeAutoObservable, observable} from "mobx";
import {CurrencyEndpoint} from "Frontend/generated/endpoints.ts";

export class TrackingStore {
    currencies: CurrencyEntity[] = [];

    constructor() {
        makeAutoObservable(
            this,
            {
                initFromServer: false,
                currencies: observable.shallow
            },
            { autoBind: true }
        );

        this.initFromServer();
    }

    async initFromServer() {
        this.currencies = await CurrencyEndpoint.findAllCurrencies();
    }
}