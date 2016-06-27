package lostandfound.fndroid.com.lostandfound.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/3/29.
 */
public class Feedback_list extends BmobObject {
	private String contain;
	private String qq;

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getContain() {
		return contain;
	}

	public void setContain(String contain) {
		this.contain = contain;
	}
}
