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
import {GridDataProviderCallback, GridDataProviderParams} from "@vaadin/grid";
import AccountGroup from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroup";
import {AccountEndpoint} from "Frontend/generated/endpoints";
import {columnBodyRenderer, GridColumnBodyLitRenderer} from "@vaadin/grid/lit";

@customElement('accounts-view')
export class AccountsView extends View {

    protected override render() {
        return html`
            <vaadin-grid .itemHasChildrenPath="${'children'}" .dataProvider="${this.dataProvider}">
                <vaadin-grid-tree-column path="accountName"></vaadin-grid-tree-column>
                <vaadin-grid-column header="Currencies" auto-width ${columnBodyRenderer(this.currenciesRenderer, [])}></vaadin-grid-column>
            </vaadin-grid>
        `;
    }

    private currenciesRenderer: GridColumnBodyLitRenderer<AccountGroup> = ({currencyCodes}) => html`
        <span>${currencyCodes.join(', ')}</span>
    `;

    async dataProvider(
        params: GridDataProviderParams<AccountGroup>,
        callback: GridDataProviderCallback<AccountGroup>
    ) {
        if (params.parentItem) {
            const parentItem: AccountGroup = params.parentItem;
            callback(parentItem.children, parentItem.children.length);
        } else {
            const roots = await AccountEndpoint.findAllRootAccountGroups();
            callback(roots, 1);
        }
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