import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from '../../views/view.js';
import {state} from "lit/decorators";

import '@vaadin/button';
import '@vaadin/checkbox';
import '@vaadin/text-field';
import '@vaadin/tabs';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import './currency-form';

import Currency from "Frontend/generated/de/spricom/zaster/endpoints/model/Currency";
import {findAllCurrencies} from "Frontend/generated/CurrenciesEndpoint";
import {currenciesViewStore} from "Frontend/views/currencies/currencies-view-store";

@customElement('currencies-view')
export class CurrenciesView extends View {

    @state()
    private currencies: Currency[] = [];

    render() {
        return html`
            <div class="toolbar flex gap-s">
                <vaadin-text-field
                        placeholder="Filter nach Name"
                        .value=${currenciesViewStore.filterText}
                        @input=${this.updateFilter}
                        clear-button-visible
                ></vaadin-text-field>
                <vaadin-button @click=${currenciesViewStore.editNew}>
                    Währung hinzufügen
                </vaadin-button>
            </div>
            <div class="content flex gap-m h-full">
                <vaadin-grid
                        class="grid h-full"
                        .items=${this.currencies}
                        .selectedItems=${[currenciesViewStore.selectedCurrency]}
                        @active-item-changed=${this.handleGridSelection}>
                    <vaadin-grid-column path="currencyCode" auto-width></vaadin-grid-column>
                    <vaadin-grid-column path="name" auto-width></vaadin-grid-column>
                </vaadin-grid>
                <currency-form
                        class="flex flex-col gap-s"
                        ?hidden=${!currenciesViewStore.selectedCurrency}
                ></currency-form>
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
        currenciesViewStore.setSelectedCurrency(ev.detail.value);
    }

    updateFilter(ev: { target: HTMLInputElement }) {
        currenciesViewStore.updateFilter(ev.target.value);
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
            if (currenciesViewStore.selectedCurrency) {
                this.classList.add("editing");
            } else {
                this.classList.remove("editing");
            }
        });
        this.currencies = await findAllCurrencies();
    }
}
