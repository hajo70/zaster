import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";
import {Booking} from "Frontend/model/tracking/Booking.ts";
import TransferDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDto.ts";

export class Transfer {
    readonly data: TransferDto;
    readonly booking: Booking;
    readonly account: AccountCurrency;

    constructor(tx: Booking, data: TransferDto, account: AccountCurrency) {
        this.booking = tx;
        this.data = data;
        this.account = account;
    }
}