package lostandfound.fndroid.com.lostandfound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import lostandfound.fndroid.com.lostandfound.beans.moweibo;

/**
 * Created by Administrator on 2016/6/7.
 */
public class ShitActivity extends AppCompatActivity{
	private static final String TAG = "ShitActivity";
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		moweibo mo = getIntent().getParcelableExtra("shit");
		Log.d(TAG, "onCreate: "+mo.toString());
	}
}
