import {makeAutoObservable, observable} from "mobx";
import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity";
import CurrencyEntityModel from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntityModel";

class CurrenciesViewStore {
    filterText = '';
    selectedCurrency: CurrencyEntity | null = null;

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