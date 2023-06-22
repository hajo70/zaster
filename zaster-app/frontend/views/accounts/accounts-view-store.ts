import AccountGroup from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroup.ts";
import {makeAutoObservable, observable} from "mobx";
import {AccountEndpoint} from "Frontend/generated/endpoints.ts";
import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import AccountGroupModel from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroupModel.ts";
import {saveAccountGroup} from "Frontend/generated/AccountEndpoint.ts";

class AccountsViewStore {
    selectedAccountGroup: AccountGroup | null = null;
    filterText = '';

    constructor() {
        makeAutoObservable(
            this,
            {
                dataProvider: false,
                selectedAccountGroup: observable.ref
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

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    setSelectedAccountGroup(accountGroup: AccountGroup) {
        this.selectedAccountGroup = accountGroup;
    }

    editNew() {
        this.selectedAccountGroup = AccountGroupModel.createEmptyValue();
    }

    cancelEdit() {
        this.selectedAccountGroup = null;
    }

    async save(accountGroup: AccountGroup) {
        await saveAccountGroup(accountGroup);
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedAccountGroup) {
            await this.deleteAccountGroup(this.selectedAccountGroup);
            this.cancelEdit();
        }
    }

    async saveAccountGroup(accountGroup: AccountGroup) {
        try {
            const saved = await AccountEndpoint.saveAccountGroup(accountGroup);
            if (saved) {
                // this.saveLocal(saved);
            } else {
                console.log('AccountGroup save failed');
            }
        } catch (ex) {
            console.log('AccountGroup save failed: ' + ex);
        }
    }

    async deleteAccountGroup(accountGroup: AccountGroup) {
        if (!accountGroup.id) return;

        try {
            await AccountEndpoint.deleteAccountGroupById(accountGroup.id);
            // this.deleteLocal(currency);
        } catch (ex) {
            console.log('AccountGroup delete failed: ' + ex);
        }
    }

}

export const accountsViewStore = new AccountsViewStore();