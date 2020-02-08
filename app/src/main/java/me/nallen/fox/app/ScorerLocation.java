package me.nallen.fox.app;

public enum ScorerLocation {
    TOWERS(8),
    CUBES(9),
    CUBES_RED(10),
    CUBES_BLUE(11),
    COMMENTATOR_AUTOMATION(5),
    COMMENTATOR(4);

    private final int id;
    ScorerLocation(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ScorerLocation fromInt(int id) {
        ScorerLocation[] values = ScorerLocation.values();
        for (ScorerLocation value : values) {
            if (value.getValue() == id)
                return value;
        }
        return null;
    }

    public String getName() {
        switch(this) {
            case TOWERS: return "Towers";
            case CUBES: return "Cubes";
            case CUBES_RED: return "Cubes (Red)";
            case CUBES_BLUE: return "Cubes (Blue)";
            case COMMENTATOR: return "Commentator";
            case COMMENTATOR_AUTOMATION: return "Commentator with Automation";
        }

        return "";
    }

    public static ScorerLocation[] getValues() {
        ScorerLocation[] values = {
            TOWERS,
            CUBES,
            CUBES_RED,
            CUBES_BLUE,
            COMMENTATOR
        };

        return values;
    }
}
