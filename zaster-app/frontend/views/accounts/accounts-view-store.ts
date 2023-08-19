import {makeObservable, observable, runInAction} from "mobx";
import {AccountingEndpoint} from "Frontend/generated/endpoints.ts";
import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import {accountingStore} from "Frontend/stores/app-store.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import AccountDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDtoModel.ts";
import {Account} from "Frontend/model/tracking/Account.ts";

class AccountsViewStore {
    selectedAccount: AccountDto | null = null;
    selectedAccountParent: Account | null = null;
    filterText = '';
    boundDataProvider = this.dataProvider.bind(this);

    constructor() {
        makeObservable(this,
            {
                selectedAccount: observable.ref,
                filterText: observable
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
        })
    }

    updateFilter(filterText: string) {
        this.filterText = filterText;
    }

    setSelectedAccount(account: AccountDto) {
        this.selectedAccount = account;
    }

    editNew() {
        this.selectedAccount = AccountDtoModel.createEmptyValue();
    }

    cancelEdit() {
        this.selectedAccount = null;
    }

    async save(account: AccountDto) {
        await this.saveAccount(account);
        this.cancelEdit();
    }

    async delete() {
        if (this.selectedAccount) {
            await this.deleteAccount(this.selectedAccount);
            this.cancelEdit();
        }
    }

    async saveAccount(account: AccountDto) {
        try {
            const saved = await AccountingEndpoint.saveAccountGroup(account);
            if (saved) {
                this.saveLocal(saved);
            } else {
                console.log('AccountDto save failed');
            }
        } catch (ex) {
            console.log('AccountDto save failed: ' + ex);
        }
    }

    async deleteAccount(account: AccountDto) {
        if (!account.id) return;

        try {
            await AccountingEndpoint.deleteAccountGroupById(account.id.uuid);
            // this.deleteLocal(this.selectedAccountParent, account);
        } catch (ex) {
            console.log('AccountDto delete failed: ' + ex);
        }
    }

    get allAccountGroups(): AccountDto[] {
        // let groups = this.rootAccounts?.flatMap(this.ancestors);
        // console.log("groups total: " + groups?.length + ", roots: " + this.rootAccounts?.length);
        return [];
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
        //if (this.selectedAccountParent !== parent) {
        //     this.deleteLocal(this.selectedAccountParent, saved);
        // }
        if (parent) {
            parent.children = this.replaceSaved(parent.children, saved);
        } else {
            // this.rootAccounts = this.replaceSaved(this.rootAccounts, saved);
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
            // this.rootAccounts = this.removeDeleted(this.rootAccounts, deleted);
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