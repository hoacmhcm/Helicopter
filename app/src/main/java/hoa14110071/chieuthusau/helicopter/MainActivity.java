package hoa14110071.chieuthusau.helicopter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private static final String TAG = "Game";
    private static final String HIGHSCORE = "Record";
    private GamePanel panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // get last highscore from shared preferences
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int currentHighScore = sharedPref.getInt(HIGHSCORE, 0);
        panel = new GamePanel(this, currentHighScore);

        // set listener for handling new high score
        panel.setHighScoreListener(new GamePanel.HighScoreListener() {
            @Override
            public void onHighScoreUpdated(int best) {
                // code to handle updates
                Log.i(TAG, "onHighScoreUpdated: new best value = " + best);

                // Update shared preferences
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(HIGHSCORE, best);
                editor.commit();
            }
        });
        setContentView(panel);
    }
}
