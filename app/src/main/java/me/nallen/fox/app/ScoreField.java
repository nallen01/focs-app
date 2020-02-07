package me.nallen.fox.app;

public enum ScoreField {
    HIGH_FLAG_1_1(0),
    HIGH_FLAG_1_2(1),
    HIGH_FLAG_1_3(2),
    HIGH_FLAG_2_1(3),
    HIGH_FLAG_2_2(4),
    HIGH_FLAG_2_3(5),
    LOW_FLAG_1(6),
    LOW_FLAG_2(7),
    LOW_FLAG_3(8),

    RED_HIGH_CAPS(9),
    RED_LOW_CAPS(10),
    RED_PARKING_1(11),
    RED_PARKING_2(12),
    RED_AUTON(13),

    BLUE_HIGH_CAPS(14),
    BLUE_LOW_CAPS(15),
    BLUE_PARKING_1(16),
    BLUE_PARKING_2(17),
    BLUE_AUTON(18),

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
        for (ScoreField value : values) {
            if (value.getValue() == id)
                return value;
        }
        return null;
    }
}