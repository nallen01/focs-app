package me.nallen.fox.app;

public enum ElevatedState {
    NONE(0),
    LOW(1),
    HIGH(2);

    private final int id;
    ElevatedState(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ElevatedState fromInt(int id) {
        ElevatedState[] values = ElevatedState.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }
}