package com.streamteam.pushpoint;

import java.util.UUID;

/**
 * Created by john on 6/13/16.
 */

public class UserSettingsModel {
    public UUID UserID;
    public String Name;
    public int Age;
    public int RestingHR;
    public int PushPointGoal;
    public boolean OnboardingComplete;
    public ClassModel CurrentClass;
// This class is a singleton so we do not accidently create more than one
}
