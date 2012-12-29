package org.example.irohaninumber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class IrohaniRanking extends Activity {
	private String RANK_TITLE;
	private String RANK_FILE;
	private static final int RANK_MAX = 11;
	private int i;
	private int rank_cnt;
	private long finishTime;
	private int sec;
	private int dec;
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Context context = this;
		int level;
		long[] rank;

		rank = new long[11];

		super.onCreate(savedInstanceState);
		// タイトルバーを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.irohani_ranking);

		// フォントを取得
		Typeface tf = Typeface.createFromAsset(getAssets(), "acgyosyo.ttf");

		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
		// adView を作成する
		adView = new AdView(this, AdSize.BANNER, "a15093d4c7f2722");
		layout.addView(adView);
		AdRequest request = new AdRequest();

		adView.loadAd(request);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		ListView listView = (ListView) findViewById(R.id.rankListView1);

		// タイムを取得
		Intent intent = getIntent();
		finishTime = intent.getLongExtra("SCORE", -1)/10;
		level = intent.getIntExtra("LEVEL", -1);

		if (level  == 4) {
			RANK_TITLE = "初級の得点";
			RANK_FILE = "ranking_1.dat";
		} else if (level  == 5) {
			RANK_TITLE = "中級の得点";
			RANK_FILE = "ranking_2.dat";
		} else if (level  == 6) {
			RANK_TITLE = "上級の得点";
			RANK_FILE = "ranking_3.dat";
		} else {
			RANK_TITLE = "得点";
			RANK_FILE = "ranking_other.dat";
		}

		// レベルに応じたタイトルを表示
		TextView title_text = (TextView)findViewById(R.id.titleView1);
		title_text.setTypeface(tf);
		title_text.setText(RANK_TITLE);
		// 今回のスコアを表示
		if(finishTime != 0) {
			sec = (int)finishTime/100;
			dec = (int)finishTime%100;
			TextView text = (TextView)findViewById(R.id.timeView1);
			text.setText("今回の得点    " + sec + "." + dec + " 秒");
			text.setTypeface(tf);
		}

		// ファイルから今までの記録を読み込み
		DataInputStream in = null;
		try {
			FileInputStream file = context.openFileInput(RANK_FILE);
			in = new DataInputStream(file);
			for(int i = 0; i < RANK_MAX; i++) {
				rank[i] = in.readLong();
			}
			in.close();
		}  catch(final FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}

		// 今回のタイムを追加
		rank[RANK_MAX-1] = finishTime;
		// ソートしてランキング形式で表示
		Arrays.sort(rank);
		for(i = 0, rank_cnt = 0; i < RANK_MAX; i++) {
			// ランキングは上位10件まで
			if(rank_cnt == RANK_MAX-1) {
				break;
			}
			// ランキングをアダプタ追加
			if(rank[i] != 0) {
				sec = (int)rank[i]/100;
				dec = (int)rank[i]%100;
				rank_cnt++;
				adapter.add(rank_cnt + "位    " + sec + "." + dec + " 秒");
			}
		}

		// アダプターを設定
		listView.setAdapter(adapter);

		// ゲーム終了後の場合だけ書き込みを行う
		if (finishTime != 0) {
			// 更新したランキングをファイルに書き込み
			DataOutputStream out = null;
			try {
				FileOutputStream file = context.openFileOutput(RANK_FILE, Context.MODE_PRIVATE);
				out = new DataOutputStream(file);
				// 11個目はランク外として書かない。
				for(int i = 0; i < RANK_MAX; i++) {
					if(rank[i] != 0) {
						out.writeLong(rank[i]);
					}
				}
				out.flush();
				file.close();
			}  catch(final FileNotFoundException e) {
				System.out.println("ファイルが見つかりません。");
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		Button btnBack = (Button)findViewById(R.id.back);
		btnBack.setTypeface(tf);
		btnBack.setTextSize(20.0f);
		btnBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
     }
}