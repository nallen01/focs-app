package me.nallen.fox.app;

public enum HistoryMethod {
    NONE(0),
    CORNER(1),
    SIDE(2),
    FULL(3);

    private final int id;
    HistoryMethod(int id) { this.id = id; }
    public int getValue() { return id; }
    public static HistoryMethod fromInt(int id) {
        HistoryMethod[] values = HistoryMethod.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }
}
