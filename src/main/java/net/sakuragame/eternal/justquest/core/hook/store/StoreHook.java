package net.sakuragame.eternal.justquest.core.hook.store;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.hook.PluginHook;

public class StoreHook implements PluginHook {
    @Override
    public String getPlugin() {
        return "JustStore";
    }

    @Override
    public void register() {
        JustQuest.getProfileManager().registerEventPreset("merchant", MerchantEvent.class);
    }
}
