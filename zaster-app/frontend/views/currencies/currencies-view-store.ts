import Currency from "Frontend/generated/de/spricom/zaster/endpoints/model/Currency";
import {makeAutoObservable, observable} from "mobx";
import CurrencyModel from "Frontend/generated/de/spricom/zaster/endpoints/model/CurrencyModel";

class CurrenciesViewStore {
    filterText = '';
    selectedCurrency: Currency | null = null;

    constructor() {
        makeAutoObservable(
            this,
            {selectedCurrency: observable.ref},
            {autoBind: true}
        );
    }

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    setSelectedCurrency(currency: Currency) {
        this.selectedCurrency = currency;
    }

    editNew() {
        this.selectedCurrency = CurrencyModel.createEmptyValue();
    }

    cancelEdit() {
        this.selectedCurrency = null;
    }

    async save(currency: Currency) {
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedCurrency) {
            this.cancelEdit();
        }
    }
}

export const currenciesViewStore = new CurrenciesViewStore();