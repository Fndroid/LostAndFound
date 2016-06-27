package lostandfound.fndroid.com.lostandfound.LostModel;

import android.content.Context;
import android.support.annotation.Nullable;

import lostandfound.fndroid.com.lostandfound.FoundModel.OnGetMaxSizeListener;

/**
 * Created by Administrator on 2016/3/21.
 */
public interface LostModelInterface {
	void downLoadData(Context context, @Nullable String searchKey, boolean isEnd, final int skip, boolean isWifiConnected, int
			limit, final DataListener listener);

	int getMaxSize(Context context, @Nullable String searchKey, final LostModelImpl.OnGetMaxSizeListener listener);
}
