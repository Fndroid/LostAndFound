package lostandfound.fndroid.com.lostandfound.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobObject;
import lostandfound.fndroid.com.lostandfound.ImageActivity;
import lostandfound.fndroid.com.lostandfound.R;
import lostandfound.fndroid.com.lostandfound.beans.Found_list;
import lostandfound.fndroid.com.lostandfound.beans.Lost_list;
import lostandfound.fndroid.com.lostandfound.utils.decode;

/**
 * Created by Administrator on 2016/3/12.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {
	private static final String TAG = "RecyclerAdapter";
	private static final int LOAD_MORE_VIEW = -1;
	private static final int END_VIEW = 0;
	private static final int EMPTY_VIEW = 2;
	private static final int NO_MORE_VIEW = 4;
	private Context mContext;
	private List<? extends BmobObject> mList;
	private OnItemClickListener mOnItemClickListener;

	private int mMaxSize;
	private Class<? extends RecyclerView.ViewHolder> mClass;

	public RecyclerAdapter(Context applicationContext, List<? extends BmobObject> data, int
			maxSize, Class<?
			extends RecyclerView.ViewHolder> cls) {
		mList = data;
		mContext = applicationContext;
		mMaxSize = maxSize;
		mClass = cls;
	}

	public void setmMaxSize(int mMaxSize) {
		this.mMaxSize = mMaxSize;
	}

	public class LoadingViewHolder extends RecyclerView.ViewHolder {
		TextView message;
//		ProgressBar progressbar;

		public LoadingViewHolder(View itemView) {
			super(itemView);
			message = (TextView) itemView.findViewById(R.id.recyclerview_item_progress_message);

		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == EMPTY_VIEW) {
			return new LostViewHolder(LayoutInflater.from(mContext).inflate(R.layout
					.recyclerview_item_empty, parent, false));
		} else if (viewType == NO_MORE_VIEW) {
			RecyclerView.ViewHolder vh = new LostViewHolder(LayoutInflater.from(mContext).inflate
					(R.layout.recyclerview_item_nomore, parent, false));
			return vh;
		} else if (viewType == LOAD_MORE_VIEW) {
			Log.d(TAG, "onCreateViewHolder: ");
			LoadingViewHolder vh = new LoadingViewHolder(LayoutInflater.from(mContext).inflate(R
					.layout.recyclerview_item_progress, parent, false));
			return vh;
		} else {
			if (mClass.getSimpleName().equals("LostViewHolder")) {
				final LostViewHolder vh = new LostViewHolder(LayoutInflater.from(mContext).inflate
						(R.layout.recyclerview_item_lost, parent, false));
				vh.phone.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + vh.phone.getText().toString().trim()));
						mContext.startActivity(intent);
					}
				});
				vh.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mOnItemClickListener.OnItemClick(vh.getAdapterPosition());
					}
				});
				vh.image.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, ImageActivity.class);
						Lost_list img = (Lost_list) mList.get(vh.getAdapterPosition());
						intent.putExtra("imageCode", img.getImage());
						mContext.startActivity(intent);
					}
				});
				return vh;
			} else {
				final FoundViewHolder vh = new FoundViewHolder(LayoutInflater.from(mContext)
						.inflate(R.layout.recyclerview_item_found, parent, false));
				vh.phone.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + vh.phone.getText().toString().trim()));
						mContext.startActivity(intent);
					}
				});
				vh.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
//						mOnItemClickListener.OnItemClick(vh.getAdapterPosition());
					}
				});
				vh.image.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, ImageActivity.class);
						Found_list img = (Found_list) mList.get(vh.getAdapterPosition());
						intent.putExtra("imageCode", img.getImage());
						mContext.startActivity(intent);
					}
				});
				return vh;
			}
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder.getItemViewType() == NO_MORE_VIEW || holder.getItemViewType() == EMPTY_VIEW) {
			return;
		}
		if (holder.getItemViewType() == END_VIEW) {
			if (holder instanceof LostViewHolder) {
				LostViewHolder vh = (LostViewHolder) holder;
				Lost_list lost = (Lost_list) mList.get(position);
				vh.title.setText(lost.getTitle());
				vh.description.setText(lost.getDescription());
				vh.place.setText(lost.getPlace());
				vh.phone.setText(lost.getPhone());
				vh.time.setText(lost.getTime());
				if (lost.getImage() != null) {
					vh.image.setImageBitmap(decode.base64ToBitmap(lost.getImage()));
					vh.image.setVisibility(View.VISIBLE);
				} else {
					vh.image.setVisibility(View.GONE);
				}
			} else if (holder instanceof FoundViewHolder) {
				FoundViewHolder vh = (FoundViewHolder) holder;
				vh.resetStyle();
				Found_list found = (Found_list) mList.get(position);
				Log.d(TAG, "onBindViewHolder: found" + found.getTitle());
				vh.downloadImage.setVisibility(View.GONE);
				vh.title.setText(found.getTitle());
				vh.description.setText(found.getDescription());
				vh.phone.setText(found.getPhone());
				if (found.getImage() != null && found.getHasimage()) {
					Bitmap bitmap = decode.base64ToBitmap(found.getImage());
					vh.image.setImageBitmap(bitmap);
					Palette.Swatch swatch = getPaletteColor(bitmap);
					if (swatch != null) {
						Log.d(TAG, "onBindViewHolder: 颜色识别成功" + swatch.getRgb());
						vh.setStyleBySwatch(swatch);
					}
					vh.image.setVisibility(View.VISIBLE);
				} else if (found.getHasimage() && found.getImage() == null) {
					Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.loading);
					vh.image.setImageBitmap(bitmap);
					vh.image.setVisibility(View.VISIBLE);
				} else {
					vh.image.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public int getItemCount() {
		return mList.size() + 1;
	}

	public interface OnItemClickListener {
		void OnItemClick(int position);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	@Override
	public int getItemViewType(int position) {
		Log.d(TAG, "getItemViewType: " + mMaxSize + ":" + position + ":" + mList.size());
		if (mList.size() == 0) {
			return EMPTY_VIEW;
		}
		if (position == mMaxSize) {
			Log.d(TAG, "getItemViewType: no more");
			return NO_MORE_VIEW;
		}
		if (position == mList.size()) {
			return LOAD_MORE_VIEW;
		}
		return END_VIEW;
	}

	private Palette.Swatch getPaletteColor(Bitmap bitmap) {
		Palette.Builder builder = new Palette.Builder(bitmap);
		Log.d(TAG, "getPaletteColor: " + bitmap.getWidth());
		Palette.Swatch swatch = null;
		builder.setRegion(bitmap.getWidth() / 3, bitmap.getHeight() / 3, bitmap.getWidth() * 2 /
				3, bitmap.getHeight() * 2 / 3);
		builder.maximumColorCount(1);
		List<Palette.Swatch> swatches = builder.generate().getSwatches();
		return swatches.size() > 0 ? swatches.get(0) : null;
	}

	private void setColorBySwatch(Palette.Swatch swatch) {

	}

}
