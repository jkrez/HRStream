package com.streamteam.pushpoint;

import android.content.SharedPreferences;
import android.widget.TextView;

import com.microsoft.band.sensors.BandHeartRateEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Locale;

import static com.streamteam.pushpoint.Constants.BandDash;

/**
 * Created by john on 6/11/16.
 */
public class DashboardManger {
    public WorkoutActivity mActivity;
    public UdpClientReceive MessageProssesor = new UdpClientReceive();
    private DatagramSocket udpSendSocket;
    private ClassModel currentClass;
    private int udpSendPort = 8000;
    private UserSettingsModel userSettingsModule;
    private int udpSendCount = 0;
    private int udpReceiveCount = 0;
    private MSBandManager currentMSBandManager;
    public int HR = 0;
    public int udpSendTag = 0;
    public int HRQ = 0;
    public int calories = 0;

    public DashboardManger(WorkoutActivity activity) {
        this.mActivity = activity;

        // Set up user settings module
        SharedPreferences settings = AppSettings.getInstance().Settings;
        this.userSettingsModule = new UserSettingsModel();
        this.userSettingsModule.Age = Integer.parseInt(settings.getString(Constants.AgeKey, ""));
        this.userSettingsModule.Name = settings.getString(Constants.NameKey, "");
        this.userSettingsModule.OnboardingComplete = false;
        this.userSettingsModule.PushPointGoal = Integer.parseInt(settings.getString(Constants.PushPointGoalKey, "1000"));
        this.userSettingsModule.RestingHR = Integer.parseInt(settings.getString(Constants.HrKey, ""));

        //Register with App settings
        AppSettings.CurrentDashboardManger = this;

        this.currentMSBandManager = MSBandManager.getInstance();
    }

    public class UdpClientReceive implements UdpMessageListener.UdpPacketProcessor {

        public UdpClientReceive() {
        }

        public void onPacketReceived(DatagramPacket receivedPacket, int localPort) {
            if (receivedPacket == null) {
                return;
            }

            AppSettings.CurrentDashboardManger.DashboardPacketReceived(receivedPacket);
        }
    }

