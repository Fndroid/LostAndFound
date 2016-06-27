package lostandfound.fndroid.com.lostandfound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import lostandfound.fndroid.com.lostandfound.beans.BarcodeResult;
import lostandfound.fndroid.com.lostandfound.beans.Found_list;
import lostandfound.fndroid.com.lostandfound.utils.BitmapHelper;
import lostandfound.fndroid.com.lostandfound.utils.Util;
import lostandfound.fndroid.com.lostandfound.utils.Variable;
import lostandfound.fndroid.com.lostandfound.utils.decode;

/**
 * Created by Administrator on 2016/3/12.
 */
public class AddFoundActivity extends AppCompatActivity implements View.OnClickListener {
	private static final String TAG = "AddLostActivity";

	private static final int FROMAT_CORRECT = 0;
	private static final int FORMAT_ERROR_PHONE = 1;
	private static final int FORMAT_ERROR_DESCRIPTION_EMPTY = 2;
	private static final int FORMAT_ERROR_TITLE_EMPTY = 3;
	private static final int TAKE_PHOTO = 5;
	public static final int GET_SHAPECODE = 6;
	private static final int SCANBARCODE = 10;
	private static final int THUMB_SIZE = 150;

	private EditText title, description, phone;
	private FloatingActionButton confirm;
	private CoordinatorLayout root;
	private AlertDialog uploadingDialog;
	private ImageView openCamera;
	private Uri imageUri;
	private Bitmap imageBitmap;
	private SharedPreferences sharePerences;
	private SharedPreferences.Editor ediotr;
	private Boolean isNotify;
	private ImageButton scanBarcode;
	private IWXAPI api;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfound);
		Bmob.initialize(this, Variable.APPLICATIONID);
		api =  WXAPIFactory.createWXAPI(this, Variable.APPKEY_WEIXIN);
		initViews();
	}

	private void initViews() {
		sharePerences = getSharedPreferences("settings", MODE_PRIVATE);
		isNotify = sharePerences.getBoolean("autoNotify", false);
		root = (CoordinatorLayout) findViewById(R.id.addFound_root);
		title = (EditText) findViewById(R.id.addFound_et_title);
		phone = (EditText) findViewById(R.id.addFound_et_phone);
		description = (EditText) findViewById(R.id.addFound_et_description);
		confirm = (FloatingActionButton) findViewById(R.id.addFound_fab_confirm);
		confirm.setOnClickListener(this);
		openCamera = (ImageView) findViewById(R.id.addFound_iv_openCamera);
		openCamera.setOnClickListener(this);
		scanBarcode = (ImageButton) findViewById(R.id.addFound_ib_scanBarcode);
		scanBarcode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.addFound_fab_confirm:
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
			case R.id.addFound_iv_openCamera:
				startActivityForImage();
				break;
			case R.id.addFound_ib_scanBarcode:
				startActivityForResult(new Intent(this, ScannerActivity.class), SCANBARCODE);
				break;
			case R.id.weche_dialog_comment:
				sendToWXTimeline(true);
				break;
			case R.id.weche_dialog_session:
				sendToWXTimeline(false);
				break;
		}

	}

	private void startActivityForImage() {
		File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
		try {
			if (outputImage.exists()) {
				outputImage.delete();
			}
			outputImage.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		imageUri = Uri.fromFile(outputImage);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, TAKE_PHOTO);
	}

	private void buildUploadingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		uploadingDialog = builder.setCancelable(false).setView(LayoutInflater.from(this).inflate(R
				.layout.uploading_progress, null, true)).create();
		uploadingDialog.show();
	}

	private void saveDataToServer() {
		if (isNotify) {
			ediotr = sharePerences.edit();
			ediotr.putBoolean("autoNotify", false);
			ediotr.apply();
		}
		final Found_list found = new Found_list();
		found.setTitle(title.getText().toString().trim());
		found.setDescription(description.getText().toString().trim());
		found.setPhone(phone.getText().toString().trim());
		found.setImage(decode.bitmapToBase64(imageBitmap));
		found.setHasimage(imageBitmap != null);
		found.setShow(false);
		found.save(this, new SaveListener() {
			@Override
			public void onSuccess() {
				if (isNotify) {
					ediotr.putBoolean("autoNotify", true);
					ediotr.apply();
				}
				Snackbar.make(root, "登记成功，是否将图片分享至社交媒体寻找失主？", Snackbar.LENGTH_LONG).setAction("是",
						new View.OnClickListener() {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case TAKE_PHOTO:
				if (resultCode == RESULT_OK) {
					try {
						BitmapFactory.Options options = new BitmapFactory.Options();
						BitmapHelper helper = new BitmapHelper();
						options.inSampleSize = helper.getInSampleSize(getContentResolver()
								.openInputStream(imageUri), imageUri, 500, 500);
						imageBitmap = BitmapFactory.decodeStream(getContentResolver()
								.openInputStream(imageUri), null, options);
						Log.d(TAG, "onActivityResult: inSampleSize=" + options.inSampleSize);
						openCamera.setImageBitmap(imageBitmap);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case GET_SHAPECODE:
				if (resultCode == RESULT_OK) {
				}
				break;
			case SCANBARCODE:
				if (resultCode == RESULT_OK) {
					String barcode = data.getStringExtra("barcode");
					Log.d(TAG, "onActivityResult: " + barcode);
					if (TextUtils.isDigitsOnly(barcode) && barcode.length() == 13) {
						Log.d(TAG, "onActivityResult: 条码扫描成功");
						AsyncTask<String, Integer, String> asyncTask = new CodeRequestAsyncTask();
						asyncTask.execute(barcode);
					} else {
						Snackbar.make(root, "扫描失败，请重新对准条形码扫描", Snackbar.LENGTH_LONG).setAction
								("重试", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								startActivityForResult(new Intent(AddFoundActivity.this,
										ScannerActivity.class), SCANBARCODE);
							}
						}).show();
					}
				} break;
		}
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

	private class CodeRequestAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			String barcode = params[0];
			BufferedReader reader;
			StringBuffer sbf = new StringBuffer();
			try {
				URL url = new URL("http://apis.baidu" +
						".com/3023/barcode/barcode?barcode=" + barcode);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("apikey", Variable.APIKEY_BAIDU);
				connection.connect();
				InputStream is = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String strRead;
				while ((strRead = reader.readLine()) != null) {
					sbf.append(strRead);
					sbf.append("\r\n");
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return sbf.toString();
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (!s.equals("") && s.contains("name")) {
				Log.d(TAG, "onPostExecute: 条码数据获取成功");
				Gson gson = new Gson();
				BarcodeResult barcodeResult = gson.fromJson(s, BarcodeResult.class);
				title.setText(barcodeResult.getName());
			} else {
				title.setText("");
				Snackbar.make(root, "抱歉，没有找到该条形码对应的数据，请手动输入", Snackbar.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			title.setText("联网分析中...");
		}
	}

	private void sendToWXTimeline(boolean comment) {
		WXImageObject imageObject = new WXImageObject(imageBitmap);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imageObject;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(imageBitmap, THUMB_SIZE, THUMB_SIZE, true);
		msg.thumbData = Util.bmpToByteArray2(thumbBmp, true);  // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = comment? SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
