package lostandfound.fndroid.com.lostandfound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import lostandfound.fndroid.com.lostandfound.FoundModel.FoundActivityInterface;
import lostandfound.fndroid.com.lostandfound.FoundModel.FoundPresenter;
import lostandfound.fndroid.com.lostandfound.adapters.FoundViewHolder;
import lostandfound.fndroid.com.lostandfound.adapters.RecyclerAdapter;
import lostandfound.fndroid.com.lostandfound.beans.Found_list;
import lostandfound.fndroid.com.lostandfound.listeners.OnLastListener;
import lostandfound.fndroid.com.lostandfound.listeners.RecyclerOnScrollListener;
import lostandfound.fndroid.com.lostandfound.utils.Variable;
import lostandfound.fndroid.com.lostandfound.utils.decode;

/**
 * Created by Administrator on 2016/3/26.
 */
public class FoundActivity extends AppCompatActivity implements FoundActivityInterface,
		OnLastListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
	private static final String TAG = "FoundActivity";

	private FoundPresenter presenter;
	private RecyclerView rv;
	private RecyclerAdapter adapter;
	private List<Found_list> data;
	private RecyclerOnScrollListener onScrollListener;
	private SwipeRefreshLayout refreshlayout;
	private FloatingActionButton addFound;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regfound);
		Bmob.initialize(this, Variable.APPLICATIONID);
		initViews();
		presenter = new FoundPresenter(this);
		presenter.setSearchKey(getIntent().getStringExtra("searchKey"));
		presenter.fetchFounds(0, true);
	}

	private void initViews() {
		rv = (RecyclerView) findViewById(R.id.regfound_rv);
		data = new ArrayList<>();
		adapter = new RecyclerAdapter(this, data, -1, FoundViewHolder.class);
		rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		rv.setAdapter(adapter);
		onScrollListener = new RecyclerOnScrollListener();
		onScrollListener.setOnLastListener(this);
		rv.addOnScrollListener(onScrollListener);
		refreshlayout = (SwipeRefreshLayout) findViewById(R.id.regfound_srl);
		refreshlayout.post(new Runnable() {
			@Override
			public void run() {
				refreshlayout.setRefreshing(true);
			}
		});
		refreshlayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
		refreshlayout.setOnRefreshListener(this);
		addFound = (FloatingActionButton) findViewById(R.id.regfound_btn_add);
		addFound.setOnClickListener(this);
	}

	@Override
	public Context getContext() {
		return getApplicationContext();
	}

	@Override
	public void showError(int i, String msg) {
		Log.d(TAG, "showError() called with: " + "i = [" + i + "], msg = [" + msg + "]");
		Toast.makeText(FoundActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void dismissRefreshing() {
		refreshlayout.setRefreshing(false);
	}

	@Override
	public void showData(List list, boolean clearBefore) {
		Log.d(TAG, "showData() called with: " + "list = [" + list + "], clartDataFirst = [" +
				clearBefore + "]");
		if (clearBefore) {
			data.clear();
		}
		for (int i = 0; i < list.size(); i++) {
			if (!isContain(data, (Found_list) list.get(i))) {
				data.add((Found_list) list.get(i));
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void showRefreshing() {
		refreshlayout.post(new Runnable() {
			@Override
			public void run() {
				refreshlayout.setRefreshing(true);
			}
		});
	}

	@Override
	public void setMaxSizeForAdapter(int mMaxSize) {
		adapter.setmMaxSize(mMaxSize);
		Log.d(TAG, "setMaxSizeForAdapter() called with: " + "mMaxSize = [" + mMaxSize + "]");
	}

	@Override
	public void updateImage(String id, Bitmap bitmap) {
		Log.d(TAG, "updateImage: 图片更新成功");
		for (Found_list found : data) {
			if (found.getObjectId().equals(id)) {
				found.setImage(decode.bitmapToBase64(bitmap));
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}

	private void showPasswordDialog(int position) {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_password, null);
		EditText password = (EditText) view.findViewById(R.id.dialog_et_password);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		builder.setTitle("请输入您录入的验证密码");
		builder.setMessage("如果这不是您发布的消息，请选择\"取消\"");
		MyOnClickListener listener = new MyOnClickListener(password, position);
		builder.setPositiveButton("确认", listener);
		builder.setNegativeButton("取消", listener);
		builder.show();
	}

	@Override
	public void onClick(View v) {
		startActivity(new Intent(this, AddFoundActivity.class));
	}

	private class MyOnClickListener implements DialogInterface.OnClickListener {
		private EditText mPassword;
		private int mPosition;

		MyOnClickListener(EditText password, int position) {
			mPassword = password;
			mPosition = position;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume() called with: " + "");
//		presenter.fetchFounds(0,true);
	}

	@Override
	public void OnLast(int total) {
		presenter.fetchFounds(total, false);
	}

	@Override
	public void onRefresh() {
		presenter.fetchFounds(0, true);
	}

	private boolean isContain(List<Found_list> data, Found_list lost) {
		for (Found_list l : data) {
			if (l.getObjectId().equals(lost.getObjectId())) return true;
		}
		return false;
	}
}
