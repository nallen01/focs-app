package me.nallen.fox.app;

public enum ScoringZone {
    NONE(0),
    FIVE_POINT(1),
    TEN_POINT(2),
    TWENTY_POINT(3);

    private final int id;
    ScoringZone(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ScoringZone fromInt(int id) {
        ScoringZone[] values = ScoringZone.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }

    public int getScore() {
        switch(this) {
            case NONE: return 0;
            case FIVE_POINT: return 5;
            case TEN_POINT: return 10;
            case TWENTY_POINT: return 20;
            default: return 0;
        }
    }
}