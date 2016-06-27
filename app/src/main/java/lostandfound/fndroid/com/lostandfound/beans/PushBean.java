package lostandfound.fndroid.com.lostandfound.beans;

/**
 * Created by Administrator on 2016/3/26.
 */
public class PushBean {
	private String title;
	private String text;
	private String url;

	public PushBean(String title, String text, String url) {
		this.title = title;
		this.text = text;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
