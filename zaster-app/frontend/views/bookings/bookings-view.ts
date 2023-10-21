import {customElement, query, state} from "lit/decorators.js";
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
import '@vaadin/context-menu'
import {Notification} from "@vaadin/notification";

import {TabsSelectedChangedEvent} from "@vaadin/tabs";
import {Grid} from "@vaadin/grid";
import './account-form';
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import {columnBodyRenderer} from "@vaadin/grid/lit";

interface Item {
    text: string,
    handler: () => void
}

@customElement('bookings-view')
export class BookingsView extends View {

    @query("vaadin-grid.accounts-grid")
    accountsGrid!: Grid;

    @state()
    private items : Item[] = [
        {text: 'View', handler: this.handleView},
        {text: 'Edit', handler: this.handleEdit},
        {text: 'Delete', handler: this.handleDelete}];

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
                        <vaadin-button @click=${this.editNew}>
                            <vaadin-icon icon="lumo:plus"></vaadin-icon>
                        </vaadin-button>
                    </div>
                    <vaadin-context-menu .items=${this.items} 
                                         class="h-full"
                                         @item-selected=${this.handleItemSelected}
                    >
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
                                    ${columnBodyRenderer(
                                            () => html`
                                                <vaadin-menu-bar .items=${this.items}
                                                                 theme="tertiary"
                                                                 @item-selected=${this.handleItemSelected}
                                                >
                                                </vaadin-menu-bar>
                                            `,
                                            []
                                    )}
                            ></vaadin-grid-column>
                        </vaadin-grid>
                    </vaadin-context-menu>
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

    handleItemSelected(ev: CustomEvent) {
        this.items
            .filter(item => item.text === ev.detail.value.text)
            .forEach(item => item.handler())
    }

    handleView() {
        Notification.show("View!");
    }

    handleEdit() {
        Notification.show("Edit!");
    }

    handleDelete() {
        Notification.show("Delete!");
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

    editNew() {
        bookingsViewStore.editNew();
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
