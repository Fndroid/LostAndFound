package lostandfound.fndroid.com.lostandfound.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/3/20.
 */
public class Lost_list_img extends BmobObject {
	private String imageId,image;

	public String getImageId() {
		return imageId;
	}

	public String getImage() {
		return image;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
