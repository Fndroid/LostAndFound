package lostandfound.fndroid.com.lostandfound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import c.b.BP;
import c.b.PListener;
import lostandfound.fndroid.com.lostandfound.utils.Variable;

/**
 * Created by Administrator on 2016/3/27.
 */
public class SupportActivity extends AppCompatActivity {
	private static final String TAG = "SupportActivity";

	private Button pay;
	private RadioButton ali, tecent;
	private EditText money;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support);
		BP.init(this, Variable.APPLICATIONID);
		pay = (Button) findViewById(R.id.support_pay);
		ali = (RadioButton) findViewById(R.id.support_radioButtonAli);
		tecent = (RadioButton) findViewById(R.id.support_radioButtonTecent);
		money = (EditText) findViewById(R.id.support_money);
		pay.setOnClickListener(new MyOnClickListener());
	}

	private class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			String m = money.getText().toString();
			if (!m.equals("")) {
				BP.pay("支持作者", "感谢", Double.parseDouble(m), ali.isChecked(), new PListener() {
					@Override
					public void orderId(String s) {
					}

					@Override
					public void succeed() {
						Toast.makeText(SupportActivity.this, "感谢你的支持！", Toast.LENGTH_SHORT).show();
						SupportActivity.this.finish();
					}

					@Override
					public void fail(int i, String s) {
						Toast.makeText(SupportActivity.this, s, Toast.LENGTH_SHORT).show();

					}

					@Override
					public void unknow() {
						Toast.makeText(SupportActivity.this, "未知异常！", Toast.LENGTH_SHORT).show();
					}

				});
			} else {
				Toast.makeText(SupportActivity.this, "金额为空哦", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
