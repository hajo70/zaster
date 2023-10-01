import {customElement, query} from "lit/decorators.js";
import {View} from "Frontend/views/view.ts";
import {html} from "lit";
import {bookingsViewStore} from "Frontend/views/bookings/bookings-view-store.ts";

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/grid/vaadin-grid-tree-column';
import '@vaadin/grid/vaadin-grid-sort-column';
import {TabsSelectedChangedEvent} from "@vaadin/tabs";
import {Grid} from "@vaadin/grid";
import './account-form';

@customElement('bookings-view')
export class BookingsView extends View {

    @query("vaadin-grid.accounts-grid")
    accountsGrid!: Grid;

    protected override render() {
        return html`
            <div class="content flex gap-m h-full">
                <vaadin-grid class="accounts-grid flex-grow-0" theme="compact no-border"
                        .itemHasChildrenPath="${'hasChildren'}"
                        .dataProvider="${bookingsViewStore.dataProvider}"
                        .selectedItems=${[bookingsViewStore.selectedAccount]}
                        @active-item-changed=${this.handleGridSelection}
                >
                    <vaadin-grid-tree-column path="accountName"></vaadin-grid-tree-column>
                </vaadin-grid>
                <div class="flex flex-col h-full flex-grow">
                    <vaadin-grid .items="${bookingsViewStore.transfers}" theme="compact" column-reordering-allowed>
                        <vaadin-grid-sort-column path="bookingDate" resizable></vaadin-grid-sort-column>
                        <vaadin-grid-sort-column path="amount" resizable></vaadin-grid-sort-column>
                        <vaadin-grid-sort-column path="description" resizable></vaadin-grid-sort-column>
                    </vaadin-grid>
                    <vaadin-tabs @selected-changed="${this.selectedChanged}">
                        ${bookingsViewStore.currencies.map(currency =>
                            html`
                                <vaadin-tab>${currency.currencyCode}</vaadin-tab>
                            `)}
                    </vaadin-tabs>
                </div>
            </div>
            <account-form
                    class="flex flex-col gap-s"
                    ?hidden=${!bookingsViewStore.selectedAccount}
                    @accounts-changed=${this.updateGrid}
            ></account-form>
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

    selectedChanged(e: TabsSelectedChangedEvent) {
        bookingsViewStore.setSelectedCurrency(e.detail.value);
    }

    updateGrid() {
        this.accountsGrid.clearCache();
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
