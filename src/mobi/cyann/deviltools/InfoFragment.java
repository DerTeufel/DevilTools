 /*   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package mobi.cyann.deviltools;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class InfoFragment extends PreferenceListFragment implements OnPreferenceClickListener {

    public InfoFragment() {
		super(R.layout.info);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Helper help = Helper.getInstance();
        help.readFile("/proc/version");
        Preference pref = findPreference("kernel_version");
        pref.setSummary(help.getOutResult().get(0));
        
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long totalMegs = mi.totalMem / 1048576L;

        pref = findPreference("system_memory");
        pref.setSummary(String.valueOf(totalMegs) + " MB");

		
	Preference about = findPreference(getString(R.string.key_about));
	about.setOnPreferenceClickListener(this);
	about.setTitle(getString(R.string.app_name)+ " " + getString(R.string.app_version));

	Preference donation = findPreference(getString(R.string.key_donation));
	donation.setOnPreferenceClickListener(this);
        
    }

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(getString(R.string.key_about))) {
			Intent browse = new Intent();
			browse.setAction(Intent.ACTION_VIEW);
			browse.setData(Uri.parse(getString(R.string.deviltools_thread_url)));
			startActivity(browse);
		}else if(preference.getKey().equals(getString(R.string.key_donation))) {
			Intent browse = new Intent();
			browse.setAction(Intent.ACTION_VIEW);
			browse.setData(Uri.parse(getString(R.string.deviltools_donation_url)));
			startActivity(browse);
		}
		return false;
	}

}
