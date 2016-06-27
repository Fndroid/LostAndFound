package lostandfound.fndroid.com.lostandfound.FoundModel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import lostandfound.fndroid.com.lostandfound.LostModel.DataListener;
import lostandfound.fndroid.com.lostandfound.beans.Found_list;

/**
 * Created by Administrator on 2016/3/26.
 */
public class FoundModelImpl implements FoundModelInterface {
	private static final String TAG = "FoundModelImpl";
	private int mMaxSize = -1;

	@Override
	public void downLoadData(final Context context, @Nullable String searchKey, boolean isEnd, int
			skip, boolean downloadPictures, int limit, final DataListener listener) {
		if (isEnd) {
			return;
		}
		boolean autoDownload = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
				.getBoolean("autoDownload", true);
		BmobQuery<Found_list> query = new BmobQuery<>();
		if (searchKey != null) {
			List<BmobQuery<Found_list>> queries = getQueries(searchKey);
			query.or(queries);
		}
		query.setSkip(skip);
		query.order("-createdAt");
		if (autoDownload) {
			query.setLimit(limit);
			Log.d(TAG, "downloadData: 无wifi");
			query.addQueryKeys("objectId,title,description,time,phone,place,password," +
					"createdAt,updatedAt,hasimage");
		} else {
			query.setLimit(limit / 2);
		}
		query.findObjects(context, new FindListener<Found_list>() {
			@Override
			public void onSuccess(List<Found_list> list) {
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
		BmobQuery<Found_list> bmobQuery = new BmobQuery<>();
		if (searchKey != null) {
			List<BmobQuery<Found_list>> queries = getQueries(searchKey);
			bmobQuery.or(queries);
		}
		bmobQuery.count(context, Found_list.class, new CountListener() {
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

	private ArrayList<BmobQuery<Found_list>> getQueries(String key) {
		ArrayList<BmobQuery<Found_list>> queries = new ArrayList<>();
		queries.add(new BmobQuery<Found_list>().addWhereContains("title", key));
		queries.add(new BmobQuery<Found_list>().addWhereContains("description", key));
		return queries;
	}
}