    public void DashboardPacketReceived(DatagramPacket receivedPacket) {
        this.udpReceiveCount++;
        String host = receivedPacket.getAddress().toString();
        int port = receivedPacket.getPort();
        String msg;
        try {
            msg = new String(receivedPacket.getData(), "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.ConsoleLogWarning("Could not parse message");
            return;
        }
        String[] comps = msg.split(",");

        Log.ConsoleLog(String.format(Locale.getDefault(), "Message '%s'", msg));
        //  0         1           2           3  4
        // 	"BandDash,SessionName,SessionGuid,IP,Port"
        if (comps.length == 5 && comps[0].equals(BandDash)) {
            Log.ConsoleLog(String.format("Dashboard: %s:%d - %s", host, port, msg));
            String classId = comps[2];
            if (this.currentClass == null ||
                    (this.currentClass != null && !this.currentClass.ClassId.equals(classId))) {

                this.currentClass = new ClassModel(classId, comps[3], comps[1], comps[4]);
                this.SignIntoClass(currentClass);
                Log.ConsoleLog(String.format("DashBand SessionID(now %s) changed, sign in again", comps[2]));
            }
        } else if (comps.length == 4 && comps[0].equals(BandDash) && comps[1].equals(Constants.BandDashAck)) {
            Log.ConsoleLog(String.format("Dashboard: %s:%d - %s", host, port, msg));
            // Now that we got an Ack, start sending WO data, when we get a
            // WOResponse we can transition to the Workout view. To do this, we
            // register for user metric readings from the Band
            if (this.currentClass.Active == false) {

                // Set class active after receiving the first ACK.
                this.currentClass.Active = true;

                AppSettings.CurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView workoutClass = (TextView) AppSettings.CurrentActivity.findViewById(R.id.workout_status_info);
                        if (workoutClass != null) {
                            workoutClass.setText("Class started!");
                        }
                    }
                });

                // TODO: Need to create Dashboard handlers for heart rate subscription and
                this.currentMSBandManager.StartStreamingUserMetrics();
            }

        } else if (comps.length == 8 && comps[0].equals(BandDash) && comps[1].equals(Constants.BandDashWorkout)) {
            // BandDash,WOResponse,<SessionId>,<UserId>,<PushPoints>,<ZoneNumber>,<Cal>,<HR%>"
            Log.ConsoleLog(String.format(Locale.getDefault(), "Dashboard: %s:%d - %s", host, port, msg));
            // Workout response
            // When we receive a WOResponse we will let any waiting view know
            // so they can transition to the active workout view and then we
            // can start updating the Workout view.

            this.mActivity.WorkoutResponseReceived(new WorkoutStatus(comps[3], comps[4], comps[5], comps[6], comps[7]));
        } else if (comps.length == 25 && comps[0].equals(BandDash) && comps[1].equals(Constants.BandDashWorkoutComplete)) {
            Log.ConsoleLog(String.format(Locale.getDefault(), "Dashboard: %s:%d - %s", host, port, msg));

            // Workout complete - stop streaming update activity
            AppSettings.CurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView workoutStatus = (TextView) AppSettings.CurrentActivity.findViewById(R.id.workout_status_info);
                    if (workoutStatus != null) {
                        workoutStatus.setText("Class complete!!");
                    }
                }
            });

            this.currentMSBandManager.StopStreamingUserMetrics();
            this.mActivity.WorkoutCompleted();
            // TODO: End notification screen with summary data
        } else {
            Log.ConsoleLogWarning(String.format(Locale.getDefault(), "Unrecognized msg: %s", msg));
        }
    }

    public void BandHeartRateListener(final BandHeartRateEvent event) {
        UserMetricsModel metrics = new UserMetricsModel();
        metrics.HeartRate = event.getHeartRate();
        metrics.HeartRateQuality = event.getQuality().ordinal();
        metrics.Calories = this.calories;
        this.SendUserMetrics(metrics, this.currentClass.ClassId);
    }

    public void SendUserMetrics(UserMetricsModel metrics, String classId) {
        if (this.udpSendSocket == null) {
            Log.ConsoleLogWarning("Tried to send user metrics will null socket");
            return;
        }

        // WO,UUID,HR,HRQ,Calories
        // WO,UserID,HR,HRQ,Calories,deviceType
        Log.ConsoleLog("Creating Workout Msg");
//        String msg = String.format(
//                Locale.getDefault(),
//                "WO,%s,%d,%d,%d",
//                AppSettings.getUserId(),
//                metrics.HeartRate,
//                metrics.HeartRateQuality,
//                metrics.Calories);

        String msg = String.format(
                Locale.getDefault(),
                "WO,%s,%d,%d,%d,0",
                AppSettings.getUserId(),
                metrics.HeartRate,
                metrics.HeartRateQuality != 0 ? 10 : 0,
                metrics.Calories);

        byte[] data = Helpers.StringToBytes(msg);
        DatagramPacket sendMsg = new DatagramPacket(data, data.length, this.currentClass.IpAddress, this.currentClass.Port);
        try {
            this.udpSendSocket.send(sendMsg);
            this.udpSendCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.ConsoleLog(String.format("Workout Data Sent: %s", msg));
    }

    private void SignIntoClass(ClassModel currentClassIn) {
        if (this.udpSendSocket == null) {
            Log.ConsoleLog("Creating send socket");
            try {
                this.udpSendSocket = new DatagramSocket(Constants.SendPort);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        // Send Sign In to Dashboard
        if (this.udpSendSocket != null) {
            // SI,UUID,Name,Age,RestingHR,PushPointGoal
            // SI,UserID,Name,Age,RestingHR,PushPointGoal,weightInGrams,heightInCM,gender
            Log.ConsoleLog("Creating sign in to class msg");
//            String msg = String.format(
//                    Locale.getDefault(),
//                    "SI,%s,%s,%d,%d,%d",
//                    AppSettings.getUserId(),
//                    this.userSettingsModule.Name,
//                    this.userSettingsModule.Age,
//                    this.userSettingsModule.RestingHR,
//                    this.userSettingsModule.PushPointGoal);

            String msg = String.format(
                    Locale.getDefault(),
                    "SI,%s,%s,%d,%d,%d,81646,190,0",
                    AppSettings.getUserId(),
                    this.userSettingsModule.Name,
                    this.userSettingsModule.Age,
                    this.userSettingsModule.RestingHR,
                    this.userSettingsModule.PushPointGoal);

            byte[] data = Helpers.StringToBytes(msg);
            Log.ConsoleLog("Sending sign in");
            DatagramPacket sendMsg = new DatagramPacket(data, data.length, currentClassIn.IpAddress, currentClassIn.Port);
            try {
                this.udpSendSocket.send(sendMsg);
                this.udpSendCount++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.ConsoleLog(String.format("Sign In: %s", msg));
            final String sessionName = currentClassIn.SessionName;
            AppSettings.CurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Update UI
                    TextView workoutClass = (TextView) AppSettings.CurrentActivity.findViewById(R.id.workout_class_info);
                    if (workoutClass != null) {
                        workoutClass.setText(String.format(Locale.getDefault(), "Joined %s", sessionName));
                    }

                    TextView workoutStatus = (TextView) AppSettings.CurrentActivity.findViewById(R.id.workout_status_info);
                    if (workoutStatus != null) {
                        workoutStatus.setText("Waiting to start");
                    }
                }});

            this.currentClass = currentClassIn;
        } else {
            Log.ConsoleLogWarning("No UDP Send Socket available for Sign In Msg");
        }
    }
}
