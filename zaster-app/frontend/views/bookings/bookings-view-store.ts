import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import {Account} from "Frontend/model/tracking/Account.ts";
import {accountingStore, trackingStore} from "Frontend/stores/app-store.ts";
import {action, computed, makeObservable, observable} from "mobx";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import {Transfer} from "Frontend/model/tracking/Transfer.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import AccountDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDtoModel.ts";
import {Notification} from "@vaadin/notification";

class BookingsViewStore {
    selectedAccount: Account | null = null;
    selectedCurrency: CurrencyDto | null = null;
    filterText = '';
    editedAccount: AccountDto | null = null;

    constructor() {
        makeObservable(this,
            {
                selectedAccount: observable.ref,
                selectedCurrency: observable.ref,
                filterText: observable,
                filteredAccounts: computed,
                editedAccount: observable.ref,
                setSelectedAccount: action,
                setSelectedCurrency: action,
                editNew: action,
                cancelEdit: action
            }
        );
    }

    dataProvider = (
        params: GridDataProviderParams<Account>,
        callback: GridDataProviderCallback<Account>
    ) => {
        const accounts = params.parentItem ? params.parentItem.filteredChildren : this.filteredAccounts;
        callback(accounts, accounts.length);
    }

    get filteredAccounts() {
        const nameFilter = new RegExp(this.filterText, 'i');
        const accountFilter = (account: Account) => nameFilter.test(account.accountName);
        const accounts = accountingStore._rootAccounts;
        return accounts.filter((account) =>
            account.matchesFilter(accountFilter)
        );
    }

    get currencies(): CurrencyDto[] {
        if (this.selectedAccount == null) {
            return [];
        }
        return this.selectedAccount.currencies.sort();
    }

    get transfers(): Transfer[] {
        if (this.selectedAccount == null || this.selectedCurrency == null) {
            console.log("no tranfers");
            return [];
        }
        const accountCurrencies = this.selectedAccount.lookupAccountCurrencies(this.selectedCurrency);
        const tf =  trackingStore.bookings.flatMap(b => b.transfers)
            .filter(t => accountCurrencies.includes(t.accountCurrency));
        console.log(tf.length + " transfers");
        return tf;
    }

    setSelectedAccount(account: Account) {
        this.selectedAccount = account;
        this.setSelectedCurrency(0);
    }

    setSelectedCurrency(tabIndex: number) {
        this.selectedCurrency = this.currencies[tabIndex];
    }

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    editNew() {
        Notification.show("editNew");
        this.editedAccount = AccountDtoModel.createEmptyValue();
    }

    cancelEdit() {
        this.editedAccount = null;
    }
}

export const bookingsViewStore = new BookingsViewStore();