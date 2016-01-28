package me.nallen.fox.app;

public enum ScorerLocation {
    RED_GOAL(0),
    BLUE_GOAL(1),
    COMMENTATOR(2),
    COMMENTATOR_AUTOMATION(3);

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
            case RED_GOAL: return "Red Goal";
            case BLUE_GOAL: return "Blue Goal";
            case COMMENTATOR: return "Commentator";
            case COMMENTATOR_AUTOMATION: return "Commentator with Automation";
        }

        return "";
    }
}
