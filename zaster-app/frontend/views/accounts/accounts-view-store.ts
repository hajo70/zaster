import AccountGroup from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroup.ts";
import {makeAutoObservable, observable} from "mobx";
import {AccountEndpoint} from "Frontend/generated/endpoints.ts";
import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity.ts";

class AccountsViewStore {
    selectedAccountGroup: AccountGroup | null = null;

    constructor() {
        makeAutoObservable(
            this,
            {
                dataProvider: false
            },
        {autoBind: true}
        );
    }

    async dataProvider(
        params: GridDataProviderParams<AccountGroup>,
        callback: GridDataProviderCallback<AccountGroup>
    ){
        if (params.parentItem) {
            const parentItem: AccountGroup = params.parentItem;
            callback(parentItem.children, parentItem.children.length);
        } else {
            const roots = await AccountEndpoint.findAllRootAccountGroups();
            callback(roots, roots.length);
        }
    }

    cancelEdit() {
        this.selectedAccountGroup = null;
    }

    async save(accountGroup: AccountGroup) {
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedAccountGroup) {
            this.cancelEdit();
        }
    }
}

export const accountsViewStore = new AccountsViewStore();