package lostandfound.fndroid.com.lostandfound.adapters;

import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lostandfound.fndroid.com.lostandfound.R;

/**
 * Created by Administrator on 2016/3/27.
 */
public class FoundViewHolder extends RecyclerView.ViewHolder {
	public TextView title, description, phone, description_label, phone_label;
	public ImageView image,downloadImage;
	public CardView root;

	public FoundViewHolder(View itemView) {
		super(itemView);
		title = (TextView) itemView.findViewById(R.id.recyclerview_item_found_title);
		image = (ImageView) itemView.findViewById(R.id.recyclerview_item_found_image);
		description = (TextView) itemView.findViewById(R.id.recyclerview_item_found_description);
		phone = (TextView) itemView.findViewById(R.id.recyclerview_item_found_phone);
		root = (CardView) itemView.findViewById(R.id.recyclerview_item_found_root);
		description_label = (TextView) itemView.findViewById(R.id
				.recyclerview_item_found_description_label);
		downloadImage = (ImageView) itemView.findViewById(R.id.recyclerview_item_found_downloadImage);
		phone_label = (TextView) itemView.findViewById(R.id.recyclerview_item_found_phone_label);
	}

	public void resetStyle() {
		root.setCardBackgroundColor(Color.parseColor("white"));
		title.setTextColor(Color.parseColor("black"));
		description.setTextColor(Color.parseColor("black"));
		phone.setTextColor(Color.parseColor("black"));
		description_label.setTextColor(Color.parseColor("black"));
		phone_label.setTextColor(Color.parseColor("black"));
	}

	public void setStyleBySwatch(Palette.Swatch swatch) {
		root.setCardBackgroundColor(swatch.getRgb());
		title.setTextColor(swatch.getTitleTextColor());
		description.setTextColor(swatch.getBodyTextColor());
		phone.setTextColor(swatch.getBodyTextColor());
		description_label.setTextColor(swatch.getBodyTextColor());
		phone_label.setTextColor(swatch.getBodyTextColor());
	}
}
