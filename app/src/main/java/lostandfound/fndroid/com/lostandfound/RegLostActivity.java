package lostandfound.fndroid.com.lostandfound;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import lostandfound.fndroid.com.lostandfound.adapters.LostViewHolder;
import lostandfound.fndroid.com.lostandfound.adapters.RecyclerAdapter;
import lostandfound.fndroid.com.lostandfound.beans.Lost_list;
import lostandfound.fndroid.com.lostandfound.listeners.OnLastListener;
import lostandfound.fndroid.com.lostandfound.listeners.RecyclerOnScrollListener;
import lostandfound.fndroid.com.lostandfound.utils.NetStateHelper;
import lostandfound.fndroid.com.lostandfound.utils.Variable;
import lostandfound.fndroid.com.lostandfound.utils.error;

/**
 * Created by Administrator on 2016/3/12.
 */
public class RegLostActivity extends AppCompatActivity implements View.OnClickListener,
		RecyclerAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnLastListener {
	private static final String TAG = "RegLostActivity";
	private static final int DATA_LOAD_FINISH = 1;
	private static final int LISTCOUNT_LIMIT = 20;
	private static final int DATA_LOAD_FROM_CACHE = 2;
	private boolean isEnd = false;
	private RecyclerView rv;
	private RecyclerOnScrollListener mOnScrollListener;
	private FloatingActionButton add;
	private SwipeRefreshLayout refreshlayout;

	private LinearLayoutManager linerlayoutmanager;

	private int maxSize = -1;

	private List<Lost_list> data;
	private RecyclerAdapter adapter;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case DATA_LOAD_FINISH:
					adapter.notifyDataSetChanged();
					refreshlayout.setRefreshing(false);
					if (msg.obj != null) {
						Snackbar.make(rv, msg.obj.toString(), Snackbar.LENGTH_LONG).show();
					}
					break;
				case DATA_LOAD_FROM_CACHE:
					refreshlayout.setRefreshing(false);
					break;
			}
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reglost);
		Bmob.initialize(this, Variable.APPLICATIONID);
		getMaxSize();
		initViews();
//		getMaxSize();
		if (!new NetStateHelper(this).isWifiConnected()) {
			Toast.makeText(RegLostActivity.this, "当前无连接WIFI，默认不下载图片", Toast.LENGTH_SHORT).show();
		}
		downloadData(0, new NetStateHelper(this).isWifiConnected());
	}


	private void initViews() {
		refreshlayout = (SwipeRefreshLayout) findViewById(R.id.reglost_srl);
		refreshlayout.post(new Runnable() {
			@Override
			public void run() {
				refreshlayout.setRefreshing(true);
			}
		});
		refreshlayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
		refreshlayout.setOnRefreshListener(this);
		rv = (RecyclerView) findViewById(R.id.reglost_rv);
		mOnScrollListener = new RecyclerOnScrollListener();
		mOnScrollListener.setOnLastListener(this);
		rv.addOnScrollListener(mOnScrollListener);
		linerlayoutmanager = new LinearLayoutManager(this);
		rv.setLayoutManager(linerlayoutmanager);
		data = new ArrayList<>();
		adapter = new RecyclerAdapter(this, data, maxSize, LostViewHolder.class);
		rv.setAdapter(adapter);
		adapter.setOnItemClickListener(this);
		add = (FloatingActionButton) findViewById(R.id.reglost_btn_add);
		add.setOnClickListener(this);

	}

	private void downloadData(final int skip, Boolean isWifiConnected) {
		if (maxSize == data.size()) return;
		BmobQuery<Lost_list> query = new BmobQuery<>();
		query.setSkip(skip);
		query.order("-createdAt");
		if (!isWifiConnected) {
			query.setLimit(LISTCOUNT_LIMIT);
			Log.d(TAG, "downloadData: 无wifi");
			query.addQueryKeys("objectId,title,description,time,phone,place,password," +
					"createdAt,updatedAt");
		} else {
			query.setLimit(LISTCOUNT_LIMIT / 2);
		}
		query.findObjects(this, new FindListener<Lost_list>() {
			@Override
			public void onSuccess(final List<Lost_list> list) {
				if (skip == 0) data.clear();
				for (int i = 0; i < list.size(); i++) {
					if (!isContain(list.get(i))) {
						data.add(list.get(i));
					}
				}
				Message msg = new Message();
				msg.what = DATA_LOAD_FINISH;
				handler.sendMessage(msg);
				Log.d(TAG, "onSuccess: ");
			}

			@Override
			public void onError(int i, String s) {
				data.clear();
				Message msg = new Message();
				msg.what = DATA_LOAD_FINISH;
				msg.obj = error.getErrorMsg(i);
				handler.sendMessage(msg);
			}
		});
	}

	private void getMaxSize() {
		BmobQuery<Lost_list> bmobQuery = new BmobQuery<>();
		bmobQuery.count(this, Lost_list.class, new CountListener() {
			@Override
			public void onSuccess(int i) {
				maxSize = i;
				adapter.setmMaxSize(maxSize);
			}

			@Override
			public void onFailure(int i, String s) {
			}
		});
	}

	private boolean isContain(Lost_list lost) {
		for (Lost_list l : data) {
			if (l.getObjectId().equals(lost.getObjectId())) return true;
		}
		return false;
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
		getMaxSize();
		downloadData(0, new NetStateHelper(this).isWifiConnected());
	}

	@Override
	public void OnLast(int total) {
		downloadData(total, new NetStateHelper(this).isWifiConnected());
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
}
