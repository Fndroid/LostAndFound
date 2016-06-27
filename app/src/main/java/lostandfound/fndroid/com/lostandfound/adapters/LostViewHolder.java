package lostandfound.fndroid.com.lostandfound.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lostandfound.fndroid.com.lostandfound.R;

/**
 * Created by Administrator on 2016/3/27.
 */
public class LostViewHolder extends RecyclerView.ViewHolder {
	public TextView title, description, phone, time, place;
	public ImageView image;

	public LostViewHolder(View itemView) {
		super(itemView);
		title = (TextView) itemView.findViewById(R.id.recyclerview_item_lost_title);
		description = (TextView) itemView.findViewById(R.id.recyclerview_item_lost_description);
		phone = (TextView) itemView.findViewById(R.id.recyclerview_item_lost_phone);
		time = (TextView) itemView.findViewById(R.id.recyclerview_item_lost_time);
		place = (TextView) itemView.findViewById(R.id.recyclerview_item_lost_place);
		image = (ImageView) itemView.findViewById(R.id.recyclerview_item_lost_image);
	}
}
