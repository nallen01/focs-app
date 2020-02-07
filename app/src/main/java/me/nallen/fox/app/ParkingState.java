package me.nallen.fox.app;

public enum ParkingState {
    NONE(0),
    ALLIANCE_PARKED(1),
    CENTRE_PARKED(2);

    private final int id;
    ParkingState(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ParkingState fromInt(int id) {
        ParkingState[] values = ParkingState.values();
        for (ParkingState value : values) {
            if (value.getValue() == id)
                return value;
        }
        return null;
    }
}
