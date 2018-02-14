package me.nallen.fox.app;

public enum FoxDisplay {
    SCIENCE(1),
    TECHNOLOGY(2),
    NONE(0);

    private final int id;
    FoxDisplay(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ScoringZone fromInt(int id) {
        ScoringZone[] values = ScoringZone.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }
}
