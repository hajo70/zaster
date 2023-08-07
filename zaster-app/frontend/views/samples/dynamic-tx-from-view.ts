import {View} from "Frontend/views/view.ts";
import {customElement, state} from "lit/decorators.js";
import {html} from "lit";

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/text-field';
import '@vaadin/icon';
import './tx-form';
import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";
import BookingDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDtoModel.ts";
import TransferDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDtoModel.ts";

@customElement("dynamic-tx-form-view")
export class DynamicTxFromView extends View {

    @state()
    private transactions: BookingDto[] = [];

    @state()
    private deleted: BookingDto[] = [];

    protected override render() {
        return html`
            <div>
                <h2>Transaktionen</h2>
                ${this.transactions.map(tx =>
                        html`
                            <div ?hidden=${this.deleted.some(deleted => deleted == tx)}>
                                <tx-form .transaction=${tx}></tx-form>
                                <vaadin-icon icon="lumo:cross"
                                             @click=${() => this.removeTx(tx)}
                                ></vaadin-icon>
                            </div>
                        `)}

                <div class="flex gap-s">
                    <vaadin-button @click=${this.addTx}>
                        Add
                    </vaadin-button>
                </div>
            </div>
        `;
    }

    private removeTx(tx: BookingDto) {
        this.deleted = [...this.deleted, tx];
    }

    private addTx() {
        this.transactions = [...this.transactions, this.createTx()];
    }

   private createTx() {
       const newBooking = BookingDtoModel.createEmptyValue();
       newBooking.bookedAtDate = new Date().toISOString().substring(0, 10);
       newBooking.transfers = [
           this.createBooking(),
           this.createBooking()
       ];
       return newBooking;
   }

   private nextId = 1;

   private createBooking() {
        const newBooking = TransferDtoModel.createEmptyValue();
        this.nextId++;
        newBooking.id.uuid = `${this.nextId}`;
        return newBooking;
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
        this.addTx();
    }
}