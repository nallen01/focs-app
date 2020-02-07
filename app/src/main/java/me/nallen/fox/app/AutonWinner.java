package me.nallen.fox.app;

public enum AutonWinner {
    NONE(0),
    RED(1),
    BLUE(2),
    TIE(3);

    private final int id;
    AutonWinner(int id) { this.id = id; }
    public int getValue() { return id; }
    public static AutonWinner fromInt(int id) {
        AutonWinner[] values = AutonWinner.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }
}
