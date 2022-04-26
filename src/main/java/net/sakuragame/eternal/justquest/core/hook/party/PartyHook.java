package net.sakuragame.eternal.justquest.core.hook.party;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.hook.PluginHook;

public class PartyHook implements PluginHook {

    @Override
    public String getPlugin() {
        return "KirraPartyBukkit";
    }

    @Override
    public void register() {
        JustQuest.getProfileManager().registerMissionPreset("create_team", CreateTeamMission.class);
    }

}
