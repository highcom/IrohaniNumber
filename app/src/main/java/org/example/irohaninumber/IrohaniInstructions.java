package org.example.irohaninumber;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class IrohaniInstructions extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// タイトルバーを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.irohani_instructions);

		// フォント取得
		Typeface tf = Typeface.createFromAsset(getAssets(), "acgyosyo.ttf");
        // フォント設定
        TextView text1 = (TextView)findViewById(R.id.textView1);
        text1.setTypeface(tf);

		Button btnPrac = (Button)findViewById(R.id.back);
		btnPrac.setTypeface(tf);
		btnPrac.setTextSize(20.0f);
		btnPrac.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}
