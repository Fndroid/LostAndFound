package lostandfound.fndroid.com.lostandfound;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by Administrator on 2016/4/3.
 */
public class SettingActivity extends AppCompatActivity implements CompoundButton
		.OnCheckedChangeListener {
	private Switch autoDownload, autoNotify;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initViews();
	}

	private void initViews() {
		autoDownload = (Switch) findViewById(R.id.setting_switch_autoDownload);
		autoNotify = (Switch) findViewById(R.id.setting_switch_notifyNewLost);
		sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
		autoDownload.setChecked(sharedPreferences.getBoolean("autoDownload", false));
		autoNotify.setChecked(sharedPreferences.getBoolean("autoNotify", false));
		autoDownload.setOnCheckedChangeListener(this);
		autoNotify.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		editor = sharedPreferences.edit();
		switch (buttonView.getId()) {
			case R.id.setting_switch_autoDownload:
				editor.putBoolean("autoDownload", isChecked);
				break;
			case R.id.setting_switch_notifyNewLost:
				editor.putBoolean("autoNotify", isChecked);
				break;
		} editor.apply();
	}
}
