import {makeAutoObservable} from "mobx";
import {appStore} from "Frontend/stores/app-store.ts";
import {SettingsEndpoint} from "Frontend/generated/endpoints.ts";
import TenantDto from "Frontend/generated/de/spricom/zaster/dtos/settings/TenantDto.ts";

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
        return appStore.userInfo?.tenant;
    }

    async saveTenant(tenant: TenantDto) {
        try {
            const saved = await SettingsEndpoint.saveTenant(tenant);
            this.updateTenantLocally(saved);
        } catch (ex) {
            console.log('Tenant save failed: ' + ex);
        }
    }

    updateTenantLocally(saved: TenantDto) {
        if (saved && appStore.userInfo) {
            appStore.userInfo.tenant = saved;
        } else {
            console.log('Tenant save failed');
        }
    }
}

export const settingsStore = new SettingsStore();