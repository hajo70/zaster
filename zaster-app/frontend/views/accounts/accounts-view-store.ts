import {makeAutoObservable, observable} from "mobx";
import {AccountingEndpoint} from "Frontend/generated/endpoints.ts";
import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import {accountingStore} from "Frontend/stores/app-store.ts";
import AccountGroupDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountGroupDto.ts";
import AccountGroupDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountGroupDtoModel.ts";

class AccountsViewStore {
    rootAccountGroups: AccountGroupDto[] | undefined;
    selectedAccountGroup: AccountGroupDto | null = null;
    selectedAccountGroupParent: AccountGroupDto | undefined;
    filterText = '';

    constructor() {
        makeAutoObservable(
            this,
            {
                boundDataProvider: false,
                allAccountGroups: false,
                rootAccountGroups: observable.deep,
                selectedAccountGroup: observable.ref
            },
        {autoBind: true}
        );
    }

    async dataProvider(
        params: GridDataProviderParams<AccountGroupDto>,
        callback: GridDataProviderCallback<AccountGroupDto>
    ){
        if (params.parentItem) {
            const parentItem: AccountGroupDto = params.parentItem;
            callback(parentItem.children || [], parentItem.children?.length);
        } else {
            if (!this.rootAccountGroups) {
                this.rootAccountGroups = await AccountingEndpoint.findAllRootAccountGroups();
            }
            callback(this.rootAccountGroups, this.rootAccountGroups.length);
        }
    }

    boundDataProvider = this.dataProvider.bind(this);

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    setSelectedAccountGroup(accountGroup: AccountGroupDto) {
        this.selectedAccountGroup = accountGroup;
        this.selectedAccountGroupParent = this.parent(accountGroup);
    }

    editNew() {
        this.selectedAccountGroup = AccountGroupDtoModel.createEmptyValue();
    }

    cancelEdit() {
        this.selectedAccountGroup = null;
    }

    async save(accountGroup: AccountGroupDto) {
        await this.saveAccountGroup(accountGroup);
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedAccountGroup) {
            await this.deleteAccountGroup(this.selectedAccountGroup);
            this.cancelEdit();
        }
    }

    async saveAccountGroup(accountGroup: AccountGroupDto) {
        try {
            const saved = await AccountingEndpoint.saveAccountGroup(accountGroup);
            if (saved) {
                this.saveLocal(saved);
            } else {
                console.log('AccountGroupDto save failed');
            }
        } catch (ex) {
            console.log('AccountGroupDto save failed: ' + ex);
        }
    }

    async deleteAccountGroup(accountGroup: AccountGroupDto) {
        if (!accountGroup.id) return;

        try {
            await AccountingEndpoint.deleteAccountGroupById(accountGroup.id.uuid);
            this.deleteLocal(this.selectedAccountGroupParent, accountGroup);
        } catch (ex) {
            console.log('AccountGroupDto delete failed: ' + ex);
        }
    }

    get allAccountGroups() {
        let groups = this.rootAccountGroups?.flatMap(this.ancestors);
        console.log("groups total: " + groups?.length + ", roots: " + this.rootAccountGroups?.length);
        return groups;
    }

    private ancestors(group: AccountGroupDto): AccountGroupDto[] {
        if (!group) {
            return [];
        }
        if (!group.children) {
            return [group];
        }
        return [group, ...group.children.flatMap(this.ancestors)];
    }

    private parent(group: AccountGroupDto): AccountGroupDto | undefined {
        if (!group.parentId) {
            return undefined;
        }
        return this.allAccountGroups?.find(ag => ag.id.uuid === group.parentId);
    }

    private saveLocal(saved: AccountGroupDto) {
        const parent = this.parent(saved);
        if (this.selectedAccountGroupParent !== parent) {
            this.deleteLocal(this.selectedAccountGroupParent, saved);
        }
        if (parent) {
            parent.children = this.replaceSaved(parent.children, saved);
        } else {
            this.rootAccountGroups = this.replaceSaved(this.rootAccountGroups, saved);
        }
    }

    private replaceSaved(list: AccountGroupDto[] | undefined, saved: AccountGroupDto) {
        if (!list) {
            return [saved];
        }
        const accountGroupExists = list?.some((ag) => ag.id.uuid === saved.id.uuid);
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

    private deleteLocal(parent: AccountGroupDto | undefined, deleted: AccountGroupDto) {
        if (parent) {
            parent.children = this.removeDeleted(parent.children, deleted) || [];
        } else {
            this.rootAccountGroups = this.removeDeleted(this.rootAccountGroups, deleted);
        }
    }

    private removeDeleted(list: AccountGroupDto[] | undefined, deleted: AccountGroupDto) {
        return list?.filter(ag => ag.id.uuid !== deleted.id.uuid);
    }

    get currencyCodes() {
        return accountingStore.currencies.map(currency => currency.currencyCode);
    }
}

export const accountsViewStore = new AccountsViewStore();