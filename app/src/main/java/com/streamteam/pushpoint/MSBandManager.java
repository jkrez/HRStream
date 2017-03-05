package com.streamteam.pushpoint;

import android.app.Activity;
import android.os.AsyncTask;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import java.lang.ref.WeakReference;

/**
 * Created by john on 6/14/16.
 */
public class MSBandManager {
    private static MSBandManager ourInstance = new MSBandManager();

    public static MSBandManager getInstance() {
        return ourInstance;
    }
    public static BandClient client;
    public HeartRateConsentTask heartRateConsentTask;
    public HeartRateSubscriptionTask heartRateSubscriptionTask;

    private MSBandManager() {
        AppSettings.CurrentMSBandManager = this;
    }

    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                Log.ConsoleLog(String.format("Heart Rate = %d beats per minute\n"
                        + "Quality = %s\n", event.getHeartRate(), event.getQuality()));

                AppSettings.CurrentDashboardManger.BandHeartRateListener(event);
            }
        }
    };

    public void StartStreamingUserMetrics() {
        final WeakReference<Activity> reference = new WeakReference<Activity>(AppSettings.CurrentActivity);
        new HeartRateConsentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reference);
        new HeartRateSubscriptionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void StopStreamingUserMetrics() {
        try {
            client.getSensorManager().unregisterHeartRateEventListeners();
        } catch (BandIOException e) {
            e.printStackTrace();
        }
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {
                    // Check if consent is needed.
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        return null;
                    }
                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                                Log.ConsoleLog("Consent given: " + consentGiven);
                            }
                        });
                    }
                } else {
                    Log.ConsoleLog("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.ConsoleLog(exceptionMessage);

            } catch (Exception e) {
                Log.ConsoleLog(e.getMessage());
            }
            return null;
        }
    }

    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {

                if (getConnectedBandClient()) {
                    while (client.getSensorManager().getCurrentHeartRateConsent() != UserConsent.GRANTED) {
                        Thread.sleep(500);
                    }
                    client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);

                } else {
                    Log.ConsoleLog("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.ConsoleLog(exceptionMessage);

            } catch (Exception e) {
                Log.ConsoleLog(e.getMessage());
            }
            return null;
        }
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Log.ConsoleLog("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(AppSettings.CurrentDashboardManger.mActivity.getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        Log.ConsoleLog("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }
}
