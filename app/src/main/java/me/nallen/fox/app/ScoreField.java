package me.nallen.fox.app;

public enum ScoreField {
    GOAL_OWNERSHIP_0_0(0),
    GOAL_OWNERSHIP_0_1(1),
    GOAL_OWNERSHIP_0_2(2),
    GOAL_OWNERSHIP_1_0(3),
    GOAL_OWNERSHIP_1_1(4),
    GOAL_OWNERSHIP_1_2(5),
    GOAL_OWNERSHIP_2_0(6),
    GOAL_OWNERSHIP_2_1(7),
    GOAL_OWNERSHIP_2_2(8),

    AUTON(9),

    RED_BALLS(10),

    BLUE_BALLS(11),

    PAUSED(22),
    HISTORY_METHOD(23),
    HIDE(25),

    THREE_TEAM(26),

    CLEAR(27);

    private final int id;
    ScoreField(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ScoreField fromInt(int id) {
        ScoreField[] values = ScoreField.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }
}