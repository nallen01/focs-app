package me.nallen.fox.app;

public enum ScoreField {
    RED_FAR_STARS(0),
    RED_FAR_CUBES(1),
    RED_NEAR_STARS(2),
    RED_NEAR_CUBES(3),
    RED_ELEVATION(4),
    RED_AUTON(10),

    BLUE_FAR_STARS(5),
    BLUE_FAR_CUBES(6),
    BLUE_NEAR_STARS(7),
    BLUE_NEAR_CUBES(8),
    BLUE_ELEVATION(9),
    BLUE_AUTON(11),

    PAUSED(12),
    HISTORY(13),
    LARGE_HISTORY(14),
    HIDE(16),

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
