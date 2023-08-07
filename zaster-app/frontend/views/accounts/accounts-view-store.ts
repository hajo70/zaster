import {makeAutoObservable, observable} from "mobx";
import {AccountingEndpoint} from "Frontend/generated/endpoints.ts";
import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import {accountingStore} from "Frontend/stores/app-store.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import AccountDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDtoModel.ts";

class AccountsViewStore {
    rootAccounts: AccountDto[] | undefined;
    selectedAccount: AccountDto | null = null;
    selectedAccountParent: AccountDto | undefined;
    filterText = '';

    constructor() {
        makeAutoObservable(
            this,
            {
                boundDataProvider: false,
                allAccountGroups: false,
                rootAccounts: observable.deep,
                selectedAccount: observable.ref
            },
        {autoBind: true}
        );
    }

    async dataProvider(
        params: GridDataProviderParams<AccountDto>,
        callback: GridDataProviderCallback<AccountDto>
    ){
        if (params.parentItem) {
            const parentItem: AccountDto = params.parentItem;
            callback(parentItem.children || [], parentItem.children?.length);
        } else {
            if (!this.rootAccounts) {
                this.rootAccounts = await AccountingEndpoint.findAllRootAccountGroups();
            }
            callback(this.rootAccounts, this.rootAccounts.length);
        }
    }

    boundDataProvider = this.dataProvider.bind(this);

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    setSelectedAccountGroup(accountGroup: AccountDto) {
        this.selectedAccount = accountGroup;
        this.selectedAccountParent = this.parent(accountGroup);
    }

    editNew() {
        this.selectedAccount = AccountDtoModel.createEmptyValue();
    }

    cancelEdit() {
        this.selectedAccount = null;
    }

    async save(accountGroup: AccountDto) {
        await this.saveAccountGroup(accountGroup);
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedAccount) {
            await this.deleteAccountGroup(this.selectedAccount);
            this.cancelEdit();
        }
    }

    async saveAccountGroup(accountGroup: AccountDto) {
        try {
            const saved = await AccountingEndpoint.saveAccountGroup(accountGroup);
            if (saved) {
                this.saveLocal(saved);
            } else {
                console.log('AccountDto save failed');
            }
        } catch (ex) {
            console.log('AccountDto save failed: ' + ex);
        }
    }

    async deleteAccountGroup(accountGroup: AccountDto) {
        if (!accountGroup.id) return;

        try {
            await AccountingEndpoint.deleteAccountGroupById(accountGroup.id.uuid);
            this.deleteLocal(this.selectedAccountParent, accountGroup);
        } catch (ex) {
            console.log('AccountDto delete failed: ' + ex);
        }
    }

    get allAccountGroups() {
        let groups = this.rootAccounts?.flatMap(this.ancestors);
        console.log("groups total: " + groups?.length + ", roots: " + this.rootAccounts?.length);
        return groups;
    }

    private ancestors(group: AccountDto): AccountDto[] {
        if (!group) {
            return [];
        }
        if (!group.children) {
            return [group];
        }
        return [group, ...group.children.flatMap(this.ancestors)];
    }

    private parent(group: AccountDto): AccountDto | undefined {
        if (!group.parentId) {
            return undefined;
        }
        return this.allAccountGroups?.find(ag => ag.id.uuid === group.parentId);
    }

    private saveLocal(saved: AccountDto) {
        const parent = this.parent(saved);
        if (this.selectedAccountParent !== parent) {
            this.deleteLocal(this.selectedAccountParent, saved);
        }
        if (parent) {
            parent.children = this.replaceSaved(parent.children, saved);
        } else {
            this.rootAccounts = this.replaceSaved(this.rootAccounts, saved);
        }
    }

    private replaceSaved(list: AccountDto[] | undefined, saved: AccountDto) {
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

    private deleteLocal(parent: AccountDto | undefined, deleted: AccountDto) {
        if (parent) {
            parent.children = this.removeDeleted(parent.children, deleted) || [];
        } else {
            this.rootAccounts = this.removeDeleted(this.rootAccounts, deleted);
        }
    }

    private removeDeleted(list: AccountDto[] | undefined, deleted: AccountDto) {
        return list?.filter(ag => ag.id.uuid !== deleted.id.uuid);
    }

    get currencyCodes() {
        return accountingStore.currencies.map(currency => currency.currencyCode);
    }
}

export const accountsViewStore = new AccountsViewStore();