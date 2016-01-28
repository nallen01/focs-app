package me.nallen.fox.app;

public enum MessageType {
    ADD(0),
    SUBTRACT(1),
    SET(2);

    private final int id;
    MessageType(int id) { this.id = id; }
    public int getValue() { return id; }
    public static MessageType fromInt(int id) {
        MessageType[] values = MessageType.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }
}
