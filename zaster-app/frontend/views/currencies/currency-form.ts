import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';
import '@vaadin/button';
import '@vaadin/combo-box';
import '@vaadin/text-field';
import {Binder, field} from '@hilla/form';
import CurrencyModel from "Frontend/generated/de/spricom/zaster/endpoints/model/CurrencyModel";
import {currenciesViewStore} from "Frontend/views/currencies/currencies-view-store";

@customElement('currency-form')
export class CurrencyForm extends View {
    protected binder = new Binder(this, CurrencyModel);

    constructor() {
        super();
        this.autorun(() => {
            if (currenciesViewStore.selectedCurrency) {
                this.binder.read(currenciesViewStore.selectedCurrency);
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
                    ${field(model.name)}
            ></vaadin-text-field>

            <div class="flex gap-s">
                <vaadin-button theme="primary" @click=${this.save}>
                    ${this.binder.value.id ? 'Speichern' : 'Erstellen'}
                </vaadin-button>
                <vaadin-button theme="error" @click=${currenciesViewStore.delete}>
                    Löschen
                </vaadin-button>
                <vaadin-button theme="tertiary" @click=${currenciesViewStore.cancelEdit}>
                    Abbrechen
                </vaadin-button>
            </div>
        `;
    }

    async save() {
        await this.binder.submitTo(currenciesViewStore.save);
        this.binder.clear();
    }
}