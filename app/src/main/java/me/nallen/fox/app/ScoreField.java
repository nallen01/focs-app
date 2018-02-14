package me.nallen.fox.app;

public enum ScoreField {
    RED_BASE_ONE_CONES(0),
    RED_BASE_TWO_CONES(1),
    RED_BASE_THREE_CONES(2),
    RED_BASE_FOUR_CONES(3),
    RED_BASE_ONE_ZONE(4),
    RED_BASE_TWO_ZONE(5),
    RED_BASE_THREE_ZONE(6),
    RED_BASE_FOUR_ZONE(7),
    RED_STATIONARY_CONES(8),
    RED_PARKING(9),
    RED_AUTON(10),

    BLUE_BASE_ONE_CONES(11),
    BLUE_BASE_TWO_CONES(12),
    BLUE_BASE_THREE_CONES(13),
    BLUE_BASE_FOUR_CONES(14),
    BLUE_BASE_ONE_ZONE(15),
    BLUE_BASE_TWO_ZONE(16),
    BLUE_BASE_THREE_ZONE(17),
    BLUE_BASE_FOUR_ZONE(18),
    BLUE_STATIONARY_CONES(19),
    BLUE_PARKING(20),
    BLUE_AUTON(21),

    PAUSED(22),
    HISTORY(23),
    LARGE_HISTORY(24),
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
