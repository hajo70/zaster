import {expect, test} from "vitest";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import {Account} from "Frontend/model/tracking/Account.ts";
import {currencyDto, id} from "Frontend/model/tracking/Account.test.ts";
import AccountCurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountCurrencyDto.ts";
import TransferDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/TransferDto.ts";
import TrackingDateTimeDto from "Frontend/generated/de/spricom/zaster/dtos/common/TrackingDateTimeDto.ts";
import BookingDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/BookingDto.ts";
import {Booking} from "Frontend/model/tracking/Booking.ts";

const someDate: TrackingDateTimeDto = {
    ts: '1111111',
    date: '2023-08-17'
};

const accountCurrency1Dto: AccountCurrencyDto = {
    id: id(20),
    currencyId: "1"
}

const account1Dto: AccountDto = {
    id: id(11),
    accountName: "account 1",
    currencies: [accountCurrency1Dto]
}

const accountCurrency2Dto: AccountCurrencyDto = {
    id: id(21),
    currencyId: "1"
}

const account2Dto: AccountDto = {
    id: id(12),
    accountName: "account 2",
    currencies: [accountCurrency2Dto]
}

const account0Dto: AccountDto = {
    id: id(10),
    accountName: "parent account",
    children: [account1Dto, account2Dto]
}

const account = new Account(null, account0Dto, (_) => currencyDto);

test('creates simple booking', () => {
    const transfer1Dto: TransferDto = {
        id: id(30),
        amount: -49.95,
        accountCurrencyId: "20",
        transferredAt: someDate
    }
    const transfer2Dto: TransferDto = {
        id: id(31),
        amount: 49.95,
        accountCurrencyId: "21",
        transferredAt: someDate
    }
    const bookingDto: BookingDto = {
        id: id(40),
        bookedAt: someDate,
        description: "Transfer of money from account 1 to account 2",
        transfers: [transfer1Dto, transfer2Dto]
    }
    function lookUpAccountCurrency(id: string) {
        const ac = account.lookUpAccountCurrency(id);
        if (ac) {
            return ac;
        }
        throw new Error("There is no " + id + " account currency.")
    }

    const booking = new Booking(bookingDto,  lookUpAccountCurrency);
    expect(booking.data.id.uuid).toBe("40");
    expect(booking.transfers).toHaveLength(2);
    expect(booking.transfers[0].data.amount + booking.transfers[1].data.amount).toBe(0);
    expect(booking.transfers[0].accountCurrency.account.data.id.uuid).toBe("11");
    expect(booking.transfers[1].accountCurrency.currency.currencyCode).toBe("EUR");
})
