package lostandfound.fndroid.com.lostandfound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import lostandfound.fndroid.com.lostandfound.beans.moweibo;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void shit(View view) {
        Intent intent  = new Intent(this, ShitActivity.class);
        moweibo mo = new moweibo();
        mo.setContent("shit");
        mo.setWeiboid("1");
        mo.setUsername("sfdsaf");
        ArrayList list = new ArrayList();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_settings_black_24dp);
        list.add(bitmap);
	    list.add(bitmap);
	    list.add(bitmap);
        mo.setPhotos(list);
        intent.putExtra("shit", mo);
        startActivity(intent);
    }
}
