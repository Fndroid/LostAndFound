package lostandfound.fndroid.com.lostandfound.LostModel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import lostandfound.fndroid.com.lostandfound.beans.Lost_list;

/**
 * Created by Administrator on 2016/3/21.
 */
public class LostModelImpl implements LostModelInterface {
	private static final String TAG = "LostModelImpl";
	private int mMaxSize = -1;

	@Override
	public void downLoadData(Context context, @Nullable String searchKey, boolean isEnd, final int
			skip, boolean isWifiConnected, int limit, final DataListener listener) {
		if (isEnd) {
			return;
		}
		boolean autoDownload = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
				.getBoolean("autoDownload", true);
		BmobQuery<Lost_list> query = new BmobQuery<>();
		if (searchKey != null) {
			List<BmobQuery<Lost_list>> bmobQueries = getQuers(searchKey);
			query.or(bmobQueries);
		}
		query.addWhereEqualTo("show", true);
		query.setSkip(skip);
		query.order("-createdAt");
		if (autoDownload && !isWifiConnected) {
			query.setLimit(limit);
			Log.d(TAG, "downloadData: 不加载图片");
			query.addQueryKeys("objectId,title,description,time,phone,place,password," +
					"createdAt,updatedAt");
		} else {
			query.setLimit(limit / 2);
		}
		query.findObjects(context, new FindListener<Lost_list>() {
			@Override
			public void onSuccess(List<Lost_list> list) {
				listener.onComplete(list);
			}

			@Override
			public void onError(int i, String s) {
				listener.onError(i, s);
			}
		});
	}

	@Override
	public int getMaxSize(Context context, @Nullable String searchKey, final OnGetMaxSizeListener
			listener) {
		BmobQuery<Lost_list> bmobQuery = new BmobQuery<>();
		if (searchKey != null) {
			List<BmobQuery<Lost_list>> queries = getQuers(searchKey);
			bmobQuery.or(queries);
		}
		bmobQuery.addWhereEqualTo("show", true);
		bmobQuery.count(context, Lost_list.class, new CountListener() {
			@Override
			public void onSuccess(int i) {
				listener.onSuccess(i);
			}

			@Override
			public void onFailure(int i, String s) {
				listener.onFailure(i, s);
			}
		});
		return mMaxSize;
	}

	public interface OnGetMaxSizeListener {
		void onSuccess(int i);

		void onFailure(int i, String s);
	}

	private List<BmobQuery<Lost_list>> getQuers(String key) {
		ArrayList<BmobQuery<Lost_list>> queries = new ArrayList<>();
		queries.add(new BmobQuery<Lost_list>().addWhereContains("title", key));
		queries.add(new BmobQuery<Lost_list>().addWhereContains("description", key));
		queries.add(new BmobQuery<Lost_list>().addWhereContains("time", key));
		queries.add(new BmobQuery<Lost_list>().addWhereContains("place", key));
		return queries;
	}

}
