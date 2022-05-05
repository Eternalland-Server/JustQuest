package net.sakuragame.eternal.justquest.storage;

import com.alibaba.fastjson.JSON;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.data.QuestState;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.quest.QuestReward;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.serversystems.manage.api.database.DataManager;
import net.sakuragame.serversystems.manage.api.database.DatabaseQuery;
import net.sakuragame.serversystems.manage.api.util.TimeDataUtils;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.apache.commons.lang.time.DateUtils;

import java.sql.ResultSet;
import java.util.*;

public class StorageManager {

    private final DataManager dataManager;

    public StorageManager() {
        this.dataManager = ClientManagerAPI.getDataManager();

        for (QuestTables table : QuestTables.values()) {
            table.createTable();
        }
    }

    public QuestAccount getAccount(UUID uuid) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return null;

        QuestAccount account = new QuestAccount(uuid);

        try (DatabaseQuery query = dataManager.createQuery(
                QuestTables.Quest_Account.getTableName(),
                "uid", uid
        )) {
            ResultSet result = query.getResultSet();
            if (result.next()) {
                String trace = result.getString("trace");
                int chain = result.getInt("chain");
                Date date = result.getDate("time");

                if (trace != null) account.setTrace(trace);
                if (DateUtils.isSameDay(date, new Date())) {
                    account.setChain(chain);
                }
                else {
                    account.setChain(0);
                    dataManager.executeUpdate(QuestTables.Quest_Account.getTableName(), "chain", 0, "uid", uid);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try (DatabaseQuery query = dataManager.createQuery(
                QuestTables.Quest_Finished.getTableName(),
                "uid", uid
        )) {
            ResultSet result = query.getResultSet();
            List<String> finished = new ArrayList<>();
            while (result.next()) {
                finished.add(result.getString("quest"));
            }

            account.setFinished(finished);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try (DatabaseQuery query = dataManager.createQuery(
                QuestTables.Quest_Progress.getTableName(),
                "uid", uid
        )) {
            ResultSet result = query.getResultSet();
            Map<String, QuestProgress> progresses = new HashMap<>();
            while (result.next()) {
                String questID = result.getString("quest");
                String missionID = result.getString("mission");
                String progressData = result.getString("data");
                QuestState state = QuestState.match(result.getInt("state"));

                IMission mission = JustQuest.getProfileManager().getMission(missionID);
                if (mission == null) continue;

                IProgress progress = mission.newProgress(uuid, questID, progressData);

                progresses.put(questID, new QuestProgress(questID, missionID, progress, state));
            }

            account.setProgresses(progresses);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return account;
    }

    public void updateTrace(UUID uuid, String trace) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeReplace(
                QuestTables.Quest_Account.getTableName(),
                new String[]{"uid", "trace"},
                new Object[]{uid, trace}
        );
    }

    public void updateQuestProgress(UUID uuid, QuestProgress data) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeReplace(
                QuestTables.Quest_Progress.getTableName(),
                new String[]{"uid", "quest", "mission", "data", "state"},
                new Object[]{uid, data.getQuestID(), data.getMissionID(), data.getProgress().getConvertData(), data.getState().getID()}
        );
    }

    public void insertQuestProgress(UUID uuid, QuestProgress data) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeInsert(
                QuestTables.Quest_Progress.getTableName(),
                new String[]{"uid", "quest", "mission", "data", "state"},
                new Object[]{uid, data.getQuestID(), data.getMissionID(), data.getProgress().getConvertData(), data.getState().getID()}
        );
    }

    public void deleteQuestProgress(UUID uuid, String questID) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeDelete(
                QuestTables.Quest_Progress.getTableName(),
                new String[]{"uid", "quest"},
                new Object[]{uid, questID}
        );
    }

    public void insertFinished(UUID uuid, String questID) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeReplace(
                QuestTables.Quest_Finished.getTableName(),
                new String[]{"uid", "quest"},
                new Object[]{uid, questID}
        );
    }

    public void purgeUserData(UUID uuid) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeDelete(
                QuestTables.Quest_Account.getTableName(),
                "uid", uid
        );
        dataManager.executeDelete(
                QuestTables.Quest_Finished.getTableName(),
                "uid", uid
        );
        dataManager.executeDelete(
                QuestTables.Quest_Progress.getTableName(),
                "uid", uid
        );
    }
}
