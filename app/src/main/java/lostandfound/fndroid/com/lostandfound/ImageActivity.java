package lostandfound.fndroid.com.lostandfound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import lostandfound.fndroid.com.lostandfound.R;
import lostandfound.fndroid.com.lostandfound.utils.decode;

/**
 * Created by Administrator on 2016/3/19.
 */
public class ImageActivity extends AppCompatActivity {
	private ImageView iv;
	private String code;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showimage);
		iv = (ImageView) findViewById(R.id.showimage_iv_image);
		code = getIntent().getStringExtra("imageCode");
		if (code != null){
			iv.setImageBitmap(decode.base64ToBitmap(code));
		}
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
