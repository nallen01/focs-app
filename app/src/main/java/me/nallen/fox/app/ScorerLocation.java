package me.nallen.fox.app;

public enum ScorerLocation {
    RED_ALL(8),
    BLUE_ALL(9),
    RED_BASES(0),
    BLUE_BASES(1),
    STATIONARY(2),
    COMMENTATOR_AUTOMATION(5),
    COMMENTATOR(4);

    private final int id;
    ScorerLocation(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ScorerLocation fromInt(int id) {
        ScorerLocation[] values = ScorerLocation.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }

    public String getName() {
        switch(this) {
            case RED_ALL: return "Red (Everything)";
            case BLUE_ALL: return "Blue (Everything)";
            case RED_BASES: return "Red Bases";
            case BLUE_BASES: return "Blue Bases";
            case STATIONARY: return "Stationary and Parking";
            case COMMENTATOR: return "Commentator";
            case COMMENTATOR_AUTOMATION: return "Commentator with Automation";
        }

        return "";
    }

    public static ScorerLocation[] getValues() {
        ScorerLocation[] values = {
            RED_ALL,
            BLUE_ALL,
            RED_BASES,
            BLUE_BASES,
            STATIONARY,
            COMMENTATOR
        };

        return values;
    }
}
