import {makeAutoObservable, observable} from "mobx";
import {CurrencyEndpoint} from "Frontend/generated/endpoints";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import {accountingStore} from "Frontend/stores/app-store.ts";
import CurrencyDtoModel from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDtoModel.ts";

class CurrenciesViewStore {
    filterText = '';
    selectedCurrency: CurrencyDto | null = null;

    constructor() {
        makeAutoObservable(
            this,
            {
                selectedCurrency: observable.ref
            },
            {autoBind: true}
        );
    }

    get filteredCurrencies() {
        const filter = new RegExp(this.filterText, 'i');
        return accountingStore._currencies.filter((currency) =>
            filter.test(`${currency.currencyCode} ${currency.currencyName}`)
        );
    }

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    setSelectedCurrency(currency: CurrencyDto) {
        this.selectedCurrency = currency;
    }

    editNew() {
        this.selectedCurrency = CurrencyDtoModel.createEmptyValue();
        this.selectedCurrency.currencyCode = this.filterText.toUpperCase();
    }

    cancelEdit() {
        this.selectedCurrency = null;
    }

    async save(currency: CurrencyDto) {
        await this.saveCurrency(currency)
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedCurrency) {
            await this.deleteCurrency(this.selectedCurrency)
            this.cancelEdit();
        }
    }

    async saveCurrency(currency: CurrencyDto) {
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

    async deleteCurrency(currency: CurrencyDto) {
        if (!currency.id) return;

        try {
            await CurrencyEndpoint.deleteCurrencyById(currency.id.uuid);
            this.deleteLocal(currency);
        } catch (ex) {
            console.log('Currency delete failed: ' + ex);
        }
    }

    private saveLocal(saved: CurrencyDto) {
        const currencyExists = accountingStore._currencies.some((c) => c.id === saved.id);
        if (currencyExists) {
            accountingStore._currencies = accountingStore._currencies.map((existing) => {
                if (existing.id === saved.id) {
                    return saved;
                } else {
                    return existing;
                }
            });
        } else {
            accountingStore._currencies.push(saved);
        }
    }

    private deleteLocal(currencyEntity: CurrencyDto) {
        accountingStore._currencies = accountingStore._currencies
            .filter((c) => c.id !== currencyEntity.id);
    }
}

export const currenciesViewStore = new CurrenciesViewStore();