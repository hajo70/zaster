import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";
import {Transfer} from "Frontend/model/tracking/Transfer.ts";
import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";

export class Booking {
    data: BookingDto;
    transfers: Transfer[];

    constructor(data: BookingDto, accountLookup: (id: string) => AccountCurrency) {
        this.data = data;
        this.transfers = data.transfers.map(booking =>
            new Transfer(this, booking, accountLookup(booking.id.uuid)));
    }
}