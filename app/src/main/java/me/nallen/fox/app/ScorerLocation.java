package me.nallen.fox.app;

public enum ScorerLocation {
    RED_FAR_ZONE(0),
    RED_NEAR_ZONE(1),
    RED_ZONE(6),
    RED_STARS(8),
    BLUE_FAR_ZONE(2),
    BLUE_NEAR_ZONE(3),
    BLUE_ZONE(7),
    BLUE_STARS(9),
    CUBES(10),
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
            case RED_FAR_ZONE: return "Red Far Zone";
            case RED_NEAR_ZONE: return "Red Near Zone";
            case RED_ZONE: return "Red All Zones";
            case RED_STARS: return "Red Stars";
            case BLUE_FAR_ZONE: return "Blue Far Zone";
            case BLUE_NEAR_ZONE: return "Blue Near Zone";
            case BLUE_ZONE: return "Blue All Zones";
            case BLUE_STARS: return "Blue Stars";
            case CUBES: return "Cubes, Elevation, Auton";
            case COMMENTATOR: return "Commentator";
            case COMMENTATOR_AUTOMATION: return "Commentator with Automation";
        }

        return "";
    }

    public static ScorerLocation[] getValues() {
        ScorerLocation[] values = {
            RED_ZONE,
            RED_STARS,
            BLUE_ZONE,
            BLUE_STARS,
            CUBES,
            COMMENTATOR
        };

        return values;
    }
}
