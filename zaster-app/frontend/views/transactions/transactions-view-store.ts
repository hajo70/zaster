import {makeAutoObservable, observable} from "mobx";
import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity";
import CurrencyEntityModel from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntityModel";
import {CurrencyEndpoint} from "Frontend/generated/endpoints";

class TransactionsViewStore {
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
        this.selectedCurrency.currencyCode = this.filterText.toUpperCase();
    }

    cancelEdit() {
        this.selectedCurrency = null;
    }

    async save(currency: CurrencyEntity) {
        await this.saveCurrency(currency)
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedCurrency) {
            await this.deleteCurrency(this.selectedCurrency)
            this.cancelEdit();
        }
    }

    async saveCurrency(currency: CurrencyEntity) {
        try {
            const saved = await CurrencyEndpoint.saveCurrency(currency);
            if (saved) {
                this.saveLocal(saved);
            } else {
                console.log('Currency save failed');
            }
        } catch (ex) {
            console.log('Currency save failed: ' + ex);
        }
    }

    async deleteCurrency(currency: CurrencyEntity) {
        if (!currency.id) return;

        try {
            await CurrencyEndpoint.deleteCurrencyById(currency.id);
            this.deleteLocal(currency);
        } catch (ex) {
            console.log('Currency delete failed: ' + ex);
        }
    }

    private saveLocal(saved: CurrencyEntity) {
        const currencyExists = this.currencies.some((c) => c.id === saved.id);
        if (currencyExists) {
            this.currencies = this.currencies.map((existing) => {
                if (existing.id === saved.id) {
                    return saved;
                } else {
                    return existing;
                }
            });
        } else {
            this.currencies.push(saved);
        }
    }

    private deleteLocal(currencyEntity: CurrencyEntity) {
        this.currencies = this.currencies.filter((c) => c.id !== currencyEntity.id);
    }
}

export const transactionsViewStore = new TransactionsViewStore();