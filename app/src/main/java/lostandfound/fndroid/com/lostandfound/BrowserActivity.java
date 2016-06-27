package lostandfound.fndroid.com.lostandfound;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;

public class BrowserActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
	private WebView mWebView;
	private SeekBar mSeekBar;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		mSharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
		mWebView = (WebView) findViewById(R.id.browser_webView);
		mSeekBar = (SeekBar) findViewById(R.id.browser_seekBar);
		mSeekBar.setOnSeekBarChangeListener(this);
		mWebView.loadUrl("http://www.gdin.edu.cn/updateindex/gonggaolist.asp");
		int broserScale = mSharedPreferences.getInt("browserScale",0);
		mWebView.setInitialScale(broserScale+80);
		mSeekBar.setProgress(broserScale);
		mWebView.setWebViewClient(new WebViewClient());
	}

	public void back(View view) {
		mWebView.goBack();
	}

	public void forward(View view) {
		mWebView.goForward();
	}

	public void exit(View view) {
		this.finish();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		mWebView.setInitialScale(progress+80);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt("browserScale", seekBar.getProgress());
		editor.apply();
	}
}
