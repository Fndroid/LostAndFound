package lostandfound.fndroid.com.lostandfound;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import lostandfound.fndroid.com.lostandfound.R;

/**
 * Created by Administrator on 2016/3/26.
 */
public class ActivitiesWebView extends AppCompatActivity {
	private static final String TAG = "ActivitiesWebView";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activities);
		String url = getIntent().getStringExtra("loadUrl");
		WebView wv = (WebView) findViewById(R.id.webview);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebViewClient());
		Log.d(TAG, "onCreate: " + url);
		wv.loadUrl(url);
	}
}
