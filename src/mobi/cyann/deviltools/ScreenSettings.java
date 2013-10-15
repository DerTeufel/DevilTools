/**
 * DevilPreference.java
 * Jan 14, 2012 9:36:34 AM
 */
package mobi.cyann.deviltools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.cyann.deviltools.PreferenceListFragment.OnPreferenceAttachedListener;
import mobi.cyann.deviltools.ColorTuningPreference;
import mobi.cyann.deviltools.preference.IntegerPreference;
import mobi.cyann.deviltools.SysCommand;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;

/**
 * @DerTeufel1980
 *
 */
public class ScreenSettings extends BasePreferenceFragment implements OnPreferenceChangeListener {
	public ScreenSettings() {
		super(R.layout.screen);
	}
	
    	private ColorTuningPreference mColor;
    	private ListPreference mMdnie;
    	private ListPreference mVoodooPre;
	private PreferenceScreen mVoodooColor;

        private ContentResolver mContentResolver;
	private static SharedPreferences preferences;

	@Override
    	public void onPreferenceAttached(PreferenceScreen rootPreference, int xmlId) {

	preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceScreen prefSet = getPreferenceScreen();
        mContentResolver = getActivity().getApplicationContext().getContentResolver();

    	final PreferenceCategory VoodooColorCategory =
            (PreferenceCategory) prefSet.findPreference("key_voodoo_color_category");
	mVoodooColor = (PreferenceScreen) prefSet.findPreference("key_voodoo_color");

        mMdnie = (ListPreference) findPreference("mdnie");
        mMdnie.setEnabled(Mdnie.isSupported());

  	mColor = (ColorTuningPreference) findPreference("color_tuning");
        mColor.setEnabled(ColorTuningPreference.isSupported());

    	mVoodooPre = (ListPreference) findPreference("key_voodoo_gamma_presets");
	if (ColorTuningPreference.isVoodoo()) {
    	mVoodooPre.setOnPreferenceChangeListener(this);
	}
        mVoodooPre.setEnabled(ColorTuningPreference.isVoodoo());

        if (!Mdnie.isSupported() && !ColorTuningPreference.isSupported()) {
            mVoodooColor.removePreference(mMdnie);
            mVoodooColor.removePreference(mColor);
	    prefSet.removePreference(VoodooColorCategory);
        } else if (Mdnie.isSupported()) {
        mMdnie.setOnPreferenceChangeListener(new Mdnie());
        }

	super.onPreferenceAttached(rootPreference, xmlId);
    }
	
    public boolean onPreferenceChange(Preference preference, Object objValue) {
	SysCommand sc = SysCommand.getInstance();
	int status, index;
	if (preference == mVoodooPre) {
            status = Integer.valueOf((String) objValue);
            index = mVoodooPre.findIndexOfValue((String) objValue);
            mVoodooPre.setSummary(mVoodooPre.getEntries()[index]);
            switch(status){
            	case 0:
                    ColorTuningPreference.Preset(0);
                    break;
            	case 1:
                    ColorTuningPreference.Preset(1);
                    break;
            	case 2:
                    ColorTuningPreference.Preset(2);
                    break;
            	default:
                    ColorTuningPreference.Preset(0);
                    break;
	    }
            return true;
        }

        return false;
    }

    public static void setPreferenceString(String key, String value) {
	Editor ed = preferences.edit();
	ed.putString(key, value);
	ed.commit();
    }

    public static void setPreferenceInteger(String key, int value) {
	Editor ed = preferences.edit();
	ed.putInt(key, value);
	ed.commit();
    }

}
