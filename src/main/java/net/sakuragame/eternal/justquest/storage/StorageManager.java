package net.sakuragame.eternal.justquest.storage;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.data.QuestState;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.eternal.justquest.util.Utils;
import net.sakuragame.serversystems.manage.api.database.DataManager;
import net.sakuragame.serversystems.manage.api.database.DatabaseQuery;
import net.sakuragame.serversystems.manage.api.util.TimeDataUtils;
import net.sakuragame.serversystems.manage.client.api.ClientManagerAPI;
import org.apache.commons.lang.time.DateUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StorageManager {

    private final DataManager dataManager;

    public StorageManager() {
        this.dataManager = ClientManagerAPI.getDataManager();

        for (QuestTables table : QuestTables.values()) {
            table.createTable();
        }

        java.sql.Date zero = Utils.getTodayZero();
        dataManager.executeSQL("UPDATE " + QuestTables.Quest_Account.getTableName() + " SET chain = 0 WHERE time < '" + zero + "';");
        dataManager.executeSQL("DELETE FROM " + QuestTables.Quest_Progress.getTableName() + " WHERE expire < '" + zero + "';");
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
            else {
                dataManager.executeInsert(
                        QuestTables.Quest_Account.getTableName(),
                        new String[]{"uid"},
                        new Object[]{uid}
                );
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
                String date = result.getString("expire");

                IMission mission = JustQuest.getProfileManager().getMission(missionID);
                if (mission == null) continue;

                IProgress progress = mission.newProgress(uuid, questID, progressData);

                progresses.put(questID, new QuestProgress(questID, missionID, progress, state, date == null ? -1 : TimeDataUtils.getTimeMillis(date)));
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

        dataManager.executeUpdate(
                QuestTables.Quest_Account.getTableName(),
                "trace", trace,
                "uid", uid
        );
    }

    public void updateChain(UUID uuid, int chain) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeUpdate(
                QuestTables.Quest_Account.getTableName(),
                "chain", chain,
                "uid", uid
        );
    }

    public void updateQuestProgress(UUID uuid, QuestProgress data) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeUpdate(
                QuestTables.Quest_Progress.getTableName(),
                new String[]{"mission", "data", "state"},
                new Object[]{data.getMissionID(), data.getProgress().getConvertData(), data.getState().getID()},
                new String[]{"uid", "quest"},
                new Object[]{uid, data.getQuestID()}
        );
    }

    public void insertQuestProgress(UUID uuid, QuestProgress data) {
        int uid = ClientManagerAPI.getUserID(uuid);
        if (uid == -1) return;

        dataManager.executeInsert(
                QuestTables.Quest_Progress.getTableName(),
                new String[]{"uid", "quest", "mission", "data", "state", "expire"},
                new Object[]{uid, data.getQuestID(), data.getMissionID(), data.getProgress().getConvertData(), data.getState().getID(), data.getExpire() == -1 ? null : new java.sql.Date(data.getExpire())}
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
