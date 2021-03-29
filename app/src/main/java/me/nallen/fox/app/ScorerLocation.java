package me.nallen.fox.app;

public enum ScorerLocation {
    GOALS(8),
    BALLS(9),
    BALLS_RED(10),
    BALLS_BLUE(11),
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
            case GOALS: return "Goals";
            case BALLS: return "Balls";
            case BALLS_RED: return "Balls (Red)";
            case BALLS_BLUE: return "Balls (Blue)";
            case COMMENTATOR: return "Commentator";
            case COMMENTATOR_AUTOMATION: return "Commentator with Automation";
        }

        return "";
    }

    public static ScorerLocation[] getValues() {

        return new ScorerLocation[]{
            GOALS,
            BALLS,
            BALLS_RED,
            BALLS_BLUE,
            COMMENTATOR
        };
    }
}
