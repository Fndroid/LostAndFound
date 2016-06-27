package lostandfound.fndroid.com.lostandfound.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import cn.bmob.push.PushConstants;
import lostandfound.fndroid.com.lostandfound.ActivitiesWebView;
import lostandfound.fndroid.com.lostandfound.R;
import lostandfound.fndroid.com.lostandfound.beans.PushBean;

/**
 * Created by Administrator on 2016/3/26.
 */
public class PushMessageReceiver extends BroadcastReceiver {
	private static final String TAG = "PushMessageReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive: 推送消息");
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			Log.d(TAG, "推送消息为: " + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			Gson gson = new Gson();
			PushBean bean = gson.fromJson(intent.getStringExtra(PushConstants
					.EXTRA_PUSH_MESSAGE_STRING), PushBean.class);
			Intent i = new Intent(context, ActivitiesWebView.class);
			Log.d(TAG, "onReceive: " + bean.getUrl());
			i.putExtra("loadUrl", bean.getUrl());
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent
					.FLAG_UPDATE_CURRENT);
			builder.setContentTitle(bean.getTitle().equals("") ? "新消息" : bean.getTitle())
					.setContentText("".equals(bean.getText()) ? "点击我去查看吧" : bean.getText())
					.setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent)
					.setAutoCancel(true);
			NotificationManager manager = (NotificationManager) context.getSystemService(Context
					.NOTIFICATION_SERVICE);
			manager.notify(0, builder.build());
		}
	}
}
