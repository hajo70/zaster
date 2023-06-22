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

import AccountGroup from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroup";
import {columnBodyRenderer, GridColumnBodyLitRenderer} from "@vaadin/grid/lit";
import {accountsViewStore} from "Frontend/views/accounts/accounts-view-store.ts";
import {Grid} from "@vaadin/grid";

@customElement('accounts-view')
export class AccountsView extends View {

    @query("vaadin-grid")
    grid!: Grid;

    protected override render() {
        return html`
            <div class="toolbar flex gap-s">
                <vaadin-text-field
                        placeholder="Filter nach Name"
                        .value=${accountsViewStore.filterText}
                        @input=${this.updateFilter}
                        clear-button-visible
                ></vaadin-text-field>
                <vaadin-button @click=${accountsViewStore.editNew}>
                    Konto hinzufügen
                </vaadin-button>
            </div>
            <div class="content flex gap-m h-full">
                <vaadin-grid 
                        .itemHasChildrenPath="${'children'}" 
                        .dataProvider="${accountsViewStore.boundDataProvider}"
                        .selectedItems=${[accountsViewStore.selectedAccountGroup]}
                        @active-item-changed=${this.handleGridSelection}
                >
                    <vaadin-grid-tree-column path="accountName"></vaadin-grid-tree-column>
                    <vaadin-grid-column header="Currencies" auto-width
                                        ${columnBodyRenderer(this.currenciesRenderer, [])}></vaadin-grid-column>
                </vaadin-grid>
                <account-form
                        class="flex flex-col gap-s"
                        ?hidden=${!accountsViewStore.selectedAccountGroup}
                        @accounts-changed=${this.updateGrid}
                ></account-form>
            </div>
        `;
    }

    private currenciesRenderer: GridColumnBodyLitRenderer<AccountGroup> = ({currencyCodes}) => html`
        <span>${currencyCodes.join(', ')}</span>
    `;

    // vaadin-grid fires a null-event when initialized. Ignore it.
    firstSelectionEvent = true;
    handleGridSelection(ev: CustomEvent) {
        if (this.firstSelectionEvent) {
            this.firstSelectionEvent = false;
            return;
        }
        accountsViewStore.setSelectedAccountGroup(ev.detail.value);
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