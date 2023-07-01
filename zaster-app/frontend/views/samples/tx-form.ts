import {html} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';

import '@vaadin/text-field';
import '@vaadin/date-time-picker';

import {Binder, field} from '@hilla/form';
import TransactionDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDtoModel.ts";
import TransactionDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDto.ts";

@customElement('tx-form')
export class TxForm extends View {

    @property()
    transaction = TransactionDtoModel.createEmptyValue();

    protected binder = new Binder(this, TransactionDtoModel, {
        onChange: this.handleChange,
        onSubmit: this.save
    });

    protected override render() {
        const {model} = this.binder;

        return html`
            <vaadin-date-time-picker
                    label="Zeitpunkt"
                    ${field(model.submittedAt)}
            ></vaadin-date-time-picker>
            <vaadin-text-field
                    label="Beschreibung"
                    ${field(model.description)}
            ></vaadin-text-field>
        `;
    }

    handleChange(value: any) {
        console.log("changed: " + JSON.stringify(value));
    }

    async save(tx: TransactionDto) {
        console.log("save: " + JSON.stringify(tx));
    }

    connectedCallback() {
        this.binder.read(this.transaction);
        super.connectedCallback();
    }
}