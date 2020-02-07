package me.nallen.fox.app;

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;

public enum ToggleState {
    NONE(0),
    RED(1),
    BLUE(2);

    private final int id;
    ToggleState(int id) { this.id = id; }
    public int getValue() { return id; }
    public static ToggleState fromInt(int id) {
        ToggleState[] values = ToggleState.values();
        for (ToggleState value : values) {
            if (value.getValue() == id)
                return value;
        }
        return null;
    }

    public static ToggleState fromPosition(int pos) {
        switch(pos) {
            case 0: return BLUE;
            case 1: return NONE;
            case 2: return RED;
        }
        return null;
    }

    public int getPosition() {
        switch(this) {
            case BLUE: return 0;
            case NONE: return 1;
            case RED: return 2;
        }

        return 0;
    }
}
