import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";
import {Booking} from "Frontend/model/tracking/Booking.ts";
import TransferDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDto.ts";

export class Transfer {
    readonly data: TransferDto;
    readonly booking: Booking;
    readonly accountCurrency: AccountCurrency;

    constructor(tx: Booking, data: TransferDto, account: AccountCurrency) {
        this.booking = tx;
        this.data = data;
        this.accountCurrency = account;
    }

    get description() {
        return this.booking.data.description;
    }

    get bookingDate() {
        return this.booking.data.bookedAt.date;
    }

    get amount() {
        return this.data.amount;
    }
}