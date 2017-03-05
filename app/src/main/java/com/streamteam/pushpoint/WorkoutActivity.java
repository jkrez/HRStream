package com.streamteam.pushpoint;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

public class WorkoutActivity extends AppCompatActivity {

    public UdpMessageListener mUdpSocketSend;
    public UdpMessageListener mUdpSocketReceive;
    public DashboardManger dashboardManger;
    public TextView mConsole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSettings.CurrentActivity = this;
        this.dashboardManger = new DashboardManger(this);
        AppSettings.getInstance().CurrentDashboardManger = this.dashboardManger;
        setContentView(R.layout.content_workout);
        String name = AppSettings.Settings.getString(Constants.NameKey, "");
        TextView welcomeName = (TextView)findViewById(R.id.workout_name_text);
        welcomeName.setText("Hi " + name);
        mConsole = (TextView) findViewById(R.id.console);
        mConsole.setMovementMethod(new ScrollingMovementMethod());
        this.FindDashboard();
    }

    public void WorkoutResponseReceived(WorkoutStatus status) {
        if (status == null) {
            return;
        }

        // TODO update UI thread.
    }

    public void WorkoutCompleted() {

    }

    public void FindDashboard() {
        // Start new handshake
        // create listener socket
        Log.ConsoleLog("Finding Dasboard");
        if (mUdpSocketReceive == null) {
            mUdpSocketReceive = new UdpMessageListener(this.dashboardManger.MessageProssesor, Constants.ReceivePort);
            try {
                mUdpSocketReceive.execute().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Unsused for now. This was copied from SO in case it came in handy.Sp
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}




//public class ReceiveMessage extends AsyncTask<Void, Void, String> {
//
//    public interface TaskListener {
//        public void onFinished(String result);
//    }
//
//    // This is the reference to the associated listener
//    private final TaskListener taskListener;
//
//    public ExampleTask(TaskListener listener) {
//        // The listener reference is passed in through the constructor
//        this.taskListener = listener;
//    }
//
//    @Override
//    protected String doInBackground(Void... params) {
//        return "doSomething()";
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//
//        // In onPostExecute we check if the listener is valid
//        if(this.taskListener != null) {
//
//            // And if it is we call the callback function on it.
//            this.taskListener.onFinished(result);
//        }
//    }
//}
