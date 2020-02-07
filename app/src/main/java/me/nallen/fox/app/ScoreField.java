package me.nallen.fox.app;

public enum ScoreField {
    TOWER_CUBE_1(0),
    TOWER_CUBE_2(1),
    TOWER_CUBE_3(2),
    TOWER_CUBE_4(3),
    TOWER_CUBE_5(4),
    TOWER_CUBE_6(5),
    TOWER_CUBE_7(6),

    AUTON(7),

    RED_ORANGE_CUBES(8),
    RED_GREEN_CUBES(9),
    RED_PURPLE_CUBES(10),

    BLUE_ORANGE_CUBES(11),
    BLUE_GREEN_CUBES(12),
    BLUE_PURPLE_CUBES(13),

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