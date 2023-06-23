import TransactionDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransactionDto.ts";
import {Account} from "Frontend/model/tracking/Account.ts";
import {Booking} from "Frontend/model/tracking/Booking.ts";

export class Transaction {
    data: TransactionDto;
    bookings: Booking[];

    constructor(data: TransactionDto, accountLookup: (id: string) => Account) {
        this.data = data;
        this.bookings = data.bookings.map(booking => new Booking(this, booking, accountLookup(booking.id.id)));
    }
}