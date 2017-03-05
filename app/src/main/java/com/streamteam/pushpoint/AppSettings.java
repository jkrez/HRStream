package com.streamteam.pushpoint;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;

import java.util.concurrent.ThreadPoolExecutor;

public class AppSettings {
    private static AppSettings ourInstance = new AppSettings();

    public static AppSettings getInstance() {
        return ourInstance;
    }
    public static Context AppContext;
    public static DashboardManger CurrentDashboardManger;
    public static MSBandManager CurrentMSBandManager;
    public static Activity CurrentActivity;

    private AppSettings() {
    }

    public static void init(Context appContext) {
        AppContext = appContext;
        Settings = appContext.getSharedPreferences(Constants.SharedPreferencesSettings, 0);
    }

    public static SharedPreferences Settings;

    // Dashboard settings
    public DashboardManger CurrentDashboard;

    public static String getUserId() {
        return Secure.getString(
                AppSettings.AppContext.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public static void ClearSettings() {
            SharedPreferences.Editor editor = AppSettings.Settings.edit();
            editor.clear();
            editor.commit();
    }

    public static Activity getCurrentActivity() {
        return CurrentActivity;
    }
}