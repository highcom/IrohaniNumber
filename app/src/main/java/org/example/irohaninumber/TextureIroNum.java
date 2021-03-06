package org.example.irohaninumber;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.example.android.apis.graphics.spritetext.LabelMaker;
import com.example.android.apis.graphics.spritetext.NumericSprite;

public class TextureIroNum extends Activity implements GLSurfaceView.Renderer {

	// GLSurfaceView
	private GLSurfaceView gLSurfaceView;
	// 効果音
	private SoundPool soundPool;
	// 背景画像ID
	private int backgroundID;

	// 読み込んだテクスチャのID
	private int frontTexID;
	private int backTexID;
	private int[] iroID;
	private int level;
	private int width;
	private int height;
	private int texsize;
	private int texX;
	private int texY;
	private float touchX;
	private float touchY;
	private float[] angle;
	private float[] addAngle;
	// テクスチャの位置
	private float x;
	private float y;
	// タッチする順番
	private int startNum;
	private int touchNum;
	// 文字列を描画するためのクラス
	private Paint labelPaint;
	private LabelMaker labels;
	private int labelTIME;
	private int labelDot;
	private int labelNEXT;
	private long startTime;
	// 数値を描画するためのクラス
	private NumericSprite numericSprite;
	private int sec = 0;
	private int dec = 0;
	private float secWidth;
	// 「よーい」時
	private long readyStartTime;
	private long readyEndTime;
	private int ready1ID;
	private int ready2ID;
	private float readyScale;
	// ゲームスタートフラグ
	private final int READY = 0;
	private final int PLAYING = 1;
	private final int END = 2;
	private int gameState = READY;
	// 「終了」時
	private long finishStartTime;
	private long finishEndTime;
	private long finishTime;
	private int finish1ID;
	private int finish2ID;
	private int finishMove;
	private float finishScale;
	// サウンドID
	private int secorrect;
	private int semistake;
	private int secomplete;
	private int volume;

