package com.streamteam.pushpoint;

import static com.streamteam.pushpoint.PushPointZone.*;

public class WorkoutStatus {

    public WorkoutStatus(String classID, String PushPoints, String zone, String calories, String hrZonePercent) {
        this.ClassID = classID;
        this.PushPoints = Integer.parseInt(PushPoints);
        int pushPointZoneNumber = Integer.parseInt(zone);
        if (pushPointZoneNumber == 0) {
            this.Zone = PushPointZone1;
        } else if (pushPointZoneNumber  == 1) {
            this.Zone = PushPointZone2;
        } else if (pushPointZoneNumber  == 2) {
            this.Zone = PushPointZone3;
        } else if (pushPointZoneNumber  == 3) {
            this.Zone = PushPointZone4;
        } else if (pushPointZoneNumber  == 4) {
            this.Zone = PushPointZone5;
        }

        this.Zone = Helpers.searchEnum(PushPointZone.class, PushPoints);
        this.Calories = Integer.parseInt(calories);
        this.HrZonePercent = Integer.parseInt(hrZonePercent);
    }

    public String ClassID;
    public int PushPoints;
    public PushPointZone Zone;
    public int Calories;
    public int HrZonePercent;
}

