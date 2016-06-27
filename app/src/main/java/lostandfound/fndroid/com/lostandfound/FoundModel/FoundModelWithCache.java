package lostandfound.fndroid.com.lostandfound.FoundModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import lostandfound.fndroid.com.lostandfound.LostModel.DataListener;
import lostandfound.fndroid.com.lostandfound.beans.Found_list;
import lostandfound.fndroid.com.lostandfound.utils.decode;

/**
 * Created by Administrator on 2016/4/19.
 */
public class FoundModelWithCache implements FoundModelInterface {
	private static final String TAG = "FoundModelWithCache";

	private int mMaxSize;
	private LruCache<String, Bitmap> mLruCache = new StringBitmapLruCache();

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
			query.addQueryKeys("objectId,title,description,time,phone,place,password," +
					"createdAt,updatedAt,hasimage");
			query.setLimit(limit / 2);
		}
		query.findObjects(context, new FindListener<Found_list>() {
			@Override
			public void onSuccess(List<Found_list> list) {
				listener.onComplete(list);
				startDownloadImages(context, list, listener);
			}

			@Override
			public void onError(int i, String s) {
				listener.onError(i, s);
			}
		});
	}

	private void startDownloadImages(Context context, List<Found_list> list, final DataListener
			listener) {
		for (final Found_list found : list) {
			if (!found.getHasimage()) {
				continue;
			}
			if (mLruCache.get(found.getObjectId()) != null) {
				Log.d(TAG, "startDownloadImages: 从缓存中获取图片");
				listener.onUpdate(found.getObjectId(), mLruCache.get(found.getObjectId()));
			} else {
				Log.d(TAG, "startDownloadImages: 从网络加载图片");
				BmobQuery<Found_list> query = new BmobQuery<>();
				query.addQueryKeys("image");
				query.getObject(context, found.getObjectId(), new GetListener<Found_list>() {
					@Override
					public void onSuccess(Found_list found_list) {
						Bitmap bitmap = decode.base64ToBitmap(found_list.getImage());
						listener.onUpdate(found_list.getObjectId(), bitmap);
						mLruCache.put(found_list.getObjectId(), bitmap);
					}

					@Override
					public void onFailure(int i, String s) {
						Log.d(TAG, "onFailure: " + s);
					}
				});
			}
		}
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

	private static class StringBitmapLruCache extends LruCache<String, Bitmap> {
		public StringBitmapLruCache() {
			// 构造方法传入当前应用可用最大内存的八分之一
			super((int) (Runtime.getRuntime().maxMemory() / 1024 / 8));
		}

		@Override
		// 重写sizeOf方法，并计算返回每个Bitmap对象占用的内存
		protected int sizeOf(String key, Bitmap value) {
			return value.getByteCount() / 1024;
		}

		@Override
		// 当缓存被移除时调用，第一个参数是表明缓存移除的原因，true表示被LruCache移除，false表示被主动remove移除，可不重写
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap
				newValue) {
			super.entryRemoved(evicted, key, oldValue, newValue);
		}

		@Override
		// 当get方法获取不到缓存的时候调用，如果需要创建自定义默认缓存，可以在这里添加逻辑，可不重写
		protected Bitmap create(String key) {
			return super.create(key);
		}
	}
}
