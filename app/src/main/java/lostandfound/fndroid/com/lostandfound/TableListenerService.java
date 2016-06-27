package lostandfound.fndroid.com.lostandfound;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by Administrator on 2016/3/28.
 */
public class TableListenerService extends Service {
	private static final String TAG = "TableListenerService";
	private BmobRealTimeData rtd;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startBmobTimeData();
	}

	private void startBmobTimeData() {
		rtd = new BmobRealTimeData();
		rtd.start(this, new ValueEventListener() {
			@Override
			public void onConnectCompleted() {
				if (rtd.isConnected()) {
					rtd.subTableUpdate("Found_list");
				}
			}

			@Override
			public void onDataChange(JSONObject jsonObject) {
				Log.d(TAG, "onDataChange: " + jsonObject.toString());
				try {
					JSONObject data = jsonObject.getJSONObject("data");
					if (data.optBoolean("show")){
						buildNotification();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void buildNotification() {
		SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
		boolean isNotify = preferences.getBoolean("autoNotify", false);
		if (!isNotify) {
			return;
		}
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		Intent i = new Intent(this, FoundActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent
				.FLAG_UPDATE_CURRENT);
		builder.setContentTitle("有新的事物提交啦，快去看看是不是你的呀").setContentText("点击我去查看吧").setContentIntent(pi)
				.setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher);
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(0, builder.build());
	}


}
