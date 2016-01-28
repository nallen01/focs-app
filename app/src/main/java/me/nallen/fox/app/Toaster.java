package me.nallen.fox.app;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    private static Toast mToast;

    public static void doToast(Context context, String message) {
        if(mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(message);
        }
        mToast.show();
    }
}
