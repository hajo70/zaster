import AccountGroup from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroup.ts";
import {makeAutoObservable, observable} from "mobx";
import {AccountEndpoint} from "Frontend/generated/endpoints.ts";
import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import AccountGroupModel from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroupModel.ts";

class AccountsViewStore {
    rootAccountGroups: AccountGroup[] | undefined;
    selectedAccountGroup: AccountGroup | null = null;
    filterText = '';

    constructor() {
        makeAutoObservable(
            this,
            {
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
            if (!this.rootAccountGroups) {
                this.rootAccountGroups = await AccountEndpoint.findAllRootAccountGroups();
            }
            callback(this.rootAccountGroups, this.rootAccountGroups.length);
        }
    }

    boundDataProvider = this.dataProvider.bind(this);

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
        await this.saveAccountGroup(accountGroup);
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
                this.saveLocal(saved);
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
            this.deleteLocal(accountGroup);
        } catch (ex) {
            console.log('AccountGroup delete failed: ' + ex);
        }
    }

    get allAccountGroups() {
        return this.rootAccountGroups?.flatMap(this.ancestors);
    }

    private ancestors(group: AccountGroup): AccountGroup[] {
        return [group, ...group.children.flatMap(this.ancestors)];
    }

    private parent(group: AccountGroup): AccountGroup | undefined {
        if (!group.parentId) {
            return undefined;
        }
        return this.allAccountGroups?.find(ag => ag.id === group.parentId);
    }

    private saveLocal(saved: AccountGroup) {
        const parent = this.parent(saved);
        if (parent) {
            parent.children = this.replaceSaved(parent.children, saved);
        } else {
            this.rootAccountGroups = this.replaceSaved(this.rootAccountGroups, saved);
        }
    }

    private replaceSaved(list: AccountGroup[] | undefined, saved: AccountGroup) {
        if (!list) {
            return [saved];
        }
        const accountGroupExists = list?.some((ag) => ag.id === saved.id);
        if (accountGroupExists) {
            return list.map((existing) => {
                if (existing.id === saved.id) {
                    return saved;
                } else {
                    return existing;
                }
            });
        } else {
            return [...list, saved];
        }
    }

    private deleteLocal(deleted: AccountGroup) {
        const parent = this.parent(deleted);
        if (parent) {
            parent.children = this.removeDeleted(parent.children, deleted) || [];
        } else {
            this.rootAccountGroups = this.removeDeleted(this.rootAccountGroups, deleted);
        }
    }

    private removeDeleted(list: AccountGroup[] | undefined, deleted: AccountGroup) {
        return list?.filter(ag => ag.id !== deleted.id);
    }
}

export const accountsViewStore = new AccountsViewStore();