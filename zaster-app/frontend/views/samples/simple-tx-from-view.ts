import {View} from "Frontend/views/view.ts";
import {customElement, state} from "lit/decorators.js";
import {html} from "lit";

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/text-field';
import '@vaadin/icon';
import './tx-form';
import TransactionDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDtoModel.ts";
import BookingDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDtoModel.ts";
import {Binder, field} from "@hilla/form";
import {repeat} from 'lit/directives/repeat.js';
import {BinderNode} from "@hilla/form/BinderNode.js";
import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";

@customElement("simple-tx-form-view")
export class SimpleTxFromView extends View {

    @state()
    private transaction = TransactionDtoModel.createEmptyValue();

    private binder = new Binder(this, TransactionDtoModel);

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
                        @click=${() => this.binder.for(this.binder.model.bookings)
            .appendItem(BookingDtoModel.createEmptyValue())}
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

    connectedCallback() {
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
        this.binder.read(this.transaction);
    }
}