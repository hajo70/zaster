import type {Route} from '@vaadin/router';
import {appStore} from './stores/app-store.js';
import './views/about/about-view';
import './views/main-layout';
import Role from "Frontend/generated/de/spicom/zaster/data/Role";

export type ViewRoute = Route & {
  title?: string;
  icon?: string;
  requiresLogin?: boolean;
  rolesAllowed?: Role[];
  children?: ViewRoute[];
};

export const hasAccess = (route: Route) => {
  const viewRoute = route as ViewRoute;
  if (viewRoute.requiresLogin && !appStore.loggedIn) {
    return false;
  }

  if (viewRoute.rolesAllowed) {
    return viewRoute.rolesAllowed.some((role) => appStore.isUserInRole(role));
  }
  return true;
};

export const views: ViewRoute[] = [
  // Place routes below (more info https://hilla.dev/docs/routing)
  {
    path: '',
    component: 'about-view',
    requiresLogin: false,
    icon: '',
    title: '',
  },
  {
    path: 'about',
    component: 'about-view',
    requiresLogin: true,
    icon: 'file',
    title: 'About',
  },
  {
    path: 'currencies',
    component: 'currencies-view',
    requiresLogin: false,
    icon: 'file',
    title: 'Währungen',
    action: async (_context, _command) => {
      await import('./views/currencies/currencies-view');
      return;
    },
  },
  {
    path: 'hello',
    component: 'hello-world-view',
    requiresLogin: true,
    icon: 'globe-solid',
    title: 'Hello World',
    action: async (_context, _command) => {
      if (!hasAccess(_context.route)) {
        return _command.redirect('login');
      }
      await import('./views/helloworld/hello-world-view');
      return;
    },
  },
];
export const routes: ViewRoute[] = [
  {
    path: 'login',
    component: 'login-view',
    requiresLogin: true,
    icon: '',
    title: 'Login',
    action: async (_context, _command) => {
      await import('./views/login/login-view.js');
      return;
    },
  },

  {
    path: '',
    component: 'main-layout',
    children: views,
  },
];
