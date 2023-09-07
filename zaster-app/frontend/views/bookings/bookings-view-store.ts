import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import {Account} from "Frontend/model/tracking/Account.ts";
import {accountingStore} from "Frontend/stores/app-store.ts";
import {makeObservable, observable, runInAction} from "mobx";

class BookingsViewStore {
    selectedAccount: Account | null = null;
    boundDataProvider = this.dataProvider.bind(this);

    constructor() {
        makeObservable(this,
            {
                selectedAccount: observable.ref
            }
        );
    }

    async dataProvider(
        params: GridDataProviderParams<Account>,
        callback: GridDataProviderCallback<Account>
    ) {
        runInAction(() => {
            if (params.parentItem) {
                const parentItem: Account = params.parentItem;
                callback(parentItem.children || [], parentItem.children?.length);
            } else {
                callback(accountingStore._rootAccounts, accountingStore._rootAccounts.length);
            }
        });
    }

    setSelectedAccount(account: Account) {
        this.selectedAccount = account;
    }
}

export const bookingsViewStore = new BookingsViewStore();