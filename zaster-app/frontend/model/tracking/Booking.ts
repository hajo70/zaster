import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";
import {Account} from "Frontend/model/tracking/Account.ts";
import {Transaction} from "Frontend/model/tracking/Transaction.ts";

export class Booking {
    data: BookingDto;
    tx: Transaction;
    account: Account;

    constructor(tx: Transaction, data: BookingDto, account: Account) {
        this.tx = tx;
        this.data = data;
        this.account = account;
    }
}