import {html} from "lit";
import {customElement, state} from "lit/decorators.js";
import {repeat} from "lit/directives/repeat.js";
import {Binder, field} from "@hilla/form";
import {BinderNode} from "@hilla/form/BinderNode.js";

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/date-picker';
import '@vaadin/text-field';
import '@vaadin/icon';
import './tx-form';

import {View} from "Frontend/views/view.ts";
import BookingDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDtoModel.ts";
import TransferDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDtoModel.ts";
import TransferDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDto.ts";

@customElement("simple-tx-form-view")
export class SimpleTxFromView extends View {

    @state()
    private booking = BookingDtoModel.createEmptyValue();

    private binder = new Binder(this, BookingDtoModel);

    protected override render() {
        const {model} = this.binder;

        return html`
            <div>
                <vaadin-date-picker
                        label="Datum"
                        ${field(model.bookedAtDate)}
                ></vaadin-date-picker>
                <vaadin-text-field
                        label="Beschreibung"
                        style="--vaadin-field-default-width: 24em"
                        ${field(model.description)}
                ></vaadin-text-field>
            </div>
            <div>
                <span>${this.binder.value.transfers.length}:</span>
                ${repeat(this.binder.model.transfers, this.renderTransfer)}
                <vaadin-button
                        @click=${() => this.binder.for(this.binder.model.transfers)
            .appendItem(TransferDtoModel.createEmptyValue())}
                >
                    <vaadin-icon icon="lumo:plus"></vaadin-icon>
                </vaadin-button>
            </div>            
        `;
    }

    private renderTransfer(transferBinder: BinderNode<TransferDto, TransferDtoModel>, index: number) {
        return html`
            <span>Transfer: ${index}</span>
            <vaadin-date-picker
                    label="Wertstellung"
                    ${field(transferBinder.model.transferredAtDate)}
            ></vaadin-date-picker>
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
        this.binder.read(this.booking);
    }
}