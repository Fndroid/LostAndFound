package lostandfound.fndroid.com.lostandfound.utils;

import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/3/19.
 */
public class BitmapHelper {
	private static final String TAG = "BitmapHelper";

	public int getInSampleSize(InputStream is, Uri uri, int reqHeight, int reqWidth) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		int mHeigth, mWidth;
		BitmapFactory.decodeStream(is, null, options);
		mHeigth = options.outHeight;
		mWidth = options.outWidth;
		int inSampleSize = 1;
		while ((mHeigth / inSampleSize) > reqHeight && (mWidth / inSampleSize) > reqWidth) {
			inSampleSize *= 2;
		}
		return inSampleSize;
	}
}
