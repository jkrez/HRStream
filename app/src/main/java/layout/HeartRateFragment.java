package layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.streamteam.pushpoint.AppSettings;
import com.streamteam.pushpoint.Constants;
import com.streamteam.pushpoint.FirstLoginActivity;
import com.streamteam.pushpoint.R;
import com.streamteam.pushpoint.WorkoutActivity;

import static com.streamteam.pushpoint.Helpers.isWhitespace;

public class HeartRateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_heart_rate, container, false);

        EditText hrText = (EditText) v.findViewById(R.id.resting_hr_textbox);

        // Get the previous pages data in edit boxes and save to Shared Preferences
        SharedPreferences settings = AppSettings.Settings;
        String age = settings.getString(Constants.AgeKey, "");
        boolean agePopulated = !isWhitespace(settings.getString(Constants.AgeKey, ""));
        String name = settings.getString(Constants.NameKey, "");
        boolean namePopulated = !isWhitespace(settings.getString(Constants.NameKey, ""));
        String hr = hrText.getText().toString();
        boolean hrPopulated = !isWhitespace(hr);

        // Add a fragment tag to look up later
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.workoutButton);
        TextView text = (TextView) v.findViewById(R.id.textView4);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                if (((FirstLoginActivity) getActivity()).ReadyToWorkout == true) {
                    SharedPreferences settings = AppSettings.Settings;
                    SharedPreferences.Editor editor = settings.edit();
                    EditText hrText = (EditText) getView().findViewById(R.id.resting_hr_textbox);
                    editor.putString(Constants.HrKey, hrText.getText().toString());
                    editor.commit();

                    // Launch home activity
                    Intent intent = new Intent(getActivity(), WorkoutActivity.class);
                    startActivity(intent);
                    Activity a = getActivity();
                    if (a != null) {
                        a.finish();
                    }
                }
            }
        };

        text.setOnClickListener(listener);
        button.setOnClickListener(listener);

        if (agePopulated == true && namePopulated == true && hrPopulated == true) {
            button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            button.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{R.color.colorAccent}));
        } else {
            button.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
            button.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{R.color.colorDisabled}));
        }

        hrText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences settings = AppSettings.Settings;
                boolean agePopulated = !isWhitespace(settings.getString(Constants.AgeKey, ""));
                boolean namePopulated = !isWhitespace(settings.getString(Constants.NameKey, ""));

                FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.workoutButton);
                if (agePopulated == true && namePopulated == true && !isWhitespace(s.toString())) {
                    button.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{Color.parseColor("#68b2d7")}));
//                    button.setBackgroundColor(ContextCompat.getColor(getContext(), Color.parseColor("#68b2d7")));
                    ((FirstLoginActivity) getActivity()).ReadyToWorkout = true;
                } else {
                    button.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{R.color.colorDisabled}));
//                    button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDisabled));
                    ((FirstLoginActivity) getActivity()).ReadyToWorkout = false;
                }
            }
        });

        return v;
    }

    public static HeartRateFragment newInstance(String text) {

        HeartRateFragment f = new HeartRateFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);

        return f;
    }
}


