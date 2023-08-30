import {makeObservable, observable} from "mobx";
import {TrackingEndpoint} from "Frontend/generated/endpoints.ts";
import {Booking} from "Frontend/model/tracking/Booking.ts";
import {accountingStore} from "Frontend/stores/app-store.ts";

export class TrackingStore {
    bookings: Booking[] = [];

    constructor() {
        makeObservable(this, {
            bookings: observable.shallow
        });
    }

    async loadBookings() {
        const bookingDtos = await TrackingEndpoint.getBookings();
        const transactions = await TrackingEndpoint.getTransactions();
        this.transactions = transactions.map(tx => new Booking(tx, accountingStore.getAccount));
    }
}