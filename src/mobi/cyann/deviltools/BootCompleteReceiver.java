package mobi.cyann.deviltools;

import mobi.cyann.deviltools.services.ObserverService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author arif
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "DevilTools.BootCompleteReceiver";
	private static final String FILE = "/sys/class/misc/backlightnotification/enabled";
    
	private static boolean BLNisSupported() {
        return Utils.fileExists(FILE);
    	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "starting service");
		
		// start OnBootCompleteService (restore setting)
		context.startService(new Intent(context, OnBootCompleteService.class));
		
		// start our ObserverService (monitor missed call)
		if(BLNisSupported())
		ObserverService.startService(context, false);
		else
		ObserverService.stopService(context);
		
	}
}
