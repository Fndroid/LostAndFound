package lostandfound.fndroid.com.lostandfound.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/3/28.
 */
public class Found_list extends BmobObject {
	private String title, description, phone, image, shapecode;
	private Boolean hasimage, show;

	public Boolean getShow() {
		return show;
	}

	public void setShow(Boolean show) {
		this.show = show;
	}

	public String getShapecode() {
		return shapecode;
	}

	public void setShapecode(String shapecode) {
		this.shapecode = shapecode;
	}

	public Boolean getHasimage() {
		return hasimage;
	}

	public void setHasimage(Boolean hasimage) {
		this.hasimage = hasimage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
