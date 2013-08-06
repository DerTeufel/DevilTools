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
public class SpeakerOffsetPreference extends DialogPreference implements OnClickListener {
        private final static String LOG_TAG = "DevilTools.Offset";

    private static final int SEEKBAR_ID = R.id.offset_seekbar;

    private static final int VALUE_DISPLAY_ID = R.id.offset_value;

    public static final String FILE_PATH = "/sys/devices/virtual/misc/scoobydoo_sound/speaker_offset";

    private OffsetSeekBar mSeekBars;

    public static final int MAX_VALUE = 12;
    public static final int DEFAULT_VALUE = 6;
    public static final int OFFSET_VALUE = 6;

    // Track instances to know when to restore original eq value
    // (when the orientation changes, a new dialog is created before the old one is destroyed)
    private static int sInstances = 0;

    public SpeakerOffsetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_dialog_amp_offset);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        sInstances++;

            SeekBar seekBar = (SeekBar) view.findViewById(SEEKBAR_ID);
            TextView valueDisplay = (TextView) view.findViewById(VALUE_DISPLAY_ID);
            mSeekBars = new OffsetSeekBar(seekBar, valueDisplay, FILE_PATH);

        SetupButtonClickListener(view);
    }

    private void SetupButtonClickListener(View view) {
        Button mResetButton = (Button)view.findViewById(R.id.offset_reset);
        mResetButton.setOnClickListener(this);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        sInstances--;

        if (positiveResult) {
                mSeekBars.save();
        } else if (sInstances == 0) {
                mSeekBars.reset();
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
            int value = sharedPrefs.getInt(FILE_PATH, DEFAULT_VALUE);
            Utils.writeEq(FILE_PATH, value - OFFSET_VALUE);
    }

    /**
     * Check whether the running kernel supports eq tuning or not.
     * @return              Whether eq tuning is supported or not
     */
    public static boolean isSupported() {
        boolean supported = true;
            if (!Utils.fileExists(FILE_PATH)) {
                supported = false;
        }

        return supported;
    }

    class OffsetSeekBar implements SeekBar.OnSeekBarChangeListener {

        protected String mFilePath;
        protected int mOriginal;
        protected SeekBar mSeekBar;
        protected TextView mValueDisplay;

        public OffsetSeekBar(SeekBar seekBar, TextView valueDisplay, String filePath) {
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
        protected OffsetSeekBar() {
        }

        public void reset() {
            mSeekBar.setProgress(mOriginal);
            updateValue(mOriginal - OFFSET_VALUE);
        }

        public void save() {
            Editor editor = getEditor();
            editor.putInt(mFilePath, mSeekBar.getProgress());
            editor.commit();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            Utils.writeEq(mFilePath, progress - OFFSET_VALUE);
            updateValue(progress - OFFSET_VALUE);
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
            mValueDisplay.setText("" + progress);
        }

        public void resetDefault(String path, int value) {
            mSeekBar.setProgress(value);
            updateValue(value - OFFSET_VALUE);
            Utils.writeEq(path, value - OFFSET_VALUE);
        }

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.offset_reset:
                    mSeekBars.resetDefault(FILE_PATH, DEFAULT_VALUE);
                break;
        }
    }

}
