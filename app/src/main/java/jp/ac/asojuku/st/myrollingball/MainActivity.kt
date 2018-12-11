package jp.ac.asojuku.st.myrollingball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import jp.ac.asojuku.st.myrollingball.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener,SurfaceHolder.Callback{

    // プロパティ
    private var surfaceWidth:Int = 0; // 幅
    private var surfaceHeight:Int = 0; // 高さ

    private val radius = 50.0f; // ボールの半径
    private val coef = 1000.0f; // ボールの移動量を計算するための係数

    private var ballX:Float = 0f; // ボールの現在のX座標
    private var ballY:Float = 0f; // ボールの現在のY座標
    private var vx:Float = 0f; // ボールのX方向の加速度
    private var vy:Float = 0f; // ボールのY方向の加速度
    private var time:Long = 0L; // 前回の取得時間

    private var rectL:Float = 500.0f;
    private var rectT:Float = 500.0f;
    private var rectR:Float = 900.0f;
    private var rectB:Float = 680.0f;

//    private var rectL2:Float = 200f;
//    private var rectT2:Float = 1100f;
//    private var rectR2:Float = 300f;
//    private var rectB2:Float = 500f;

    private var ballX2:Float = 200f;
    private var ballY2:Float = 200f;
    private val radius2 = 50.0f;
    private var isTouched:Boolean = false;



    // val 定数
    // var 変数






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val holder = surfaceView.holder; // サーフェスホルダーを取得
        holder.addCallback(this); // サーフェスホルダーのコールバックに自クラスを追加
    }


    // 画面表示・再表示のライフサイクルイベント
    override fun onResume() {
        // 親クラスのonResume処理
        super.onResume()

//        // 自クラスのonResume処理
//        // センサーマネージャーをOSから取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
//
        // 加速度センサー(Accelerometer)を指定してセンサーマネージャーからセンサーを取得
        val  accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // リスナー登録して加速度センサーの監視を開始
        sensorManager.registerListener(
                this,   // イベントリスナー機能を持つインスタンス(自クラスのインスタンス)
                accSensor,  // 監視するセンサー(加速度センサー)
                SensorManager.SENSOR_DELAY_GAME // センサーの更新頻度
        )

        btn_reset.setOnClickListener{
            finish()
            startActivity(intent)
        }
    }

    // 画面が非表示の時のイベント
    override fun onPause() {
        super.onPause()

//        // センサーマネージャーを取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
//
//        // センサーマネージャーに登録したリスナーを解除(自分自身を解除)
        sensorManager.unregisterListener(this);


    }

    // 制度が変わった時のイベント
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    // センサーの値が変わった時のイベント
    override fun onSensorChanged(event: SensorEvent?) {
//        Log.d("TAG01","センサーが変わりました");
//        // イベントが何もなかったらそのままリターン
        if(event == null){ return; }
//
//        // センサーの値が変わったらログに出力
//        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
//            val str = "x = ${event.values[0].toString()}" +
//                    ", y = ${event.values[1].toString()}" +
//                    ", z = ${event.values[2].toString()}";
//            // テキストビューに表示
//            txvMain.text = str;
//
//        }
        // ボールの描画の計算処理
        if(time==0L){time = System.currentTimeMillis();} // 最初のタイミングは現在時刻を保存
        // イベントのセンサー種別の情報がアクセラメーター(加速度センサー)の時だけ以下の処理を実行
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            // センサーのx(左右),y(縦)の値を取得
            val x = event.values[0]*-1;
            val y = event.values[1];

            // 経過時間を計算(今の時間-前の時間 = 経過時間)
            var t = (System.currentTimeMillis() - time).toFloat();
            // 今の時間を「前の時間」として保存
            time = System.currentTimeMillis();
            t /= 1000.0f;
            // 移動距離を計算
            var dx = (vx*t) + (x*t*t)/2.0f;
            var dy = (vy*t) + (y*t*t)/2.0f;
            // メートルをピクセルのcmに補正してボールのx座標に差し込む
            ballX += (dx*coef);
            // メートルをピクセルのcmに補正してボールのy座標に差し込む
            ballY += (dy*coef);
            // 今の各方向の加速度を更新
            vx += (x*t);
            vy += (y*t);
            // 画面の端にきたら跳ね返る処理
            if((ballX - radius)<0 && vx<0){
                // 左にぶつかった時
                vx = -vx /1.5f;
                ballX = radius;
            }else if((ballX+radius)>surfaceWidth && vx>0){
                // 右にぶつかった時
                vx = -vx/1.5f;
                ballX = (surfaceWidth-radius);
            }
            // 上下
            if((ballY - radius)<0 && vy<0){
                // 下
                vy = -vy/1.5f;
                ballY = radius;
            }else if((ballY + radius)>surfaceHeight && vy>0){
                // 上
                vy = -vy/1.5f;
                ballY = surfaceHeight - radius;
            }
            // 障害物
            if((ballX + radius)>rectL &&

                    ballY + radius > rectT &&

                    ballX - radius < rectR &&

                    ballY - radius < rectB ) {
                // ぶつかった時
                vx = vx / 1.3f
                vy = vy / 1.3f
                // フラグ
                if (!isTouched) {
                    txt_ouen.text = "残念";
                    txt_gameover.setTextColor(Color.RED);
                    isTouched = true;
                    txt_gameover.text = "GAMEOVER";
                    val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                            as SensorManager
                    sensorManager.unregisterListener(this)
                }
            }


            // ゴール
            if((ballX + radius) > ballX2 - radius2 && // 左から

                    ballY + radius < ballY2 + radius2 && // 上

                    ballX - radius < ballX2 + radius2 && // 右

                    ballY + radius < ballY2 + radius2 ){ // 下
                // ぶつかった時
                vx = vx;
                vy = vy;
                // フラグ
                if(!isTouched) {
                    txt_ouen.text = "おめでとう";
                    txt_gameover.setTextColor(Color.RED);
                    isTouched = true;
                    txt_gameover.text = "CLEAR";
                    val sensorManager=this.getSystemService(Context.SENSOR_SERVICE)
                            as SensorManager
                    sensorManager.unregisterListener(this)
                }

            }

            // キャンバスに描画
            this.drawCanvas();
        }
    }

    // サーフェスが更新された時のイベント
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        // サーフェスの幅と高さをプロパティに保存しておく
        surfaceWidth = width;
        surfaceHeight = height;
        // ボールの初期位置を保存しておく
        ballX = (width/2).toFloat();
        ballY = (height).toFloat();
    }
    // サーフェスが破棄された時のイベント
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        // 加速度センサーの登録を解除する流れ
        // センサーマネージャーを取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        // センサーマネージャーを通じてOSからリスナー(自分自身)を登録解除
        sensorManager.unregisterListener(this);
    }
    // サーフェスが作成された時のイベント
    override fun surfaceCreated(holder: SurfaceHolder?) {
        // 加速度センサーのリスナーを登録する流れ
        // センサーマネージャーを取得
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        // センサーマネージャーから加速度センサーを取得
        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 加速度センサーのリスナーをOSに登録
        sensorManager.registerListener(
                this, // リスナー（自クラス）
                accSensor, // 加速度センサー
                SensorManager.SENSOR_DELAY_GAME // センシングの頻度
        )

    }

    // Surfaceのキャンバスに描画するメソッド
    private fun drawCanvas(){
        // キャンバスをロックして取得
        val canvas = surfaceView.holder.lockCanvas();
        // キャンバスの背景色を設定
        canvas.drawColor(Color.GREEN);
        // キャンバスに円を書いてボールにする
        canvas.drawCircle(ballX, // x座標
                ballY, // y座標
                (radius*1.02f), // 半径
                Paint().apply{color = Color.BLACK}); // 色
        // 障害物の四角
        canvas.drawRect(rectL,rectT,rectR,rectB,
                Paint().apply{color = Color.BLUE});

//        canvas.drawRect(rectL2,rectT2,rectR2,rectB2,
//                Paint().apply{color = Color.BLUE});

        canvas.drawCircle(ballX2,ballY2,radius,Paint().apply { color = Color.RED});

        // キャンバスをアンロックしてキャンバスを描画
        surfaceView.holder.unlockCanvasAndPost(canvas);
    }
    private fun reset(){
        ballX = surfaceWidth - radius;
        ballY = surfaceHeight - radius;
        vx = 0f // 加速度0
        vy = 0f
        isTouched = false;
        txt_gameover.text = "";
        txt_ouen.text = "頑張れ";
    }


}