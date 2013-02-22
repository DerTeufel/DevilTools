package mobi.cyann.deviltools;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Special preference type that allows configuration of both the ring volume and
 * notification volume.
 */
public class ColorTuningPreference extends DialogPreference implements OnClickListener {
	//private final static String LOG_TAG = "DevilTools.Color";
    enum Colors {
        RED,
        GREEN,
        BLUE
    };

    private static final int[] SEEKBAR_ID = new int[] {
        R.id.color_red_seekbar,
        R.id.color_green_seekbar,
        R.id.color_blue_seekbar
    };

    private static final int[] VALUE_DISPLAY_ID = new int[] {
        R.id.color_red_value,
        R.id.color_green_value,
        R.id.color_blue_value
    };

    private static final int[] GAMMA_SEEKBAR_ID = new int[] {
        R.id.color_red_gamma_seekbar,
        R.id.color_green_gamma_seekbar,
        R.id.color_blue_gamma_seekbar,
    };

    private static final int[] GAMMA_VALUE_DISPLAY_ID = new int[] {
        R.id.color_red_gamma_value,
        R.id.color_green_gamma_value,
        R.id.color_blue_gamma_value
    };

    public static final String[] FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/color_tuning/red_multiplier",
        "/sys/devices/virtual/misc/color_tuning/green_multiplier",
        "/sys/devices/virtual/misc/color_tuning/blue_multiplier"
    };

    public static final String[] GAMMA_FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/color_tuning/red_v1_offset",
        "/sys/devices/virtual/misc/color_tuning/green_v1_offset",
        "/sys/devices/virtual/misc/color_tuning/blue_v1_offset"
    };

   public static final String[] VOODOO_FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/voodoo_color/red_multiplier",
        "/sys/devices/virtual/misc/voodoo_color/green_multiplier",
        "/sys/devices/virtual/misc/voodoo_color/blue_multiplier"
    };

    public static final String[] VOODOO_GAMMA_FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/voodoo_color/red_v1_offset",
        "/sys/devices/virtual/misc/voodoo_color/green_v1_offset",
        "/sys/devices/virtual/misc/voodoo_color/blue_v1_offset"
    };

    private ColorSeekBar mSeekBars[] = new ColorSeekBar[6];

    public static final int MAX_VALUE = Integer.MAX_VALUE;

    private static final int GAMMA_MAX_VALUE = 40;
    public static final int GAMMA_DEFAULT_VALUE = 0;
    private static final int OFFSET_VALUE = 0;
    private static final int VOODOO_GAMMA_MAX_VALUE = 60;
    private static final int VOODOO_GAMMA_DEFAULT_VALUE = 40;
    private static final int VOODOO_OFFSET_VALUE = 40;



    // Track instances to know when to restore original color
    // (when the orientation changes, a new dialog is created before the old one is destroyed)
    private static int sInstances = 0;

    public ColorTuningPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_dialog_color_tuning);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        sInstances++;
	if(!isVoodoo()) { // not voodoo
        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            SeekBar seekBar = (SeekBar) view.findViewById(SEEKBAR_ID[i]);
            TextView valueDisplay = (TextView) view.findViewById(VALUE_DISPLAY_ID[i]);
            mSeekBars[i] = new ColorSeekBar(seekBar, valueDisplay, FILE_PATH[i]);
        }

        for (int i = 0; i < GAMMA_SEEKBAR_ID.length; i++) {
            SeekBar seekBar = (SeekBar) view.findViewById(GAMMA_SEEKBAR_ID[i]);
            TextView valueDisplay = (TextView) view.findViewById(GAMMA_VALUE_DISPLAY_ID[i]);
            mSeekBars[SEEKBAR_ID.length + i] = new GammaSeekBar(seekBar, valueDisplay, GAMMA_FILE_PATH[i]);
        }
	} else { //try voodoo
        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            SeekBar seekBar = (SeekBar) view.findViewById(SEEKBAR_ID[i]);
            TextView valueDisplay = (TextView) view.findViewById(VALUE_DISPLAY_ID[i]);
            mSeekBars[i] = new ColorSeekBar(seekBar, valueDisplay, VOODOO_FILE_PATH[i]);
        }

        for (int i = 0; i < GAMMA_SEEKBAR_ID.length; i++) {
            SeekBar seekBar = (SeekBar) view.findViewById(GAMMA_SEEKBAR_ID[i]);
            TextView valueDisplay = (TextView) view.findViewById(GAMMA_VALUE_DISPLAY_ID[i]);
            mSeekBars[SEEKBAR_ID.length + i] = new GammaSeekBar(seekBar, valueDisplay, VOODOO_GAMMA_FILE_PATH[i]);
        }
	}

        SetupButtonClickListener(view);
    }

    private void SetupButtonClickListener(View view) {
        Button mResetButton = (Button)view.findViewById(R.id.color_reset);
        mResetButton.setOnClickListener(this);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        sInstances--;

        if (positiveResult) {
            for (ColorSeekBar csb : mSeekBars) {
                csb.save();
            }
        } else if (sInstances == 0) {
            for (ColorSeekBar csb : mSeekBars) {
                csb.reset();
            }
        }
    }

    /**
     * Restore screen color tuning from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!isVoodoo()) { // no voodoo
        for (String filePath : FILE_PATH) {
            int value = sharedPrefs.getInt(filePath, MAX_VALUE);
            Utils.writeColor(filePath, value);
        }
        for (String filePath : GAMMA_FILE_PATH) {
            int value = sharedPrefs.getInt(filePath, GAMMA_DEFAULT_VALUE);
            Utils.writeGamma(filePath, value);
        }
	} else { // try vodoo
        for (String filePath : VOODOO_FILE_PATH) {
            int value = sharedPrefs.getInt(filePath, MAX_VALUE);
            Utils.writeColor(filePath, value);
        }
        for (String filePath : VOODOO_GAMMA_FILE_PATH) {
            int value = sharedPrefs.getInt(filePath, VOODOO_GAMMA_DEFAULT_VALUE);
            Utils.writeGamma(filePath, value - VOODOO_OFFSET_VALUE);
        }
	}
    }

    /**
     * Check whether the running kernel supports color tuning or not.
     * @return              Whether color tuning is supported or not
     */
    public static boolean isSupported() {
        boolean supported = true;
        for (String filePath : FILE_PATH) {
            if (!Utils.fileExists(filePath)) {
                supported = false;
            }
        }
        for (String filePath : GAMMA_FILE_PATH) {
            if (!Utils.fileExists(filePath)) {
                supported = false;
            }
        }
	if(!supported) {
	   supported = true;
           for (String filePath : VOODOO_FILE_PATH) {
            	if (!Utils.fileExists(filePath)) {
                   supported = false;
            	}
           }
           for (String filePath : VOODOO_GAMMA_FILE_PATH) {
            	if (!Utils.fileExists(filePath)) {
                   supported = false;
               }
           }
	}

        return supported;
    }


    public static boolean isVoodoo() {
        boolean supported = true;
	boolean voodoo = false;
        for (String filePath : FILE_PATH) {
            if (!Utils.fileExists(filePath)) {
                supported = false;
            }
        }
        for (String filePath : GAMMA_FILE_PATH) {
            if (!Utils.fileExists(filePath)) {
                supported = false;
            }
        }
	if(!supported) {
	   voodoo = true;
           for (String filePath : VOODOO_FILE_PATH) {
            	if (!Utils.fileExists(filePath)) {
                   voodoo = false;
            	}
           }
           for (String filePath : VOODOO_GAMMA_FILE_PATH) {
            	if (!Utils.fileExists(filePath)) {
                   voodoo = false;
               }
           }
	}

        return voodoo;
    }

    class ColorSeekBar implements SeekBar.OnSeekBarChangeListener {

        protected String mFilePath;
        protected int mOriginal;
        protected SeekBar mSeekBar;
        protected TextView mValueDisplay;

        public ColorSeekBar(SeekBar seekBar, TextView valueDisplay, String filePath) {
            mSeekBar = seekBar;
            mValueDisplay = valueDisplay;
            mFilePath = filePath;

            // Read original value
            SharedPreferences sharedPreferences = getSharedPreferences();
            mOriginal = sharedPreferences.getInt(mFilePath, MAX_VALUE);

            seekBar.setMax(MAX_VALUE);
            reset();
            seekBar.setOnSeekBarChangeListener(this);
        }

        // For inheriting class
        protected ColorSeekBar() {
        }

        public void reset() {
            mSeekBar.setProgress(mOriginal);

        if (!isVoodoo()) { // no voodoo
            updateValue(mOriginal);
	} else {
            updateValue(mOriginal - VOODOO_OFFSET_VALUE);
	}

        }

        public void save() {
	DevilTweaksFragment.setPreferenceInteger(mFilePath, mSeekBar.getProgress());
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            Utils.writeColor(mFilePath, progress);
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
            Utils.writeColor(path, value);
        }

    }

    class GammaSeekBar extends ColorSeekBar {

        public GammaSeekBar(SeekBar seekBar, TextView valueDisplay, String filePath) {
            mSeekBar = seekBar;
            mValueDisplay = valueDisplay;
            mFilePath = filePath;

            // Read original value
            SharedPreferences sharedPreferences = getSharedPreferences();
        if (!isVoodoo()) { // no voodoo
            mOriginal = sharedPreferences.getInt(mFilePath, GAMMA_DEFAULT_VALUE);
            seekBar.setMax(GAMMA_MAX_VALUE);
	} else {
            mOriginal = sharedPreferences.getInt(mFilePath, VOODOO_GAMMA_DEFAULT_VALUE);
            seekBar.setMax(VOODOO_GAMMA_MAX_VALUE);
	}

            reset();
            seekBar.setOnSeekBarChangeListener(this);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
	   if(!isVoodoo()) {
            Utils.writeGamma(mFilePath, progress - OFFSET_VALUE);
            updateValue(progress - OFFSET_VALUE);
	   } else {
            Utils.writeGamma(mFilePath, progress - VOODOO_OFFSET_VALUE);
            updateValue(progress - VOODOO_OFFSET_VALUE);
	   }
        }

        @Override
        protected void updateValue(int progress) {
           if (!isVoodoo()) { // no voodoo
            	mValueDisplay.setText("-" + progress);
	   } else {
           	 mValueDisplay.setText("" + progress);
	   }
        }

        public void resetDefault(String path, int value) {
            mSeekBar.setProgress(value);
	   if(!isVoodoo()) {
            updateValue(value - OFFSET_VALUE);
            Utils.writeGamma(path, value - OFFSET_VALUE);
	   } else {
            updateValue(value - VOODOO_OFFSET_VALUE);
            Utils.writeGamma(path, value - VOODOO_OFFSET_VALUE);
	   }
        }

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.color_reset:
		if(!isVoodoo()) { // no voodoo
                for (int i = 0; i < SEEKBAR_ID.length; i++) {
                    mSeekBars[i].resetDefault(FILE_PATH[i], MAX_VALUE);
                }
                for (int i = 0; i < GAMMA_SEEKBAR_ID.length; i++) {
                    mSeekBars[SEEKBAR_ID.length + i].resetDefault(GAMMA_FILE_PATH[i], GAMMA_DEFAULT_VALUE);
                }
		} else { // try voodoo
                for (int i = 0; i < SEEKBAR_ID.length; i++) {
                    mSeekBars[i].resetDefault(VOODOO_FILE_PATH[i], MAX_VALUE);
                }
                for (int i = 0; i < GAMMA_SEEKBAR_ID.length; i++) {
                    mSeekBars[SEEKBAR_ID.length + i].resetDefault(VOODOO_GAMMA_FILE_PATH[i], VOODOO_GAMMA_DEFAULT_VALUE);
                }
		}
                break;
        }
    }

}
