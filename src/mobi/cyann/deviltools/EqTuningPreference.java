package mobi.cyann.deviltools;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.util.Log;

/**
 * Special preference type that allows configuration of both the ring volume and
 * notification volume.
 */
public class EqTuningPreference extends DialogPreference implements OnClickListener {
        private final static String LOG_TAG = "DevilTools.Eq";
    enum Eqs {
        BAND1,
        BAND2,
        BAND3,
        BAND4,
        BAND5
    };

    private static final int[] SEEKBAR_ID = new int[] {
        R.id.eq_b1_seekbar,
        R.id.eq_b2_seekbar,
        R.id.eq_b3_seekbar,
        R.id.eq_b4_seekbar,
        R.id.eq_b5_seekbar
    };

    private static final int[] VALUE_DISPLAY_ID = new int[] {
        R.id.eq_b1_value,
        R.id.eq_b2_value,
        R.id.eq_b3_value,
        R.id.eq_b4_value,
        R.id.eq_b5_value
    };

    public static final String[] FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b1_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b2_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b3_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b4_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b5_gain"
    };

    private EqSeekBar mSeekBars[] = new EqSeekBar[5];

    public static final int MAX_VALUE = 12;
    public static final int DEFAULT_VALUE = 0;

    // Track instances to know when to restore original eq value
    // (when the orientation changes, a new dialog is created before the old one is destroyed)
    private static int sInstances = 0;

    public EqTuningPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_dialog_eq_tuning);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        sInstances++;

        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            SeekBar seekBar = (SeekBar) view.findViewById(SEEKBAR_ID[i]);
            TextView valueDisplay = (TextView) view.findViewById(VALUE_DISPLAY_ID[i]);
            mSeekBars[i] = new EqSeekBar(seekBar, valueDisplay, FILE_PATH[i]);
        }

        SetupButtonClickListener(view);
    }

    private void SetupButtonClickListener(View view) {
        Button mResetButton = (Button)view.findViewById(R.id.eq_reset);
        mResetButton.setOnClickListener(this);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        sInstances--;

        if (positiveResult) {
            for (EqSeekBar csb : mSeekBars) {
                csb.save();
            }
        } else if (sInstances == 0) {
            for (EqSeekBar csb : mSeekBars) {
                csb.reset();
            }
        }
    }

    /**
     * Restore eq tuning from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (String filePath : FILE_PATH) {
            int value = sharedPrefs.getInt(filePath, DEFAULT_VALUE);
            Utils.writeEq(filePath, value);
        }
    }

    /**
     * Check whether the running kernel supports eq tuning or not.
     * @return              Whether eq tuning is supported or not
     */
    public static boolean isSupported() {
        boolean supported = true;
        for (String filePath : FILE_PATH) {
            if (!Utils.fileExists(filePath)) {
                supported = false;
            }
        }

        return supported;
    }

    class EqSeekBar implements SeekBar.OnSeekBarChangeListener {

        protected String mFilePath;
        protected int mOriginal;
        protected SeekBar mSeekBar;
        protected TextView mValueDisplay;

        public EqSeekBar(SeekBar seekBar, TextView valueDisplay, String filePath) {
            mSeekBar = seekBar;
            mValueDisplay = valueDisplay;
            mFilePath = filePath;

            // Read original value
            SharedPreferences sharedPreferences = getSharedPreferences();
            mOriginal = sharedPreferences.getInt(mFilePath, DEFAULT_VALUE);

            seekBar.setMax(MAX_VALUE);
            reset();
            seekBar.setOnSeekBarChangeListener(this);
        }

        // For inheriting class
        protected EqSeekBar() {
        }

        public void reset() {
            mSeekBar.setProgress(mOriginal);
            updateValue(mOriginal);
        }

        public void save() {
            Editor editor = getEditor();
            editor.putInt(mFilePath, mSeekBar.getProgress());
            editor.commit();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            Utils.writeEq(mFilePath, progress);
            updateValue(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        protected void updateValue(int progress) {
            mValueDisplay.setText(String.format("%.3f", (double) progress / MAX_VALUE));
        }

        public void resetDefault(String path, int value) {
            mSeekBar.setProgress(value);
            updateValue(value);
            Utils.writeEq(path, value);
        }

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.eq_reset:
                for (int i = 0; i < SEEKBAR_ID.length; i++) {
                    mSeekBars[i].resetDefault(FILE_PATH[i], DEFAULT_VALUE);
                }
                break;
        }
    }

}
