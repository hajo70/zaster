import {customElement, query} from "lit/decorators.js";
import {View} from "Frontend/views/view.ts";
import {html} from "lit";
import {bookingsViewStore} from "Frontend/views/bookings/bookings-view-store.ts";

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/grid/vaadin-grid-tree-column';
import '@vaadin/grid/vaadin-grid-sort-column';
import '@vaadin/split-layout';
import '@vaadin/icon'
import '@vaadin/menu-bar'

import {TabsSelectedChangedEvent} from "@vaadin/tabs";
import {Grid} from "@vaadin/grid";
import './account-form';
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import {columnBodyRenderer, GridColumnBodyLitRenderer} from "@vaadin/grid/lit";
import {Account} from "Frontend/model/tracking/Account.ts";

interface Item {
    text: string,
    handler: () => void
}

@customElement('bookings-view')
export class BookingsView extends View {

    @query("vaadin-grid.accounts-grid")
    accountsGrid!: Grid;

    protected override render() {
        return html`
            <vaadin-split-layout class="h-full">
                <div class="flex flex-col flex-grow-0">
                    <div class="toolbar flex gap-s">
                        <vaadin-text-field
                                placeholder="Filter"
                                .value=${bookingsViewStore.filterText}
                                @input=${this.updateFilter}
                                clear-button-visible
                        ></vaadin-text-field>
                        <vaadin-button @click=${this.onAddTopLevel}>
                            <vaadin-icon icon="lumo:plus"></vaadin-icon>
                        </vaadin-button>
                    </div>
                    <vaadin-grid theme="compact"
                                 class="accounts-grid h-full"
                                 .itemHasChildrenPath="${'hasChildren'}"
                                 .dataProvider="${bookingsViewStore.dataProvider}"
                                 .selectedItems=${[bookingsViewStore.selectedAccount]}
                                 @active-item-changed=${this.handleGridSelection}
                    >
                        <vaadin-grid-tree-column path="accountName"></vaadin-grid-tree-column>
                        <vaadin-grid-column
                                width="50px"
                                flex-grow="0"
                                ${columnBodyRenderer(this.menuRenderer, [])}
                        ></vaadin-grid-column>
                    </vaadin-grid>
                </div>
                <div class="flex flex-col h-full flex-grow">
                    <vaadin-grid .items="${bookingsViewStore.transfers}" theme="compact" column-reordering-allowed>
                        <vaadin-grid-sort-column path="bookingDate" resizable></vaadin-grid-sort-column>
                        <vaadin-grid-sort-column path="amount" resizable></vaadin-grid-sort-column>
                        <vaadin-grid-sort-column path="description" resizable></vaadin-grid-sort-column>
                    </vaadin-grid>
                    <vaadin-tabs @selected-changed="${this.selectedChanged}">
                        ${bookingsViewStore.currencies.map(currency => this.renderCurrency(currency))}
                    </vaadin-tabs>
                </div>
            </vaadin-split-layout>
            <account-form
                    class="flex flex-col gap-s"
                    ?hidden=${!bookingsViewStore.editedAccount}
                    @accounts-changed=${this.updateAccountsGrid}
            ></account-form>
        `;
    }

    private renderCurrency(currency: CurrencyDto) {
        return html`
            <vaadin-tab>${currency.currencyCode}</vaadin-tab>
        `;
    }

    // vaadin-grid fires a null-event when initialized. Ignore it.
    firstSelectionEvent = true;

    handleGridSelection(ev: CustomEvent) {
        if (this.firstSelectionEvent) {
            this.firstSelectionEvent = false;
            return;
        }
        bookingsViewStore.setSelectedAccount(ev.detail.value);
    }

    private menuRenderer: GridColumnBodyLitRenderer<Account> = (account) => {
        let menuItems : Item[] = [
            {text: 'Add', handler: () => this.onAdd(account)},
            {text: 'Edit', handler: () => this.onEdit(account)}
        ];
        if (!account.hasChildren) {
            menuItems.push(
                {text: 'Delete', handler: () => this.onDelete(account)}
            )
        }
        return html`
        <vaadin-menu-bar .items=${menuItems}
                         theme="tertiary"
                         @item-selected=${this.handleItemSelected}
        >
        </vaadin-menu-bar>
    `;}

    handleItemSelected(ev: CustomEvent) {
        const item = ev.detail.value as Item;
        item.handler();
    }

    onAdd(account: Account) {
        bookingsViewStore.editNew(account);
    }

    onEdit(account: Account) {
        bookingsViewStore.editCurrent(account);
    }

    onDelete(account: Account) {
        bookingsViewStore.editCurrent(account);
    }

    onAddTopLevel() {
        bookingsViewStore.editNew(null);
    }

    selectedChanged(e: TabsSelectedChangedEvent) {
        bookingsViewStore.setSelectedCurrency(e.detail.value);
    }

    updateAccountsGrid() {
        this.accountsGrid.clearCache();
    }

    updateFilter(ev: { target: HTMLInputElement }) {
        bookingsViewStore.updateFilter(ev.target.value);
        this.updateAccountsGrid();
    }

    connectedCallback() {
        super.connectedCallback();
        this.classList.add(
            'box-border',
            'flex',
            'flex-col',
            'p-m',
            'gap-s',
            'w-full',
            'h-full'
        );
    }
}
