package lostandfound.fndroid.com.lostandfound.FoundModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import lostandfound.fndroid.com.lostandfound.LostModel.DataListener;

/**
 * Created by Administrator on 2016/3/26.
 */
public class FoundPresenter {
	private static final String TAG = "FoundPresenter";
	private static final int LIMIT = 20;
	private Context mContext;
	private FoundActivityInterface mFoundActivity;
	private FoundModelInterface mFoundModel;
	private int mMaxSize = -1;
	private String searchKey;
	private SharedPreferences mSharedPreferences;

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public FoundPresenter(FoundActivityInterface foundActivity) {
		mFoundActivity = foundActivity;
		mContext = mFoundActivity.getContext();
		try {
			mFoundModel = new FoundModelWithDiskCache(mContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fetchFounds(final int skip, boolean isRefresh) {
		Log.d("FoundActivity", "fetchFounds: 开始查找条目");
//		downLoadData(skip, false, true);
		if (isRefresh) {
			mMaxSize = -1;
			mFoundActivity.showRefreshing();
			mFoundModel.getMaxSize(mContext, searchKey, new OnGetMaxSizeListener() {
				@Override
				public void onSuccess(int i) {
					Log.d("FoundActivity", "onSuccess: 查找条目数成功");
					mMaxSize = i;
					if (mMaxSize == 0) {
						mFoundActivity.dismissRefreshing();
						Toast.makeText(mFoundActivity.getContext(), "没有找到相关记录哦", Toast
								.LENGTH_SHORT).show();
						return;
					}
					mFoundActivity.setMaxSizeForAdapter(mMaxSize);
					downLoadData(skip, mMaxSize <= skip, true);
				}

				@Override
				public void onFailure(int i, String s) {
					mFoundActivity.showError(i, s);
				}
			});
		} else {
			downLoadData(skip, mMaxSize <= skip, false);
		}

	}

	private void downLoadData(int skip, boolean isEnd, final boolean clartDataFirst) {
		Log.d("FoundActivity", "downLoadData: 开始下载数据");
		mSharedPreferences = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
		mFoundModel.downLoadData(mContext, searchKey, isEnd, skip, mSharedPreferences.getBoolean
				("autoDownload", false), LIMIT, new DataListener() {
			@Override
			public void onComplete(List list) {
				Log.d("FoundActivity", "onComplete: 数据下载成功");
				mFoundActivity.dismissRefreshing();
				mFoundActivity.showData(list, clartDataFirst);
			}

			@Override
			public void onUpdate(String id, Bitmap bitmap) {
				mFoundActivity.updateImage(id, bitmap);
			}

			@Override
			public void onError(int err, String msg) {
				mFoundActivity.dismissRefreshing();
				mFoundActivity.showError(err, msg);
			}
		});
	}
}
