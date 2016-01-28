package me.nallen.fox.app;

public enum ScoreField {
    RED_HIGH_BALLS(0),
    RED_LOW_BALLS(1),
    RED_HIGH_BONUS_BALLS(2),
    RED_LOW_BONUS_BALLS(3),
    RED_ELEVATION(4),
    RED_AUTON(10),

    BLUE_HIGH_BALLS(5),
    BLUE_LOW_BALLS(6),
    BLUE_HIGH_BONUS_BALLS(7),
    BLUE_LOW_BONUS_BALLS(8),
    BLUE_ELEVATION(9),
    BLUE_AUTON(11),

    PAUSED(12),
    HISTORY(13),
    LARGE_HISTORY(14),

    CLEAR(15);

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
