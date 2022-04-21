package net.sakuragame.eternal.justquest.core.mission.hook.party;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.mission.hook.PluginHook;

public class PartyHook implements PluginHook {

    @Override
    public void register() {
        JustQuest.getProfileManager().registerMissionPreset("create_team", CreateTeamMission.class);
    }

}
