import {html} from 'lit';
import {customElement, query} from 'lit/decorators.js';
import {View} from '../../views/view.js';

import '@vaadin/button';
import '@vaadin/checkbox';
import '@vaadin/text-field';
import '@vaadin/tabs';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/grid/vaadin-grid-tree-column';
import '@vaadin/grid/vaadin-grid-sort-column';
import './account-form';

import {columnBodyRenderer, GridColumnBodyLitRenderer} from "@vaadin/grid/lit";
import {accountsViewStore} from "Frontend/views/accounts/accounts-view-store.ts";
import {Grid} from "@vaadin/grid";
import {Account} from "Frontend/model/tracking/Account.ts";

@customElement('accounts-view')
export class AccountsView extends View {

    @query("vaadin-grid")
    grid!: Grid;

    protected override render() {
        return html`
            <div class="toolbar flex gap-s">
                <vaadin-text-field
                        placeholder="Filter"
                        .value=${accountsViewStore.filterText}
                        @input=${this.updateFilter}
                        clear-button-visible
                ></vaadin-text-field>
                <vaadin-button @click=${accountsViewStore.editNew}>
                    Add Account
                </vaadin-button>
            </div>
            <div class="content flex gap-m h-full">
                <vaadin-grid
                        .itemHasChildrenPath="${'hasChildren'}"
                        .dataProvider="${accountsViewStore.boundDataProvider}"
                        .selectedItems=${[accountsViewStore.selectedAccount]}
                        @active-item-changed=${this.handleGridSelection}
                >
                    <vaadin-grid-tree-column path="data.accountName"></vaadin-grid-tree-column>
                    <vaadin-grid-column path="data.accountCode"></vaadin-grid-column>
                    <vaadin-grid-column header="Currencies" auto-width
                                        ${columnBodyRenderer(this.currenciesRenderer, [])}></vaadin-grid-column>
                </vaadin-grid>
                <account-form
                        class="flex flex-col gap-s"
                        ?hidden=${!accountsViewStore.selectedAccount}
                        @accounts-changed=${this.updateGrid}
                ></account-form>
            </div>
        `;
    }

    private currenciesRenderer: GridColumnBodyLitRenderer<Account> = (account) => html`
        <span>${account.accountCurrencies
                .map(accountCurrency => accountCurrency.currency.currencyCode)
                .join(', ')}</span>
    `;

    // vaadin-grid fires a null-event when initialized. Ignore it.
    firstSelectionEvent = true;

    handleGridSelection(ev: CustomEvent) {
        if (this.firstSelectionEvent) {
            this.firstSelectionEvent = false;
            return;
        }
        accountsViewStore.setSelectedAccount(ev.detail.value);
    }

    updateFilter(ev: { target: HTMLInputElement }) {
        accountsViewStore.updateFilter(ev.target.value);
    }

    updateGrid() {
        this.grid.clearCache();
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
    }
}