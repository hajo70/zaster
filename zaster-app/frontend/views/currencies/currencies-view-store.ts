import {makeAutoObservable, observable} from "mobx";
import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity";
import CurrencyEntityModel from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntityModel";
import {CurrencyEndpoint} from "Frontend/generated/endpoints";

class CurrenciesViewStore {
    currencies: CurrencyEntity[] = [];
    filterText = '';
    selectedCurrency: CurrencyEntity | null = null;

    constructor() {
        makeAutoObservable(
            this,
            {
                initFromServer: false,
                currencies: observable.shallow,
                selectedCurrency: observable.ref
            },
            {autoBind: true}
        );

        this.initFromServer();
    }

    async initFromServer() {
        this.currencies = await CurrencyEndpoint.findAllCurrencies();
    }

    get filteredCurrencies() {
        const filter = new RegExp(this.filterText, 'i');
        return this.currencies.filter((currency) =>
            filter.test(`${currency.currencyCode} ${currency.currencyName}`)
        );
    }

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    setSelectedCurrency(currency: CurrencyEntity) {
        this.selectedCurrency = currency;
    }

    editNew() {
        this.selectedCurrency = CurrencyEntityModel.createEmptyValue();
    }

    cancelEdit() {
        this.selectedCurrency = null;
    }

    async save(currency: CurrencyEntity) {
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedCurrency) {
            this.cancelEdit();
        }
    }
}

export const currenciesViewStore = new CurrenciesViewStore();