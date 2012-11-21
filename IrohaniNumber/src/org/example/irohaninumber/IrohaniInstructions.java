package org.example.irohaninumber;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class IrohaniInstructions extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// タイトルバーを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.irohani_instructions);

		Button btnPrac = (Button)findViewById(R.id.back);
		btnPrac.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}
