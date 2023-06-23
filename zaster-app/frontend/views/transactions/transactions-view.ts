import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from '../../views/view.js';

import '@vaadin/button';
import '@vaadin/checkbox';
import '@vaadin/text-field';
import '@vaadin/tabs';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/grid/vaadin-grid-sort-column';
import './transaction-form';
import {transactionsViewStore} from "Frontend/views/transactions/transactions-view-store.ts";

@customElement('transactions-view')
export class TransactionsView extends View {

    render() {
        return html`
            <div class="toolbar flex gap-s">
                <vaadin-text-field
                        placeholder="Filter nach Name"
                        .value=${transactionsViewStore.filterText}
                        @input=${this.updateFilter}
                        clear-button-visible
                ></vaadin-text-field>
                <vaadin-button @click=${transactionsViewStore.editNew}>
                    Währung hinzufügen
                </vaadin-button>
            </div>
            <div class="content flex gap-m h-full">
                <vaadin-grid
                        class="grid h-full"
                        .items=${transactionsViewStore.filteredCurrencies}
                        .selectedItems=${[transactionsViewStore.selectedCurrency]}
                        @active-item-changed=${this.handleGridSelection}>
                    <vaadin-grid-sort-column path="currencyCode" auto-width></vaadin-grid-sort-column>
                    <vaadin-grid-sort-column path="currencyName" auto-width></vaadin-grid-sort-column>
                    <vaadin-grid-column path="currencyType" auto-width></vaadin-grid-column>
                </vaadin-grid>
                <transaction-form
                        class="flex flex-col gap-s"
                        ?hidden=${!transactionsViewStore.selectedCurrency}
                ></transaction-form>
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
        transactionsViewStore.setSelectedCurrency(ev.detail.value);
    }

    updateFilter(ev: { target: HTMLInputElement }) {
        transactionsViewStore.updateFilter(ev.target.value);
    }

    async connectedCallback() {
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
        this.autorun(() => {
            if (transactionsViewStore.selectedCurrency) {
                this.classList.add("editing");
            } else {
                this.classList.remove("editing");
            }
        });
    }
}
