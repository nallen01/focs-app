package me.nallen.fox.app;

public enum FoxDisplay {
    SCIENCE(1),
    TECHNOLOGY(2),
    NONE(0);

    private final int id;
    FoxDisplay(int id) { this.id = id; }
    public int getValue() { return id; }
    public static FoxDisplay fromInt(int id) {
        FoxDisplay[] values = FoxDisplay.values();
        for (FoxDisplay value : values) {
            if (value.getValue() == id)
                return value;
        }
        return null;
    }
}
