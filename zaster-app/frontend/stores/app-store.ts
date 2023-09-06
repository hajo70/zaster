import {RouterLocation} from '@vaadin/router';
import {action, makeObservable, observable} from 'mobx';
import {AppEndpoint} from "Frontend/generated/endpoints";
import {TrackingStore} from "Frontend/stores/tracking-store.ts";
import {AccountingStore} from "Frontend/stores/accounting-store.ts";
import UserInfoDto from "Frontend/generated/de/spricom/zaster/dtos/app/UserInfoDto.ts";
import UserRole from "Frontend/generated/de/spricom/zaster/enums/settings/UserRole.ts";

export class AppStore {
  accountingStore = new AccountingStore();
  trackingStore = new TrackingStore();

  applicationName = 'Zaster';

  // The location, relative to the base path, e.g. "hello" when viewing "/hello"
  location = '';

  currentViewTitle = '';

  userInfo: UserInfoDto | undefined = undefined;

  constructor() {
    makeObservable(this, {
      location: observable,
      currentViewTitle: observable,
      userInfo: observable.ref,
      setLocation: action
    });
  }

  setLocation(location: RouterLocation) {
    const serverSideRoute = location.route?.path == '(.*)';
    if (location.route && !serverSideRoute) {
      this.location = location.route.path;
    } else if (location.pathname.startsWith(location.baseUrl)) {
      this.location = location.pathname.substring(location.baseUrl.length);
    } else {
      this.location = location.pathname;
    }
    if (serverSideRoute) {
      this.currentViewTitle = document.title; // Title set by server
    } else {
      this.currentViewTitle = (location?.route as any)?.title || '';
    }
  }

  async fetchUserInfo() {
    const userInfo = await AppEndpoint.getUserInfo();
    this.updateUserInfoLocally(userInfo);
  }

  private updateUserInfoLocally = (userInfo: UserInfoDto|undefined) => {
    this.userInfo = userInfo;
    if (userInfo) {
      accountingStore.currencies = userInfo.currencies;
      accountingStore.rootAccounts = userInfo.rootAccounts;
      trackingStore.loadBookings();
    }
  }

  clearUserInfo() {
    this.userInfo = undefined;
  }

  get loggedIn() {
    return !!this.userInfo;
  }

  isUserInRole(role: UserRole) {
    return this.userInfo?.user.roles.includes(role);
  }
}

export const appStore = new AppStore();
export const trackingStore = appStore.trackingStore;
export const accountingStore = appStore.accountingStore;
