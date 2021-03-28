package me.nallen.fox.app;

import android.graphics.Color;

public enum BallType {
    NONE(0),
    RED(1),
    BLUE(2);

    private final int id;
    BallType(int id) { this.id = id; }
    public int getValue() { return id; }
    public static BallType fromInt(int id) {
        BallType[] values = BallType.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }

    public int getColor() {
        switch(this) {
            case NONE: return Color.rgb(189, 189, 189);
            case RED: return Color.rgb(244, 67, 54);
            case BLUE: return Color.rgb(33, 150, 243);
        }

        return Color.rgb(255, 255, 255);
    }
}
