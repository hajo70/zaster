import {View} from "Frontend/views/view.ts";
import {customElement, state} from "lit/decorators.js";
import {html} from "lit";

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/text-field';
import '@vaadin/icon';
import './tx-form';
import TransactionDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDto.ts";
import TransactionDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDtoModel.ts";
import BookingDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDtoModel.ts";

@customElement("dynamic-tx-form-view")
export class DynamicTxFromView extends View {

    @state()
    private transactions: TransactionDto[] = [];

    @state()
    private deleted: TransactionDto[] = [];

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

    private removeTx(tx: TransactionDto) {
        this.deleted = [...this.deleted, tx];
    }

    private addTx() {
        this.transactions = [...this.transactions, this.createTx()];
    }

   private createTx() {
       const newTx = TransactionDtoModel.createEmptyValue();
       newTx.submittedAtDate = new Date().toISOString().substring(0, 10);
       newTx.bookings = [
           this.createBooking(),
           this.createBooking()
       ];
       return newTx;
   }

   private nextId = 1;

   private createBooking() {
        const newBooking = BookingDtoModel.createEmptyValue();
        this.nextId++;
        newBooking.id.id = this.nextId as string;
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