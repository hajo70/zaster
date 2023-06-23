import {RouterLocation} from '@vaadin/router';
import {makeAutoObservable} from 'mobx';
import ApplicationUserEntity from "Frontend/generated/de/spricom/zaster/entities/managment/ApplicationUserEntity";
import {ApplicationUserEndpoint} from "Frontend/generated/endpoints";
import UserRole from "Frontend/generated/de/spricom/zaster/entities/managment/UserRole";
import {TrackingStore} from "Frontend/stores/tracking-store.ts";

export class AppStore {
  trackingStore = new TrackingStore();

  applicationName = 'Zaster';

  // The location, relative to the base path, e.g. "hello" when viewing "/hello"
  location = '';

  currentViewTitle = '';

  user: ApplicationUserEntity | undefined = undefined;

  constructor() {
    makeAutoObservable(this);
  }

  setLocation(location: RouterLocation) {
    const serverSideRoute = location.route?.path == '(.*)';
    if (location.route && !serverSideRoute) {
      this.location = location.route.path;
    } else if (location.pathname.startsWith(location.baseUrl)) {
      this.location = location.pathname.substr(location.baseUrl.length);
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
    this.user = await ApplicationUserEndpoint.getAuthenticatedUser();
  }

  clearUserInfo() {
    this.user = undefined;
  }

  get loggedIn() {
    return !!this.user;
  }

  isUserInRole(role: UserRole) {
    return this.user?.userRoles?.includes(role);
  }
}

export const appStore = new AppStore();
export const trackingStore = appStore.trackingStore;