	public TextureIroNum() {
		sec = 0;
		dec = 0;
		frontTexID = -1;
		backTexID = -1;
		readyStartTime = -1;
		readyEndTime = -1;
		readyScale = 2.0f;
		finishStartTime = -1;
		finishEndTime = -1;
		finishMove = 0;
		finishScale = 2.0f;
		// 文字列をセット
		labels = null;
		numericSprite = null;
		labelPaint = new Paint();
		labelPaint.setTextSize(36);
		labelPaint.setAntiAlias(true);
		labelPaint.setARGB(0xff, 0xff, 0xff, 0x00);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// サウンドプレイヤーの生成
		Context context = this.getApplicationContext();
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		volume = audio.getStreamVolume(AudioManager.STREAM_RING);
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		secorrect = soundPool.load(context, R.raw.secorrect, 1);
		semistake = soundPool.load(context, R.raw.semistake, 1);
		secomplete = soundPool.load(context, R.raw.secomplete, 1);

		// ウィンドウマネージャのインスタンス取得
		WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
		// ディスプレイのインスタンス生成
		Display disp = wm.getDefaultDisplay();
		// ディスプレイのサイズ取得
		width = disp.getWidth();
		height = disp.getHeight();
		// レベルを取得
		Intent intent = getIntent();
		level = intent.getIntExtra("LEVEL", -1);
		// 配列の生成＆初期化
		iroID = new int[level*level];
		angle = new float[level*level];
		addAngle = new float[level*level];
		for(int i = 0; i < level*level; i++) {
			iroID[i] = -1;
			angle[i] = 0;
			addAngle[i] = 0;
		}
		// テクスチャのサイズを計算
		if(width < height) {
			texsize = width/level;
		} else {
			texsize = height/level;
		}
		// テクスチャの初期貼り付け位置
		if(level%2 == 0) {
			texX = width/2 - texsize/2 - texsize*(level/2-1);
			texY = height/2 +texsize/2 + texsize*(level/2-1) - height/15;
		} else {
			texX = width/2 - texsize*(level/2);
			texY = height/2 + texsize*(level/2) - height/15;
		}

		// いろはをランダムな順番に生成
		startNum = irohaCreate();
		touchNum = startNum;
		TextureDrawer.TextureDrawerInit(level, texsize, iroID);

		// タイトルバーを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// GLSurfaceViewを作成
		gLSurfaceView = new GLSurfaceView(this);
		// レンダラーを生成してセット
		gLSurfaceView.setRenderer(this);

		// レイアウトのリソース参照は渡さず直接Viewオブジェクトを渡す
		setContentView(gLSurfaceView);
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		// タッチされた瞬間
		case MotionEvent.ACTION_DOWN:
			touchX = (float)event.getX();
			touchY = (float)(height - event.getY());
			break;
		}
		return true;
	}

	// 描画のために毎フレーム呼び出される
	public void onDrawFrame(GL10 gl) {
		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// 背景を描画
		TextureDrawer.drawTexture(gl, backgroundID, width/2, height/2, width, height, 0.0f, 1.0f, 1.0f);

		if(gameState == READY) {
			readyDraw(gl);
		} else {
			irohaDraw(gl);
			if (gameState == END) {
				endDraw(gl);
			}
		}
	}

	// 「よーい」の状態の描画
	private void readyDraw(GL10 gl) {
		if(readyStartTime == -1) {
			readyStartTime = System.currentTimeMillis();
		} else {
			readyEndTime = System.currentTimeMillis();
		}

		if (readyEndTime - readyStartTime < 2000) {
			TextureDrawer.drawTexture(gl, ready1ID, width/2, height/2, 256, 256, 0.0f, readyScale, readyScale);
			readyScale -= 0.02f;
			if (readyScale <= 1.0f) {
				readyScale = 1.0f;
			}
		} else if (2000 < readyEndTime - readyStartTime && readyEndTime - readyStartTime < 2800) {
			TextureDrawer.drawTexture(gl, ready2ID, width/2, height/2, 256, 256, 0.0f, 1.0f, 1.0f);
		} else {
			// 開始時間を取得
			startTime = System.currentTimeMillis();
			gameState = PLAYING;
		}
	}

	// ゲーム中の描画
	private void irohaDraw(GL10 gl) {
		// 経過時間をセット
		numericSprite.setValue(sec);
		// 文字列描画
		labels.beginDrawing(gl, width, height);
		labels.draw(gl, 0, height*9/10 - labels.getHeight(labelTIME),labelTIME);
		labels.draw(gl, labels.getWidth(labelTIME) + numericSprite.width(), height*9/10 - labels.getHeight(labelTIME), labelDot);
		// 次のタッチパネル
		labels.draw(gl, width - labels.getWidth(labelNEXT) - texsize*0.7f, height*9/10 - labels.getHeight(labelNEXT), labelNEXT);
		labels.endDrawing(gl);
		for (int i = 0; i < level*level; i++) {
			if (touchNum == iroID[i]) {
				TextureDrawer.drawTexture(gl, frontTexID, width - texsize/2*0.7f, height*9/10 - texsize/2*0.5f, texsize, texsize, 0.0f, 0.5f, 0.5f, i);
				// テクスチャを薄く見せる
				TextureDrawer.drawTexture(gl, width - texsize/2*0.7f, height*9/10 - texsize/2*0.5f, texsize, texsize, 0.0f, 0.5f, 0.5f);
			}
		}
		// すべてのマスを明けるまでは経過時間を計算
		if(touchNum < startNum + level*level) {
			finishTime = calcPassTime();
		} else {
			gameState = END;
		}
		// 数値描画
		numericSprite.setValue(sec);
		secWidth = numericSprite.width();
		numericSprite.draw(gl, labels.getWidth(labelTIME), height*9/10 - labels.getHeight(labelTIME), width, height);
		numericSprite.setValue(dec);
		numericSprite.draw(gl, labels.getWidth(labelTIME) + secWidth + labels.getWidth(labelDot), height*9/10 - labels.getHeight(labelTIME), width, height);

		// 中心はx 160, y 240
		for(int i = 0; i < level*level; i++) {
			x = (float)(texsize*(i%level)+texX);
			y = (float)(-texsize*(i/level)+texY);
			if(x-texsize/2 < touchX && touchX < x+texsize/2 && y-texsize/2 < touchY && touchY < y+texsize/2) {
				// タッチする順番とマスが合っていたらひっくり返す
				if(touchNum == iroID[i] && angle[i] == 0.0f) {
					System.out.println("iroID " + iroID[i]);
					System.out.println("touchNum " + touchNum);
					soundPool.play(secorrect, (float)volume, (float)volume, 0, 0, 1.0f);
					addAngle[i] = 9.0f;
					touchNum++;
				} else {
					soundPool.play(semistake, (float)volume, (float)volume, 0, 0, 1.0f);
				}
				// XXX デバッグ用に
				/*
 				if(angle[i] == 0.0f) {
					soundPool.play(secorrect, (float)volume, (float)volume, 0, 0, 1.0f);
					addAngle[i] = 6.0f;
					touchNum++;
				}
				*/
				touchX = 0.0f;
				touchY = 0.0f;
			}

			// 裏側を向いている間にテクスチャを変更
			if(angle[i] >= 180.0f) {
				addAngle[i] = 0.0f;
			}
			angle[i] += addAngle[i];

			// テクスチャの描画(表)
			TextureDrawer.drawTexture(gl, frontTexID, x, y, texsize, texsize, angle[i], 0.9f, 0.9f, i);
			// テクスチャの描画(裏)
			TextureDrawer.drawTexture(gl, backTexID, x, y, texsize, texsize, 180.0f+angle[i], 0.9f, 0.9f, i);
		}
	}

	// ゲーム終了時の描画
	private void endDraw(GL10 gl) {
		if (finishStartTime == -1) {
			soundPool.play(secomplete, (float)volume, (float)volume, 0, 0, 1.0f);
			finishStartTime = System.currentTimeMillis();
		} else {
			finishEndTime = System.currentTimeMillis();
		}
		if (finishEndTime - finishStartTime > 800) {
			TextureDrawer.drawTexture(gl, finish1ID, finishMove, 64, 128, 128, 0.0f, finishScale, finishScale);
			TextureDrawer.drawTexture(gl, finish2ID, width - finishMove, 64, 128, 128, 0.0f, finishScale, finishScale);
			if (finishMove <= width/2 - 64 && finishScale >= 0.5f) {
				// 文字が小さくなっていく速度
				finishScale -= 0.03f;
				// 左右から中心に向かって移動する速度
				finishMove += 5;
			}
		}
		// 3秒経ったらランキング画面に遷移
		if(finishEndTime - finishStartTime > 2500) {
			// サウンドプールを開放
			soundPool.release();
			if(level > 3) {
				// インテントのインスタンス生成
				Intent intent = new Intent(TextureIroNum.this, IrohaniRanking.class);
				intent.putExtra("SCORE", finishTime);
				intent.putExtra("LEVEL", level);
				// 次画面のアクティビティ起動
				startActivity(intent);
				finish();
			} else {
				// 練習だったらランキング画面に行かずに終了
				finish();
			}
		}
	}

	// マスがランダムな順番で生成されるようにする。
	private int irohaCreate() {
		int startNum;
		int buff;
		int conf;
		int[] buffID;

		// 配列の生成＆初期化
		buffID = new int[level*level];
		for(int i = 0; i < buffID.length; i++) {
			buffID[i] = -1;
		}
		// スタート位置を決める
		while(true) {
			buff = (int)(Math.random()*100.0d)%48;
			if (buff < 48-buffID.length) {
				startNum = buff;
				break;
			}
		}
		// 一旦、スタート位置から連続する文字を格納
		for(int i = 0; i < buffID.length; i++) {
			buffID[i] = startNum + i;
		}
		// ランダムに16マス埋める
		for(int i = 0; i < buffID.length; i++) {
			while(true) {
				conf = 0;
				buff = (int)(Math.random()*100.0d)%(buffID.length);
				// 取得してきた値が被っていないことを判定
				for(int j = 0; j < buffID.length; j++) {
					if(iroID[j] == buffID[buff]) {
						conf = 1;
						break;
					}
				}
				// 重複していなければ値を格納
				if(conf == 0) {
					iroID[i] = buffID[buff];
					break;
				}
			}
		}
		return startNum;
	}

	// 経過時間の計測
	private long calcPassTime() {
		long endTime = System.currentTimeMillis();
		sec = (int)(endTime - startTime)/1000;
		dec = (int)((endTime - startTime)%1000/10);
		return endTime - startTime;
    }

	// サーフェイスのサイズ変更時に呼び出される
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// ビューボートをサイズに合わせてセットしなおす
		gl.glViewport(0, 0, width, height);
	// カメラ位置をセット
		GLU.gluLookAt(gl, 2.0f, 2.5f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

		// 射影行列を選択
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 現在選択されている行列(射影行列)に、単位行列をセット
		gl.glLoadIdentity();
		// 平行投影用のパラメータをセット
		GLU.gluOrtho2D(gl, 0.0f, width, 0.0f, height);
	}

	// サーフェイスが生成される際、または再生成される際に呼び出される
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// ディザを無効化
		gl.glDisable(GL10.GL_DITHER);
		// カラーとテクスチャ座標の補完精度を最も効率的なものに指定
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		// バッファ初期化時のカラー情報をセット
		gl.glClearColor(0, 0, 0, 1);
		// 片面表示を有効に
		gl.glEnable(GL10.GL_CULL_FACE);
		// カリング設定をCCWに
		gl.glFrontFace(GL10.GL_CCW);
		// 深度テストを有効に
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// スムースシェーディングにセット
		gl.glShadeModel(GL10.GL_FLAT);

		// リソース読み込み
		frontTexID = TextureLoader.loadTexture(gl, this, R.drawable.iroha);
		backTexID = TextureLoader.loadTexture(gl, this, R.drawable.iroha_ok);
		backgroundID = TextureLoader.loadTexture(gl, this, R.drawable.game_background);
		ready1ID = TextureLoader.loadTexture(gl, this, R.drawable.ready1);
		ready2ID = TextureLoader.loadTexture(gl, this, R.drawable.ready2);
		finish1ID = TextureLoader.loadTexture(gl, this, R.drawable.finish1);
		finish2ID = TextureLoader.loadTexture(gl, this, R.drawable.finish2);

		// 文字列を生成
		if (labels != null) {
			labels.shutdown(gl);
		} else {
			labels = new LabelMaker(true, 256, 128);
		}
		labels.initialize(gl);
		labels.beginAdding(gl);
		labelTIME = labels.add(gl, "時間：", labelPaint);
		labelDot = labels.add(gl, ".", labelPaint);
		labelNEXT = labels.add(gl, "次：", labelPaint);
		labels.endAdding(gl);
		// 数値文字列を生成
		if (numericSprite != null) {
			numericSprite.shutdown(gl);
		} else {
			numericSprite = new NumericSprite();
		}
		numericSprite.initialize(gl, labelPaint);
	}

	// ポーズ状態からの復旧時やアクティビティ生成時などに呼び出される
	protected void inResume() {
		super.onResume();
		gLSurfaceView.onResume();
	}

	// アクティビティ一時停止や終了時に呼び出される
	protected void onPause() {
		super.onPause();
		gLSurfaceView.onPause();
		// サウンドプールを開放する
		soundPool.release();
		// 一時停止した場合はタイトル画面に戻る
		finish();
	}
}
