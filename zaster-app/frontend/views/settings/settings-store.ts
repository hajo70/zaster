import {makeAutoObservable} from "mobx";
import {appStore} from "Frontend/stores/app-store.ts";
import TenantEntity from "Frontend/generated/de/spricom/zaster/entities/management/TenantEntity.ts";
import {SettingsEndpoint} from "Frontend/generated/endpoints.ts";

class SettingsStore {
    constructor() {
        makeAutoObservable(
            this,
            {
            },
            {autoBind: true}
        );
    }

    get tenant() {
        return appStore.user?.tenant;
    }

    async saveTenant(tenant: TenantEntity) {
        try {
            const saved = await SettingsEndpoint.saveTenant(tenant);
            if (saved && appStore.user) {
                appStore.user.tenant = saved;
            } else {
                console.log('Tenant save failed');
            }
        } catch (ex) {
            console.log('Tenant save failed: ' + ex);
        }
    }
}

export const settingsStore = new SettingsStore();