package net.sakuragame.eternal.justquest.ui;

import lombok.Getter;

@Getter
public enum OperateCode {

    Conv_Option(100),
    Choose_Quest(200),
    Cancel_Quest(201),
    Receive_Reward(202),
    Up_Page(300),
    Next_Page(301);

    private final int ID;

    OperateCode(int ID) {
        this.ID = ID;
    }

    public static OperateCode match(int ID) {
        for (OperateCode code : values()) {
            if (code.getID() == ID) return code;
        }

        return null;
    }
}
