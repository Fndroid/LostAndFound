package lostandfound.fndroid.com.lostandfound.LostModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import lostandfound.fndroid.com.lostandfound.utils.NetStateHelper;

/**
 * Created by Administrator on 2016/3/21.
 */
public class LostPresenter {
	private static final String TAG = "LostPresenter";

	private static final int LIMIT = 20;
	private LostActivityInterface mLostActivity;
	private LostModelInterface mLostModel = new LostModelImpl();
	private Context mContext;
	private List data;
	private String searchKey;

	private int maxSize = -1;

	public LostPresenter(LostActivityInterface lostActivityInterface) {
		mLostActivity = lostActivityInterface;
		mContext = mLostActivity.getContext();
	}

	public void fetchLosts(final int skip, boolean isRefresh) {
		if (isRefresh) {
			maxSize = -1;
			mLostActivity.showRefreshing();
			mLostModel.getMaxSize(mContext, searchKey, new LostModelImpl.OnGetMaxSizeListener() {
				@Override
				public void onSuccess(int i) {
					maxSize = i;
					if (maxSize == 0) {
						mLostActivity.dismissRefreshing();
						Toast.makeText(mLostActivity.getContext(), "没有找到相关记录哦", Toast
								.LENGTH_SHORT).show();
						return;
					}
					mLostActivity.setMaxSizeForAdapter(maxSize);
					downLoadData(skip, maxSize <= skip, true);
				}

				@Override
				public void onFailure(int i, String s) {
					mLostActivity.showError(i, s);
				}
			});
		} else {
			downLoadData(skip, maxSize <= skip, false);
		}

	}

	private void downLoadData(int skip, boolean isEnd, final boolean clartDataFirst) {
		Log.d(TAG, "downLoadData() called with: " + "skip = [" + skip + "], isEnd = [" + isEnd +
				"], clartData = [" + clartDataFirst + "]");
		mLostModel.downLoadData(mContext, searchKey, isEnd, skip, new NetStateHelper(mContext)
				.isWifiConnected(), LIMIT, new DataListener() {
			@Override
			public void onComplete(List list) {
				mLostActivity.dismissRefreshing();
				mLostActivity.showData(list, clartDataFirst);
			}

			@Override
			public void onUpdate(String id, Bitmap bitmap) {

			}

			@Override
			public void onError(int err, String msg) {
				mLostActivity.dismissRefreshing();
				mLostActivity.showError(err, msg);
			}
		});
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
}
