package me.nallen.fox.app;

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;

public enum CubeType {
    NONE(0),
    ORANGE(1),
    GREEN(2),
    PURPLE(3);

    private final int id;
    CubeType(int id) { this.id = id; }
    public int getValue() { return id; }
    public static CubeType fromInt(int id) {
        CubeType[] values = CubeType.values();
        for(int i=0; i<values.length; i++) {
            if(values[i].getValue() == id)
                return values[i];
        }
        return null;
    }

    public int getColor() {
        switch(this) {
            case NONE: return Color.rgb(189, 189, 189);
            case ORANGE: return Color.rgb(251, 140, 0);
            case GREEN: return Color.rgb(67, 160, 71);
            case PURPLE: return Color.rgb(142, 36, 170);
        }

        return Color.rgb(255, 255, 255);
    }
}
