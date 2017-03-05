package layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by john on 6/7/16.
 */

public class EditTextSingle extends EditText
{
    public EditTextSingle(Context context) {
        super(context);
        init();
    }

    public EditTextSingle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextSingle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode==KeyEvent.KEYCODE_ENTER)
        {
            // Just ignore the [Enter] key
            return true;
        }
        // Handle all other keys in the default way
        return super.onKeyDown(keyCode, event);
    }
}