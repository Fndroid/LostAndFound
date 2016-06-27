package lostandfound.fndroid.com.lostandfound.LostModel;

import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/3/21.
 * as View
 */
public interface LostActivityInterface {
	void showData(List list, boolean clearFirst);

	void notifyList(List list);

	void showError(int i, String msg);

	Context getContext();

	void showRefreshing();

	void dismissRefreshing();

	void setMaxSizeForAdapter(int maxSize);
}
