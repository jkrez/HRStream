package layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.streamteam.pushpoint.AppSettings;
import com.streamteam.pushpoint.Constants;
import com.streamteam.pushpoint.FirstLoginActivity;
import com.streamteam.pushpoint.R;


public class AgeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_age, container, false);

        //TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
        //tv.setText(getArguments().getString("msg"));

        EditText et = (EditText) v.findViewById(R.id.age_textbox);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    //get shared settings
                    SharedPreferences settings = AppSettings.Settings;
                    SharedPreferences.Editor editor = settings.edit();

                    //get text from edittext field...
                    EditText ageText = (EditTextSingle)v.findViewById(R.id.age_textbox);
                    editor.putString(Constants.AgeKey, ageText.getText().toString());
                    editor.commit();
                }
            }
        });

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    FirstLoginActivity fla = ((FirstLoginActivity)getActivity());
                    fla.NextFragment();
                }
                return true;
            }
        });

        return v;
    }

    public static AgeFragment newInstance(String text) {

        AgeFragment f = new AgeFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}