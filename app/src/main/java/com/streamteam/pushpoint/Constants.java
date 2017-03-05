package com.streamteam.pushpoint;

/**
 * Created by john on 6/6/16.
 */

public final class Constants {
    public static final String SharedPreferencesSettings = "PushPointPreferences";

    // Shared Preferences Keys
    public static final String NameKey = "name";
    public static final String AgeKey = "age";
    public static final String HrKey = "hrkey";
    public static final String PushPointGoalKey = "pushpointgoalkey";

    // Connection constants
    public static int ReceivePort = 9998;
    public static int SendPort = 9999;
    public static int MaxPacketSize = 1500; // Bytes
    public static String BandDash = "BandDash";
    public static String BandDashAck = "SignAck";
    public static String BandDashWorkout = "WOResponse";
    public static String BandDashWorkoutComplete = "WOComplete";

}
