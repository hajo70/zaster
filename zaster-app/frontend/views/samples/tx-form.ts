import {html} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';

import '@vaadin/button';
import '@vaadin/text-field';
import '@vaadin/date-time-picker';

import {Binder, field} from '@hilla/form';
import TransactionDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDtoModel.ts";
import TransactionDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDto.ts";
import {repeat} from "lit/directives/repeat.js";
import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";
import BookingDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDtoModel.ts";
import {BinderNode} from "@hilla/form/BinderNode.js";

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
            <div>
                <vaadin-date-time-picker
                        label="Zeitpunkt"
                        ${field(model.submittedAt)}
                ></vaadin-date-time-picker>
                <vaadin-text-field
                        label="Beschreibung"
                        style="--vaadin-field-default-width: 24em"
                        ${field(model.description)}
                ></vaadin-text-field>
            </div>
            <div>
                <span>${this.binder.value.bookings.length}:</span>
                ${repeat(this.binder.model.bookings, this.renderBooking)}
                <vaadin-button
                        @click=${() => {
                            this.binder.for(this.binder.model.bookings).appendItem();
                            this.requestUpdate();
                        }
                        }
                >
                    <vaadin-icon icon="lumo:plus"></vaadin-icon>
                </vaadin-button>
            </div>
        `;
    }

    private renderBooking(bookingBinder: BinderNode<BookingDto, BookingDtoModel>, index: number) {
        return html`
            <span>Booking: ${index}</span>
            <vaadin-date-time-picker
                    label="Buchungszeitpunkt"
                    ${field(bookingBinder.model.bookedAt)}
            ></vaadin-date-time-picker>
        `;
    }

    handleChange(value: any) {
        console.log("changed: " + JSON.stringify(value));
    }

    async save(tx: TransactionDto) {
        console.log("save: " + JSON.stringify(tx));
    }

    connectedCallback() {
        super.connectedCallback();
        console.log("binder.read...");
        this.binder.read(this.transaction);
    }
}