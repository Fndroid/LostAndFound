package lostandfound.fndroid.com.lostandfound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class GetcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
	private ZXingScannerView mZXingScannerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mZXingScannerView = new ZXingScannerView(this);
		setContentView(mZXingScannerView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mZXingScannerView.setResultHandler(this);
		mZXingScannerView.startCamera();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mZXingScannerView.stopCamera();
	}

	@Override
	public void handleResult(Result result) {
		Intent data = new Intent();
		data.putExtra("code", result.getText());
		setResult(RESULT_OK, data);
		finish();
	}
}
