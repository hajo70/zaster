import {html} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';

import '@vaadin/button';
import '@vaadin/combo-box';
import '@vaadin/text-field';

import {Binder, field} from '@hilla/form';
import TransactionDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDtoModel.ts";

@customElement('tx-form')
export class TxForm extends View {

    @property()
    transaction = TransactionDtoModel.createEmptyValue();

    protected binder = new Binder(this, TransactionDtoModel);

    protected override render() {
        const {model} = this.binder;

        return html`
            <vaadin-text-field
                    label="Beschreibung"
                    ${field(model.description)}
            ></vaadin-text-field>
        `;
    }
}