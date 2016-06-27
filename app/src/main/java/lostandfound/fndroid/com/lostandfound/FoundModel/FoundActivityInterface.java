package lostandfound.fndroid.com.lostandfound.FoundModel;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Administrator on 2016/3/26.
 */
public interface FoundActivityInterface {
	Context getContext();
	void showError(int i,String msg);

	void dismissRefreshing();

	void showData(List list, boolean clartDataFirst);

	void showRefreshing();

	void setMaxSizeForAdapter(int mMaxSize);

	void updateImage(String id, Bitmap bitmap);
}
