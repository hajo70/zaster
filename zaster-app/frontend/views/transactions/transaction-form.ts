import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';
import '@vaadin/button';
import '@vaadin/combo-box';
import '@vaadin/text-field';
import {Binder, field} from '@hilla/form';
import {transactionsViewStore} from "Frontend/views/transactions/transactions-view-store.ts";
import CurrencyDtoModel from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDtoModel.ts";

@customElement('transaction-form')
export class TransactionForm extends View {
    protected binder = new Binder(this, CurrencyDtoModel);

    constructor() {
        super();
        this.autorun(() => {
            if (transactionsViewStore.selectedCurrency) {
                this.binder.read(transactionsViewStore.selectedCurrency);
            } else {
                this.binder.clear();
            }
        });
    }

    render() {
        const {model} = this.binder;

        return html`
            <vaadin-text-field
                    label="Währungscode"
                    ${field(model.currencyCode)}
            ></vaadin-text-field>
            <vaadin-text-field
                    label="Währungsname"
                    ${field(model.currencyName)}
            ></vaadin-text-field>

            <div class="flex gap-s">
                <vaadin-button theme="primary" @click=${this.save}>
                    ${this.binder.value.id ? 'Speichern' : 'Erstellen'}
                </vaadin-button>
                <vaadin-button theme="error" @click=${transactionsViewStore.delete}>
                    Löschen
                </vaadin-button>
                <vaadin-button theme="tertiary" @click=${transactionsViewStore.cancelEdit}>
                    Abbrechen
                </vaadin-button>
            </div>
        `;
    }

    async save() {
        await this.binder.submitTo(transactionsViewStore.save);
        this.binder.clear();
    }
}