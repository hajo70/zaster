import {customElement} from "lit/decorators.js";
import {View} from "Frontend/views/view.ts";
import {html} from "lit";
import {bookingsViewStore} from "Frontend/views/bookings/bookings-view-store.ts";

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/grid/vaadin-grid-tree-column';
import '@vaadin/grid/vaadin-grid-sort-column';
import {TabsSelectedChangedEvent} from "@vaadin/tabs";

@customElement('bookings-view')
export class BookingsView extends View {

    protected override render() {
        return html`
            <div class="content flex gap-m h-full">
                <vaadin-grid class="accounts-grid flex-grow-0"
                        .itemHasChildrenPath="${'hasChildren'}"
                        .dataProvider="${bookingsViewStore.dataProvider}"
                        .selectedItems=${[bookingsViewStore.selectedAccount]}
                        @active-item-changed=${this.handleGridSelection}
                >
                    <vaadin-grid-tree-column path="accountName"></vaadin-grid-tree-column>
                </vaadin-grid>
                <div class="flex flex-col h-full flex-grow">
                    <vaadin-grid .items="${bookingsViewStore.transfers}">
                        <vaadin-grid-column path="description"></vaadin-grid-column>
                    </vaadin-grid>
                    <vaadin-tabs @selected-changed="${this.selectedChanged}">
                        ${bookingsViewStore.currencies.map(currency =>
                            html`
                                <vaadin-tab>${currency.currencyCode}</vaadin-tab>
                            `)}
                    </vaadin-tabs>
                </div>
            </div>
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
