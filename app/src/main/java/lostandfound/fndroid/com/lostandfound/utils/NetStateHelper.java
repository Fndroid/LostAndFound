package lostandfound.fndroid.com.lostandfound.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by Administrator on 2016/3/20.
 */
public class NetStateHelper {
	private static final String TAG = "NetStateHelper";
	private Context mContext;

	public NetStateHelper(Context mContext) {
		this.mContext = mContext;
	}

	public boolean isWifiConnected() {
		WifiManager manager = (WifiManager) mContext.getSystemService(Context
				.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		if (info.getSupplicantState() == SupplicantState.COMPLETED) {
			return true;
		}else{
			return false;
		}
	}
}
