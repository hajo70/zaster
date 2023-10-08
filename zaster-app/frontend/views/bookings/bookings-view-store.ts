import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import {Account} from "Frontend/model/tracking/Account.ts";
import {accountingStore, trackingStore} from "Frontend/stores/app-store.ts";
import {action, makeObservable, observable} from "mobx";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import {Transfer} from "Frontend/model/tracking/Transfer.ts";
import {Notification} from "@vaadin/notification";

class BookingsViewStore {
    selectedAccount: Account | null = null;
    selectedCurrency: CurrencyDto | null = null;

    constructor() {
        makeObservable(this,
            {
                selectedAccount: observable.ref,
                selectedCurrency: observable.ref,
                setSelectedAccount: action,
                setSelectedCurrency: action,
                cancelEdit: action
            }
        );
    }

    dataProvider(
        params: GridDataProviderParams<Account>,
        callback: GridDataProviderCallback<Account>
    ) {
        if (params.parentItem) {
            const parentItem: Account = params.parentItem;
            callback(parentItem.children || [], parentItem.children?.length);
        } else {
            callback(accountingStore._rootAccounts, accountingStore._rootAccounts.length);
        }
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

    cancelEdit() {
        Notification.show("cancel edit");
        this.selectedAccount = null;
    }
}

export const bookingsViewStore = new BookingsViewStore();