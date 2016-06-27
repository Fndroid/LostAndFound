package lostandfound.fndroid.com.lostandfound.LostModel;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Administrator on 2016/3/21.
 */
public interface DataListener {
	void onComplete(List list);
	void onUpdate(String id, Bitmap bitmap);
	void onError(int err,String msg);
}
