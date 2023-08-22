import {AccountCurrency} from "Frontend/model/tracking/AccountCurrency.ts";
import {Transfer} from "Frontend/model/tracking/Transfer.ts";
import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";

export class Booking {
    data: BookingDto;
    transfers: Transfer[];

    constructor(data: BookingDto, accountCurrencyLookup: (id: string) => AccountCurrency) {
        this.data = data;
        this.transfers = data.transfers.map(transfer =>
            new Transfer(this, transfer, accountCurrencyLookup(transfer.accountCurrencyId)));
    }
}