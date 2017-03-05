package com.streamteam.pushpoint;


import android.widget.TextView;

/**
 * Created by john on 6/9/16.
 */

public class Log {
    public static void ConsoleLog(String str) {
        if (AppSettings.CurrentActivity != null) {
            Log.appendToUI(str);
        }
        android.util.Log.v("Console_Log", str);
    }

    public static void appendToUI(final String string) {
        AppSettings.CurrentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.UpdateConsoleUI(string);
            }
        });
    }

    public static void UpdateConsoleUI(String str) {
        TextView tv = (TextView) AppSettings.CurrentActivity.findViewById(R.id.console);
        tv.append(str);
        tv.append("\n");
    }

    public static void ConsoleLogWarning(String str) {
        android.util.Log.w("Console_Warning", str);
    }
}
