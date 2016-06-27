package lostandfound.fndroid.com.lostandfound;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Calendar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import lostandfound.fndroid.com.lostandfound.beans.Lost_list;
import lostandfound.fndroid.com.lostandfound.utils.Variable;
import lostandfound.fndroid.com.lostandfound.utils.decode;

/**
 * Created by Administrator on 2016/3/12.
 */
public class AddLostActivity extends AppCompatActivity implements View.OnClickListener,
		IWeiboHandler.Response {
	private static final String TAG = "AddLostActivity";

	private static final int FROMAT_CORRECT = 0;
	private static final int FORMAT_ERROR_PHONE = 1;
	private static final int FORMAT_ERROR_DESCRIPTION_EMPTY = 2;
	private static final int FORMAT_ERROR_TITLE_EMPTY = 3;
	private static final int TAKE_PHOTO = 5;

	private TextView time;
	private EditText title, place, description, phone;
	private FloatingActionButton confirm;
	//	private ImageView showImage;
	private CoordinatorLayout root;
	private Uri imageUri;
	private Bitmap imageBitmap;
	private AlertDialog uploadingDialog;
	private IWeiboShareAPI mWeiboShareAPI;
	private IWXAPI api;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addlost);
		Bmob.initialize(this, Variable.APPLICATIONID);
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Variable.APPKEY_SINA);
		mWeiboShareAPI.registerApp();
		api = WXAPIFactory.createWXAPI(this, Variable.APPKEY_WEIXIN);
		initViews();
	}

	private void initViews() {
		root = (CoordinatorLayout) findViewById(R.id.addlost_root);
		title = (EditText) findViewById(R.id.addlost_et_title);
		time = (TextView) findViewById(R.id.addlost_et_time);
		place = (EditText) findViewById(R.id.addlost_et_place);
		phone = (EditText) findViewById(R.id.addlost_et_phone);
		description = (EditText) findViewById(R.id.addlost_et_description);
		confirm = (FloatingActionButton) findViewById(R.id.add_fab_confirm);
		confirm.setOnClickListener(this);
		time.setOnClickListener(this);
//		showImage = (ImageView) findViewById(R.id.addlost_iv_showImage);
//		showImage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add_fab_confirm:
				switch (checkForSave()) {
					case FROMAT_CORRECT:
						buildUploadingDialog();
						saveDataToServer();
						break;
					case FORMAT_ERROR_TITLE_EMPTY:
						Snackbar.make(root, "标题不能为空，请填写", Snackbar.LENGTH_LONG).show();
						break;
					case FORMAT_ERROR_DESCRIPTION_EMPTY:
						Snackbar.make(root, "描述不能为空，请填写", Snackbar.LENGTH_LONG).show();
						break;
					case FORMAT_ERROR_PHONE:
						Snackbar.make(root, "联系电话格式不正确，请检查", Snackbar.LENGTH_LONG).show();
						break;
				}
				break;
			case R.id.addlost_et_time:
				showDatePicker();
				break;
			case R.id.weche_dialog_comment:
				sendToWXTimeline(true);
				break;
			case R.id.weche_dialog_session:
				sendToWXTimeline(false);
				break;
		}

	}

	private void buildUploadingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		uploadingDialog = builder.setCancelable(false).setView(LayoutInflater.from(this).inflate(R
				.layout.uploading_progress, null, true)).create();
		uploadingDialog.show();
	}

	private void saveDataToServer() {
		final Lost_list lost = new Lost_list();
		lost.setTime(time.getText().toString().equals("") ? getNowDateString() : time.getText()
				.toString().trim());
		lost.setTitle(title.getText().toString().trim());
		lost.setDescription(description.getText().toString().trim());
		lost.setPlace(place.getText().toString().trim());
		lost.setPhone(phone.getText().toString().trim());
		lost.setImage(decode.bitmapToBase64(imageBitmap));
		lost.setShow(false);
		lost.save(this, new SaveListener() {
			@Override
			public void onSuccess() {
				Snackbar.make(root, "登记成功，是否分享至社交媒体寻求帮助", Snackbar.LENGTH_LONG).setAction("是", new
						View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showSendPickDialog();
					}
				}).show();
				uploadingDialog.dismiss();
			}

			@Override
			public void onFailure(int i, String s) {
				Snackbar.make(root, "登记失败：" + s, Snackbar.LENGTH_LONG).show();
			}
		});
	}

	private void showSendPickDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.wechat_dialog, null);
		LinearLayout session = (LinearLayout) view.findViewById(R.id.weche_dialog_session);
		LinearLayout comment = (LinearLayout) view.findViewById(R.id.weche_dialog_comment);
		session.setOnClickListener(this);
		comment.setOnClickListener(this);
		builder.setView(view);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * check the format of input
	 *
	 * @return int-FORMAT_ERROR_TITLE_EMPTY、FORMAT_ERROR_DESCRIPTION_EMPTY、FORMAT_ERROR_PHONE
	 * 、FROMAT_CORRECT
	 */
	private int checkForSave() {
		if (title.getText().toString().trim().isEmpty()) {
			return FORMAT_ERROR_TITLE_EMPTY;
		}
		if (description.getText().toString().trim().isEmpty()) {
			return FORMAT_ERROR_DESCRIPTION_EMPTY;
		}
		if (phone.getText().toString().trim().length() > 11 || phone.getText().toString().trim()
				.isEmpty() || phone.getText().toString().trim().length() == 10) {
			return FORMAT_ERROR_PHONE;
		}
		return FROMAT_CORRECT;
	}

	private String getNowDateString() {
		Calendar calendar = Calendar.getInstance();
		int year, month, day;
		year = calendar.get(calendar.YEAR);
		month = calendar.get(calendar.MONTH) + 1;
		day = calendar.get(calendar.DAY_OF_MONTH);
		return year + "-" + month + "-" + day;
	}

	private void showDatePicker() {
		View view = LayoutInflater.from(this).inflate(R.layout.add_dp, null);
		DatePicker dp = (DatePicker) view.findViewById(R.id.add_dp_dp);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		MyOnClickListener listener = new MyOnClickListener(dp);
		builder.setPositiveButton("OK", listener);
		builder.setNegativeButton("Cancel", listener);
		builder.show();
	}

	@Override
	public void onResponse(BaseResponse baseResponse) {
		switch (baseResponse.errCode) {
			case WBConstants.ErrorCode.ERR_OK:
				Log.d(TAG, "onResponse: ok");
				break;
			case WBConstants.ErrorCode.ERR_FAIL:
				Log.d(TAG, "onResponse: fail");
				break;
			case WBConstants.ErrorCode.ERR_CANCEL:
				Log.d(TAG, "onResponse: cancel");
				break;
		}
	}

	private class MyOnClickListener implements DialogInterface.OnClickListener {
		private DatePicker mDatePicker;

		public MyOnClickListener(DatePicker mDatePicker) {
			this.mDatePicker = mDatePicker;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					int year, month, day;
					year = mDatePicker.getYear();
					month = mDatePicker.getMonth() + 1;
					day = mDatePicker.getDayOfMonth();
					time.setText(year + "-" + month + "-" + day);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
			}
		}
	}


	private void sendMultiMessage(boolean hasText) {
		WeiboMultiMessage message = new WeiboMultiMessage();
		if (hasText) {
			TextObject textObject = new TextObject();
			textObject.text = "gdin";
			message.textObject = textObject;
		}
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = message;
		mWeiboShareAPI.sendRequest(AddLostActivity.this, request);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	private void sendToWXTimeline(boolean comment) {
		String str_time = time.getText().toString().equals("") ? getNowDateString() : time.getText
				().toString().trim();
		String str_desc = description.getText().toString();
		String str_phone = phone.getText().toString();
		String str_place = place.getText().toString();
		String str_title = title.getText().toString();

		StringBuilder sb = new StringBuilder();
		sb.append("各位小伙伴们大家好，我在");
		sb.append(str_time);
		sb.append("日，在");
		sb.append(str_place);
		sb.append("丢失了我的");
		sb.append(str_title);
		sb.append("。它是");
		sb.append(str_desc);
		sb.append("，看到或者捡到请联系：");
		sb.append(str_phone);
		sb.append("，万分感谢。");

		WXTextObject textObj = new WXTextObject();
		textObj.text = sb.toString();
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = "发送给朋友";
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = comment ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req
				.WXSceneSession;
		api.sendReq(req);
	}

}
