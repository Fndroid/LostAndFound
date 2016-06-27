package lostandfound.fndroid.com.lostandfound;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import lostandfound.fndroid.com.lostandfound.utils.Variable;

/**
 * Created by Administrator on 2016/3/26.
 */
public class HomeActivity extends AppCompatActivity implements NavigationView
		.OnNavigationItemSelectedListener, View.OnClickListener {
	private static final String TAG = "HomeActivity";
	private NavigationView navigationView;
	private DrawerLayout drawerLayout;
	private View actionBarView;
	private ImageView switcher;
	private Button he;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Bmob.initialize(this, Variable.APPLICATIONID);
		BmobInstallation.getCurrentInstallation(this).save();
		BmobPush.startWork(this);
		initViews();
		Log.d(TAG, "onCreate: " + ViewConfiguration.get(this).getScaledDoubleTapSlop());
		startService(new Intent(HomeActivity.this, TableListenerService.class));
	}

	private void initViews() {
		navigationView = (NavigationView) findViewById(R.id.home_navigationView);
		drawerLayout = (DrawerLayout) findViewById(R.id.home_drawerLayout);
		navigationView.setNavigationItemSelectedListener(this);
		ActionBar actionBar = getSupportActionBar();
		actionBarView = getLayoutInflater().inflate(R.layout.activity_home_menu, null);
		actionBar.setCustomView(actionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		switcher = (ImageView) actionBarView.findViewById(R.id.activity_home_menu_switch);
		switcher.setOnClickListener(this);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.home_navigationView_lost:
				intent = new Intent(HomeActivity.this, LostActivity.class);
				startActivity(intent);
				break;
			case R.id.home_navigationView_found:
				intent = new Intent(this, FoundActivity.class);
				startActivity(intent);
				break;
			case R.id.home_navigationView_search:
				showSearchDialog();
				break;
			case R.id.home_navigationView_support:
				intent = new Intent(this, SupportActivity.class);
				startActivity(intent);
				break;
			case R.id.home_navigationView_addLost:
				intent = new Intent(this, AddLostActivity.class);
				startActivity(intent);
				break;
			case R.id.home_navigationView_addFound:
				intent = new Intent(this, AddFoundActivity.class);
				startActivity(intent);
				break;
			case R.id.home_navigationView_settings:
				intent = new Intent(this, SettingActivity.class);
				startActivity(intent);

				break;
			case R.id.home_navigationView_feedback:
				intent = new Intent(this, FeedbackActivity.class);
				startActivity(intent);
				break;
			case R.id.home_navigationView_exit:
				finish();
				break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (navigationView.isShown()) {
			drawerLayout.closeDrawers();
		} else {
			drawerLayout.openDrawer(navigationView);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showSearchDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null);
		EditText searchKey = (EditText) view.findViewById(R.id.dialog_et_search);
		RadioButton lost = (RadioButton) view.findViewById(R.id.dialog_rb_lost);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		builder.setTitle("搜索");
		builder.setMessage("请输入要搜索的关键字");
		MyOnClickListener listener = new MyOnClickListener(searchKey, lost);
		builder.setPositiveButton("确认", listener);
		builder.setNegativeButton("取消", listener);
		builder.show();
	}

	public void news(View view) {
		startActivity(new Intent(this, BrowserActivity.class));
	}

	private class MyOnClickListener implements DialogInterface.OnClickListener {
		private EditText key;
		private RadioButton mLost;

		public MyOnClickListener(EditText searchKey, RadioButton lost) {
			key = searchKey;
			mLost = lost;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					if (mLost.isChecked()) {
						Intent intent = new Intent(HomeActivity.this, LostActivity.class);
						intent.putExtra("searchKey", key.getText().toString().trim());
						startActivity(intent);
					} else {
						Intent intent = new Intent(HomeActivity.this, FoundActivity.class);
						intent.putExtra("searchKey", key.getText().toString().trim());
						startActivity(intent);
					}
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
