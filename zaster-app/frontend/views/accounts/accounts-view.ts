import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
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
import {currenciesViewStore} from "Frontend/views/currencies/currencies-view-store.ts";

@customElement('accounts-view')
export class AccountsView extends View {

    protected override render() {
        return html`
            <div class="content flex gap-m h-full">

                <vaadin-grid 
                        .itemHasChildrenPath="${'children'}" 
                        .dataProvider="${accountsViewStore.dataProvider}"
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
        accountsViewStore.selectedAccountGroup = ev.detail.value;
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