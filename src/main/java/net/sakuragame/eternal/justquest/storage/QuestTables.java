package net.sakuragame.eternal.justquest.storage;

import net.sakuragame.eternal.dragoncore.database.mysql.DatabaseTable;

public enum QuestTables {

    Quest_Account(new DatabaseTable("quest_account",
            new String[]{
                    "`uid` int not null PRIMARY KEY",
                    "`trace` varchar(32)",
                    "`chain` int default 0",
                    "`time` timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
            }
    )),

    Quest_Finished(new DatabaseTable("quest_finished",
            new String[]{
                    "`uid` int not null",
                    "`quest` varchar(32) not null",
                    "`time` timestamp default CURRENT_TIMESTAMP"
            }
    )),

    Quest_Progress(new DatabaseTable("quest_progress",
            new String[]{
                    "`uid` int not null",
                    "`quest` varchar(32) not null",
                    "`mission` varchar(32) not null",
                    "`data` text not null",
                    "`state` int not null",
                    "`expire` DATETIME",
                    "UNIQUE KEY `progress`(`uid`,`quest`)"
            }
    ));

    private final DatabaseTable table;

    QuestTables(DatabaseTable table) {
        this.table = table;
    }

    public String getTableName() {
        return table.getTableName();
    }

    public String[] getColumns() {
        return table.getTableColumns();
    }

    public DatabaseTable getTable() {
        return table;
    }

    public void createTable() {
        table.createTable();
    }
}
