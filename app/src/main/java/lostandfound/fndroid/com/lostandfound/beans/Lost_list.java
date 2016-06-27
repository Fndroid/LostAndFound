package lostandfound.fndroid.com.lostandfound.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/3/11.
 */
public class Lost_list extends BmobObject {
	private String title, description, time, phone, place, image;
	private Boolean show;

	public Boolean getShow() {
		return show;
	}

	public void setShow(Boolean show) {
		this.show = show;
	}

	public Lost_list() {
		this.setTableName("Lost_list");
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getTime() {
		return time;
	}

	public String getPhone() {
		return phone;
	}

	public String getPlace() {
		return place;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Lost{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				", time='" + time + '\'' +
				", phone='" + phone + '\'' +
				", place='" + place + '\'' +
				'}';
	}
}
