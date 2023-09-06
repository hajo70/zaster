import {html} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {repeat} from "lit/directives/repeat.js";
import {Binder, field} from '@hilla/form';
import {BinderNode} from "@hilla/form/BinderNode.js";

import '@vaadin/button';
import '@vaadin/text-field';
import '@vaadin/date-picker';

import {View} from 'Frontend/views/view';
import BookingDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDtoModel.ts";
import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";
import TransferDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDto.ts";
import TransferDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDtoModel.ts";

@customElement('tx-form')
export class TxForm extends View {

    @property()
    transaction = BookingDtoModel.createEmptyValue();

    protected binder = new Binder(this, BookingDtoModel, {
        onChange: this.handleChange,
        onSubmit: this.save
    });

    protected override render() {
        const {model} = this.binder;

        return html`
            <div>
                <vaadin-date-picker
                        label="Datum"
                        ${field(model.bookedAt.date)}
                ></vaadin-date-picker>
                <vaadin-text-field
                        label="Beschreibung"
                        style="--vaadin-field-default-width: 24em"
                        ${field(model.description)}
                ></vaadin-text-field>
            </div>
            <div>
                <span>${this.binder.value.transfers.length}:</span>
                ${repeat(this.binder.model.transfers, this.renderBooking)}
                <vaadin-button
                        @click=${() => {
                            this.binder.for(this.binder.model.transfers).appendItem();
                            this.requestUpdate();
                        }
                        }
                >
                    <vaadin-icon icon="lumo:plus"></vaadin-icon>
                </vaadin-button>
            </div>
        `;
    }

    private renderBooking(bookingBinder: BinderNode<TransferDto, TransferDtoModel>, index: number) {
        return html`
            <span>Booking: ${index}</span>
            <vaadin-date-picker
                    label="Buchungsdatum"
                    ${field(bookingBinder.model.transferredAt.date)}
            ></vaadin-date-picker>
        `;
    }

    handleChange(value: any) {
        console.log("changed: " + JSON.stringify(value));
    }

    async save(tx: BookingDto) {
        console.log("save: " + JSON.stringify(tx));
    }

    connectedCallback() {
        super.connectedCallback();
        console.log("binder.read...");
        this.binder.read(this.transaction);
    }
}