import type {Route} from '@vaadin/router';
import {appStore} from './stores/app-store.js';
import './views/about/about-view';
import './views/main-layout';
import './views/samples/dynamic-grid-view';
import './views/samples/simple-tx-from-view';
import './views/samples/dynamic-tx-from-view';
import UserRole from "Frontend/generated/de/spricom/zaster/enums/settings/UserRole.ts";

export type ViewRoute = Route & {
  title?: string;
  icon?: string;
  requiresLogin?: boolean;
  rolesAllowed?: UserRole[];
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
    requiresLogin: false,
    icon: 'file',
    title: 'About',
  },
  {
    path: 'settings',
    component: 'settings-view',
    rolesAllowed: [UserRole.ADMIN],
    icon: 'bath-solid',
    title: 'Settings',
    action: async (_context, _command) => {
      await import('Frontend/views/settings/settings-view.ts');
      return;
    },
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
    path: 'bookings',
    component: 'bookings-view',
    requiresLogin: true,
    icon: 'file',
    title: 'Bookings',
    action: async (_context, _command) => {
      await import('./views/bookings/bookings-view');
      return;
    },
  },
  {
    path: 'importers',
    component: 'import-view',
    requiresLogin: true,
    icon: 'file',
    title: 'Import',
    action: async (_context, _command) => {
      await import('./views/importers/import-view');
      return;
    },
  },
  {
    path: 'samples/dynamic-grid-view',
    component: 'dynamic-grid-view',
    requiresLogin: true,
    icon: 'file',
    title: 'Sample: Dynamic Grid',
  },
  {
    path: 'samples/simple-tx-form-view',
    component: 'simple-tx-form-view',
    requiresLogin: true,
    icon: 'file',
    title: 'Sample: Simple transactions form',
  },
  {
    path: 'samples/dynamic-tx-form-view',
    component: 'dynamic-tx-form-view',
    requiresLogin: true,
    icon: 'file',
    title: 'Sample: Dynamic transactions form',
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
