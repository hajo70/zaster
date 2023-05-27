import {RouterLocation} from '@vaadin/router';
import {makeAutoObservable} from 'mobx';
import User from "Frontend/generated/de/spicom/zaster/data/entity/User";
import {UserEndpoint} from "Frontend/generated/endpoints";
import Role from "Frontend/generated/de/spicom/zaster/data/Role";

export class AppStore {
  applicationName = 'Zaster';

  // The location, relative to the base path, e.g. "hello" when viewing "/hello"
  location = '';

  currentViewTitle = '';

  user: User | undefined = undefined;

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
    this.user = await UserEndpoint.getAuthenticatedUser();
  }

  clearUserInfo() {
    this.user = undefined;
  }

  get loggedIn() {
    return !!this.user;
  }

  isUserInRole(role: Role) {
    return this.user?.roles?.includes(role);
  }
}

export const appStore = new AppStore();
