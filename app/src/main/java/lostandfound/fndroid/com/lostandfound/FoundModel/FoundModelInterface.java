package lostandfound.fndroid.com.lostandfound.FoundModel;

import android.content.Context;
import android.support.annotation.Nullable;

import lostandfound.fndroid.com.lostandfound.LostModel.DataListener;

/**
 * Created by Administrator on 2016/3/26.
 */
public interface FoundModelInterface {
	void downLoadData(Context context, @Nullable String searchKey, boolean isEnd, final int skip,
	                  boolean downloadPictures, int limit, final DataListener listener);

	int getMaxSize(Context context, @Nullable String searchKey, final OnGetMaxSizeListener
			listener);
}
