package lostandfound.fndroid.com.lostandfound.FoundModel;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import lostandfound.fndroid.com.lostandfound.LostModel.DataListener;
import lostandfound.fndroid.com.lostandfound.beans.Found_list;
import lostandfound.fndroid.com.lostandfound.utils.DiskLruCache;
import lostandfound.fndroid.com.lostandfound.utils.decode;

/**
 * Created by Administrator on 2016/6/1.
 */

public class FoundModelWithDiskCache implements FoundModelInterface {
	private static final String TAG = "FoundModelWithDiskCache";
	private int mMaxSize;
	private DiskLruCache mDiskLruCache;

	public FoundModelWithDiskCache(Context context) throws IOException {
		File fileDir = getDiskCacheDir(context, "bitmap");
		if (!fileDir.exists()){
			fileDir.mkdir();
		}
		mDiskLruCache = DiskLruCache.open(fileDir, getAppVersion(context), 1, 10 * 1024 *1024);
	}

	@Override
	public void downLoadData(final Context context, @Nullable String searchKey, boolean isEnd, int skip,
	                         final boolean downloadPictures, int limit, final DataListener listener) {
		Log.d(TAG, "downLoadData: 开始下载");
		if (isEnd) {
			return;
		}
		BmobQuery<Found_list> query = new BmobQuery<>();
		if (searchKey != null) {
			List<BmobQuery<Found_list>> queries = getQueries(searchKey);
			query.or(queries);
		}
		query.addWhereEqualTo("show", true);
		query.setSkip(skip);
		query.order("-createdAt");
		if (downloadPictures) {
			query.setLimit(limit);
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
				Log.d(TAG, "onSuccess: 条目请求完成");
				if (downloadPictures) {
					startDownloadImages(context, list, listener);
				}
			}

			@Override
			public void onError(int i, String s) {
				listener.onError(i, s);
			}
		});
	}

	private void startDownloadImages(Context context, List<Found_list> list, final DataListener listener) {
		for (final Found_list found : list) {
			if (!found.getHasimage()) {
				continue;
			}
			try {
				DiskLruCache.Snapshot snapshot = mDiskLruCache.get(found.getObjectId());
				if (snapshot != null) {
					Log.d(TAG, "startDownloadImages: 从缓存获取");
					Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
					listener.onUpdate(found.getObjectId(), bitmap);
				} else {
					Log.d(TAG, "startDownloadImages: 从网络获取");
					BmobQuery<Found_list> query = new BmobQuery<>();
					query.addQueryKeys("image");
					query.getObject(context, found.getObjectId(), new GetListener<Found_list>() {
						@Override
						public void onSuccess(Found_list found_list) {
							Bitmap bitmap = decode.base64ToBitmap(found_list.getImage());
							listener.onUpdate(found_list.getObjectId(), bitmap);
							try {
								DiskLruCache.Editor editor = mDiskLruCache.edit(found.getObjectId());
								if (editor != null){
									OutputStream os = editor.newOutputStream(0);
									if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)){
										editor.commit();
									}else{
										editor.abort();
									}
								}
								mDiskLruCache.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int i, String s) {
							Log.d(TAG, "onFailure: " + s);
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
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
		bmobQuery.addWhereEqualTo("show", true);
		bmobQuery.addQueryKeys("objectId");
		bmobQuery.findObjects(context, new FindListener<Found_list>() {
			@Override
			public void onSuccess(List<Found_list> list) {
				listener.onSuccess(list.size());
			}

			@Override
			public void onError(int i, String s) {
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

	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
				!Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName()
					, 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

}
