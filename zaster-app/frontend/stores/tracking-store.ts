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
        const trackingData = await TrackingEndpoint.loadTrackingData();
        this.bookings = trackingData.bookings.map(bookingDto =>
            new Booking(bookingDto, accountingStore.lookUpAccountCurrency));
    }
}