package lostandfound.fndroid.com.lostandfound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import lostandfound.fndroid.com.lostandfound.LostModel.LostActivityInterface;
import lostandfound.fndroid.com.lostandfound.LostModel.LostPresenter;
import lostandfound.fndroid.com.lostandfound.adapters.LostViewHolder;
import lostandfound.fndroid.com.lostandfound.adapters.RecyclerAdapter;
import lostandfound.fndroid.com.lostandfound.beans.Lost_list;
import lostandfound.fndroid.com.lostandfound.listeners.OnLastListener;
import lostandfound.fndroid.com.lostandfound.listeners.RecyclerOnScrollListener;
import lostandfound.fndroid.com.lostandfound.utils.Variable;

/**
 * Created by Administrator on 2016/3/21.
 */
public class LostActivity extends AppCompatActivity implements LostActivityInterface, View
		.OnClickListener, OnLastListener, RecyclerAdapter.OnItemClickListener, SwipeRefreshLayout
		.OnRefreshListener {
	private static final String TAG = "LostActivity";

	private LostPresenter presenter;
	private RecyclerView rv;
	private FloatingActionButton add;
	private SwipeRefreshLayout refreshlayout;
	private RecyclerOnScrollListener onScrollListener;
	private RecyclerAdapter adapter;
	private List<Lost_list> lost = new ArrayList<>();
	private SwipeRefreshLayout refreshLayout;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reglost);
		Bmob.initialize(this, Variable.APPLICATIONID);
//		Log.d(TAG, "onCreate: " + getIntent().getStringExtra("searchKey"));
		initViews();
		presenter = new LostPresenter(this);
		presenter.setSearchKey(getIntent().getStringExtra("searchKey"));
		presenter.fetchLosts(0, true);
	}

	private void initViews() {
		rv = (RecyclerView) findViewById(R.id.reglost_rv);
		adapter = new RecyclerAdapter(this, lost, -1, LostViewHolder.class);
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.setAdapter(adapter);
		add = (FloatingActionButton) findViewById(R.id.reglost_btn_add);
		add.setOnClickListener(this);
		onScrollListener = new RecyclerOnScrollListener();
		onScrollListener.setOnLastListener(this);
		rv.addOnScrollListener(onScrollListener);
		adapter.setOnItemClickListener(this);
		refreshlayout = (SwipeRefreshLayout) findViewById(R.id.reglost_srl);
		refreshlayout.post(new Runnable() {
			@Override
			public void run() {
				refreshlayout.setRefreshing(true);
			}
		});
		refreshlayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
		refreshlayout.setOnRefreshListener(this);
	}

	@Override
	public void showData(List list, boolean clearFirst) {
		if (clearFirst) {
			lost.clear();
		}
		int x = lost.size();
		for (int i = 0; i < list.size(); i++) {
			if (!isContain(lost, (Lost_list) list.get(i))) {
				lost.add((Lost_list) list.get(i));
			}
		}
		Log.d(TAG, "showData: grow:" + (lost.size() - x));
		adapter.notifyDataSetChanged();
	}

	@Override
	public void notifyList(List list) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void showError(int i, String msg) {
		Toast.makeText(LostActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public Context getContext() {
		return getApplicationContext();
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
	public void dismissRefreshing() {
		refreshlayout.setRefreshing(false);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.reglost_btn_add:
				intent = new Intent(this, AddLostActivity.class);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void OnLast(int total) {
		Log.d(TAG, "OnLast() called with: " + "total = [" + total + "]");
		presenter.fetchLosts(total, false);
	}

	@Override
	public void OnItemClick(int position) {
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
	public void onRefresh() {
		presenter.fetchLosts(0, true);
	}

	private class MyOnClickListener implements DialogInterface.OnClickListener {
		private EditText mPassword;
		private int mPosition;

		public MyOnClickListener(EditText password, int position) {
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
	public void setMaxSizeForAdapter(int maxSize) {
		adapter.setmMaxSize(maxSize);
	}

	private boolean isContain(List<Lost_list> data, Lost_list lost) {
		for (Lost_list l : data) {
			if (l.getObjectId().equals(lost.getObjectId())) return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume() called");
	}
}
