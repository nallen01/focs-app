package me.nallen.fox.app;

public enum ScorerLocation {
    FLAGS(8),
    CAPS(9),
    PARKING(0),
    CAPS_PARKING(1),
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
            case FLAGS: return "Flags";
            case CAPS: return "Caps";
            case PARKING: return "Parking";
            case CAPS_PARKING: return "Caps and Parking";
            case COMMENTATOR: return "Commentator";
            case COMMENTATOR_AUTOMATION: return "Commentator with Automation";
        }

        return "";
    }

    public static ScorerLocation[] getValues() {
        ScorerLocation[] values = {
            FLAGS,
            CAPS_PARKING,
            COMMENTATOR
        };

        return values;
    }
}
