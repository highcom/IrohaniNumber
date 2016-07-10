package org.example.irohaninumber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

//import jp.basicinc.gamefeat.ranking.android.sdk.controller.GFRankingController;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

//import com.google.ads.AdListener;
//import com.google.ads.AdRequest;
//import com.google.ads.Ad;
//import com.google.ads.AdRequest.ErrorCode;
//import com.google.ads.InterstitialAd;


public class IrohaniRanking extends Activity{
	private String RANK_TITLE;
	private String RANK_FILE;
	private String RANK_WORLD;
	private String RANK_WORLD_SCORE;
	private static final int RANK_MAX = 11;
	private int i;
	private int rank_cnt;
	private long finishTime;
	private long[] rank;
	private int sec;
	private int dec;
	private int world_score_flg;

	//private String unitID = "ca-app-pub-3217012767112748/4715424317";
	//private InterstitialAd interstitialAd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Context context = this;
		int level;
		world_score_flg = 0;

		rank = new long[11];

		super.onCreate(savedInstanceState);
		// タイトルバーを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.irohani_ranking);

		// フォントを取得
		Typeface tf = Typeface.createFromAsset(getAssets(), "acgyosyo.ttf");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		ListView listView = (ListView) findViewById(R.id.rankListView1);

		// インタースティシャルを作成する。
		//interstitialAd = new InterstitialAd(this, unitID);
	    // 広告リクエストを作成する。
	    //AdRequest adRequest = new AdRequest();
	    // インタースティシャルの読み込みを開始する。
	    //interstitialAd.loadAd(adRequest);
	    // Ad Listener を設定して下のコールバックを使用する
	    //interstitialAd.setAdListener((AdListener) this);

		// タイムを取得
		Intent intent = getIntent();
		finishTime = intent.getLongExtra("SCORE", -1)/10;
		level = intent.getIntExtra("LEVEL", -1);

		if (level  == 4) {
			RANK_TITLE = "初級の得点";
			RANK_FILE = "ranking_1.dat";
			RANK_WORLD = "org.example.irohaninumber.1";
		} else if (level  == 5) {
			RANK_TITLE = "中級の得点";
			RANK_FILE = "ranking_2.dat";
			RANK_WORLD = "org.example.irohaninumber.2";
		} else if (level  == 6) {
			RANK_TITLE = "上級の得点";
			RANK_FILE = "ranking_3.dat";
			RANK_WORLD = "org.example.irohaninumber.3";
		} else {
			RANK_TITLE = "得点";
			RANK_FILE = "ranking_other.dat";
			RANK_WORLD = "org.example.irohaninumber.1";
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
				if (world_score_flg == 0)
				{
					// 一番良いタイムを世界順位として登録
					RANK_WORLD_SCORE = sec + "." + dec;
					world_score_flg = 1;
				}
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

		// 表題に戻る
		ImageButton btnBack = (ImageButton)findViewById(R.id.back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		// 世界順位ボタン
		Button btnWorldRank = (Button)findViewById(R.id.worldRank);
		btnWorldRank.setTypeface(tf);
		btnWorldRank.setTextSize(20.0f);
		btnWorldRank.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//GFRankingController.show(IrohaniRanking.this, RANK_WORLD);
			}
		});

		// 得点送信ボタン
		Button btnScoreSend = (Button)findViewById(R.id.scoreSend);
		btnScoreSend.setTypeface(tf);
		btnScoreSend.setTextSize(20.0f);
		btnScoreSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (rank_cnt > 0)
				{
					//スコア送信（ランキング用）
					String[] gameIds = {RANK_WORLD};
					String[] scores = {RANK_WORLD_SCORE};
					//GFRankingController appController = GFRankingController.getIncetance(IrohaniRanking.this);
					//appController.sendScore(gameIds, scores);
				}
			}
		});

		// ツイートする
		ImageButton btnTweet = (ImageButton)findViewById(R.id.Tweet);
		btnTweet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				if(finishTime != 0) {
					sec = (int)finishTime/100;
					dec = (int)finishTime%100;
					intent.putExtra(Intent.EXTRA_TEXT,"【" + RANK_TITLE + " : " + sec + "." + dec + " 秒でクリア！" + "】"
											+ "Androidアプリ「タッチ！いろはにほへと！」\nhttps://play.google.com/store/apps/details?id=org.example.irohaninumber");
				}
				else
				{
					sec = 0;
					dec = 0;
					for(int i = 0; i < RANK_MAX; i++) {
						if(rank[i] != 0) {
							sec = (int)rank[i]/100;
							dec = (int)rank[i]%100;
							break;
						}
					}
					intent.putExtra(Intent.EXTRA_TEXT,"【" + RANK_TITLE + " : " + sec + "." + dec + " 秒でクリア！" + "】"
							+ "Androidアプリ「タッチ！いろはにほへと！」\nhttps://play.google.com/store/apps/details?id=org.example.irohaninumber");
				}
				intent.setType("text/plain");
				startActivity(intent);
			}
		});
	}

//	@Override
//	public void onReceiveAd(Ad ad) {
//	    if (ad == interstitialAd) {
//	    	// 20%の確立で広告を表示させる
//	    	if ((int)(Math.random()*100.0d)%5 == 0)
//	    	{
//	    		interstitialAd.show();
//	    	}
//	    }
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
     }

}