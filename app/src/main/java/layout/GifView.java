package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;

/**
 * Created by john on 6/8/16.
 */

public class GifView extends View {

    private Movie mMovie;
    private long mMovieStart;

    public GifView(Context context) throws IOException {
        super(context);
        setFocusable(true);

        java.io.InputStream is;
        is = context.getAssets().open("running.gif");
        mMovie = Movie.decodeStream(is);
    }

    public GifView(Context context, AttributeSet attrSet) throws IOException {
        super(context, attrSet);
        setFocusable(true);

        java.io.InputStream is;
        is = context.getAssets().open("running.gif");
        mMovie = Movie.decodeStream(is);
    }

    public GifView(Context context, AttributeSet attrSet, int defStyle) throws IOException {
        super(context, attrSet, defStyle);
        setFocusable(true);

        java.io.InputStream is;
        is = context.getAssets().open("running.gif");
        mMovie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x00000000);

        Paint p = new Paint();
        p.setAntiAlias(true);

        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) { // first time
            mMovieStart = now;
        }
        if (mMovie != null) {
            int dur = mMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            int relTime = (int) ((now - mMovieStart) % dur);
            mMovie.setTime(relTime);
            mMovie.draw(canvas, getWidth() / 2 - mMovie.width() / 2,
                    getHeight() / 2 - mMovie.height() / 2);
            invalidate();
        }
    }
}