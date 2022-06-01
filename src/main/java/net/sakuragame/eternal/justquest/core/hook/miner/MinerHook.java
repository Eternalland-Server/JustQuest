package net.sakuragame.eternal.justquest.core.hook.miner;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.hook.PluginHook;

public class MinerHook implements PluginHook {
    @Override
    public String getPlugin() {
        return "KirraMiner";
    }

    @Override
    public void register() {
        JustQuest.getProfileManager().registerEventPreset("explore_ore", ExploreOreEvent.class);
    }
}
