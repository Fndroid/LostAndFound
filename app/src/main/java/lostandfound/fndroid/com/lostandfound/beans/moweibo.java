package lostandfound.fndroid.com.lostandfound.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/7.
 */

public class moweibo implements Parcelable {
	private String weiboid;
	private String content;
	private String username;
	private ArrayList<Object> photos;

	public moweibo() {
	}

	public static final Parcelable.Creator<moweibo> CREATOR = new Creator<moweibo>() {
		@Override
		public moweibo createFromParcel(Parcel source) {
			return new moweibo(source);
		}

		@Override
		public moweibo[] newArray(int size) {
			return new moweibo[0];
		}
	};

	private moweibo(Parcel parcel) {
		weiboid = parcel.readString();
		content = parcel.readString();
		username = parcel.readString();
		photos = parcel.readArrayList(Object.class.getClassLoader());
//		parcel.readTypedList(photos, Bitmap.CREATOR);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(weiboid);
		dest.writeString(content);
		dest.writeString(username);
//		dest.writeTypedList(photos);
		dest.writeArray(photos.toArray());
	}
	public ArrayList<Object> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<Object> photos) {
		this.photos = photos;
	}

	public String getWeiboid() {
		return weiboid;
	}

	public void setWeiboid(String weiboid) {
		this.weiboid = weiboid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "moweibo{" +
				"weiboid='" + weiboid + '\'' +
				", content='" + content + '\'' +
				", username='" + username + '\'' +
				", photos=" + photos +
				'}';
	}
}
