package lostandfound.fndroid.com.lostandfound.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/3/14.
 */
public class RecyclerOnScrollListener extends RecyclerView.OnScrollListener {
	private OnLastListener mLastListener;
	private static final String TAG = "RecyclerOnScrollListener";

	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		super.onScrollStateChanged(recyclerView, newState);
		if (newState == RecyclerView.SCROLL_STATE_IDLE) {
			LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
					.getLayoutManager();
			int itemCount = layoutManager.getItemCount();
			int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
			if (lastPosition == itemCount - 1) {
				mLastListener.OnLast(itemCount-1);
			}
		}
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);

	}

	public void setOnLastListener(OnLastListener listener) {
		mLastListener = listener;
	}
}
