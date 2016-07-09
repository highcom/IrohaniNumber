package org.example.irohaninumber;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class IrohaniNumber extends Activity {
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// タイトルバーを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_irohani_number);

		// フォントを取得
		Typeface tf = Typeface.createFromAsset(getAssets(), "acgyosyo.ttf");
        // タイトルのフォント設定
        TextView text1 = (TextView)findViewById(R.id.textView1);
        text1.setTypeface(tf);

		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
		// adView を作成する
		adView = new AdView(this, AdSize.BANNER, "ca-app-pub-3217012767112748/8348877912");
		//adView = new AdView(this, AdSize.BANNER, "a15093d4c7f2722");
		layout.addView(adView);
		AdRequest request = new AdRequest();

		adView.loadAd(request);
		Button btnInst = (Button)findViewById(R.id.instructions);
		btnInst.setTypeface(tf);
		btnInst.setTextSize(20.0f);
		btnInst.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, IrohaniInstructions.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button btnPrac = (Button)findViewById(R.id.practice);
		btnPrac.setTypeface(tf);
		btnPrac.setTextSize(20.0f);
		btnPrac.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, TextureIroNum.class);
				intent.putExtra("LEVEL", 3);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button btnLev1 = (Button)findViewById(R.id.level1);
		btnLev1.setTypeface(tf);
		btnLev1.setTextSize(20.0f);
		btnLev1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, TextureIroNum.class);
				intent.putExtra("LEVEL", 4);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button btnLev2 = (Button)findViewById(R.id.level2);
		btnLev2.setTypeface(tf);
		btnLev2.setTextSize(20.0f);
		btnLev2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, TextureIroNum.class);
				intent.putExtra("LEVEL", 5);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button btnLev3 = (Button)findViewById(R.id.level3);
		btnLev3.setTypeface(tf);
		btnLev3.setTextSize(20.0f);
		btnLev3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, TextureIroNum.class);
				intent.putExtra("LEVEL", 6);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button btnScore1 = (Button)findViewById(R.id.level1_score);
		btnScore1.setTypeface(tf);
		btnScore1.setTextSize(20.0f);
		btnScore1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, IrohaniRanking.class);
				intent.putExtra("LEVEL", 4);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button btnScore2 = (Button)findViewById(R.id.level2_score);
		btnScore2.setTypeface(tf);
		btnScore2.setTextSize(20.0f);
		btnScore2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, IrohaniRanking.class);
				intent.putExtra("LEVEL", 5);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button btnScore3 = (Button)findViewById(R.id.level3_score);
		btnScore3.setTypeface(tf);
		btnScore3.setTextSize(20.0f);
		btnScore3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(IrohaniNumber.this, IrohaniRanking.class);
				intent.putExtra("LEVEL", 6);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_irohani_number, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}
}
