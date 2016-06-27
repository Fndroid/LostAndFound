package lostandfound.fndroid.com.lostandfound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import lostandfound.fndroid.com.lostandfound.beans.Feedback_list;
import lostandfound.fndroid.com.lostandfound.utils.Variable;

/**
 * Created by Administrator on 2016/3/29.
 */
public class FeedbackActivity extends AppCompatActivity {
	private Button confirm;
	private EditText contain, qq;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		Bmob.initialize(this, Variable.APPLICATIONID);
		contain = (EditText) findViewById(R.id.feedback_et);
		confirm = (Button) findViewById(R.id.feedback_bt);
		qq = (EditText) findViewById(R.id.feedback_qq);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str_contain = contain.getText().toString();
				String str_qq = qq.getText().toString();
				if (!TextUtils.isEmpty(str_contain) && !TextUtils.isEmpty(str_qq)) {
					Feedback_list feedback = new Feedback_list();
					feedback.setContain(str_contain);
					feedback.setQq(str_qq);
					feedback.save(FeedbackActivity.this, new SaveListener() {
						@Override
						public void onSuccess() {
							Toast.makeText(FeedbackActivity.this, "感谢您的反馈", Toast.LENGTH_SHORT)
									.show();
							FeedbackActivity.this.finish();
						}

						@Override
						public void onFailure(int i, String s) {
							Toast.makeText(FeedbackActivity.this, s, Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					Toast.makeText(FeedbackActivity.this, "请先填写你的反馈意见和联系方式再提交吧*V*", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}
}
