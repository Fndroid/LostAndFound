package lostandfound.fndroid.com.lostandfound.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/13.
 */
public final class error {
	public static String getErrorMsg(int code) {
		switch (code) {
			case 9001:
				return "Application Id为空，请初始化";
			case 9002:
				return "解析返回数据出错";
			case 9003:
				return "上传文件出错";
			case 9004:
				return "文件上传失败";
			case 9005:
				return "批量操作只支持最多50条";
			case 9006:
				return "objectId为空";
			case 9007:
				return "文件大小超过10M";
			case 9008:
				return "上传文件不存在";
			case 9009:
				return "没有缓存数据";
			case 9010:
				return "网络超时";
			case 9011:
				return "BmobUser类不支持批量操作";
			case 9012:
				return "上下文为空";
			case 9013:
				return "BmobObject（数据表名称）格式不正确";
			case 9014:
				return "第三方账号授权失败";
			case 9016:
				return "无网络连接，请检查您的手机网络";
			case 9017:
				return "与第三方登录有关的错误，具体请看对应的错误描述";
			case 9018:
				return "参数不能为空";
			case 9019:
				return "格式不正确：手机号码、邮箱地址、验证码";
			default:
				return "抱歉！未知错误...";
		}
	}
}
