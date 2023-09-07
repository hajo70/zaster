import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import {Account} from "Frontend/model/tracking/Account.ts";
import {accountingStore} from "Frontend/stores/app-store.ts";

class BookingsViewStore {

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

}

export const bookingsViewStore = new BookingsViewStore();