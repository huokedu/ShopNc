package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.Vector;

import top.yokey.nsg.R;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.zxing.camera.CameraManager;
import top.yokey.nsg.zxing.decoding.CaptureActivityHandler;
import top.yokey.nsg.zxing.decoding.InactivityTimer;
import top.yokey.nsg.zxing.view.ViewfinderView;

public class ScanActivity extends Activity implements SurfaceHolder.Callback {

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;

    private Activity mActivity;
    private NcApplication mApplication;

    private TextView titleTextView;
    private ImageView backImageView;
    private ViewfinderView viewfinderView;

    private String charString;
    private boolean vibrateBoolean;
    private boolean playBeepBoolean;
    private MediaPlayer mMediaPlayer;
    private boolean hasSurfaceBoolean;
    private CaptureActivityHandler mHandler;
    private InactivityTimer mInactivityTimer;
    private Vector<BarcodeFormat> formatsVector;

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurfaceBoolean = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurfaceBoolean) {
            hasSurfaceBoolean = true;
            initCamera(holder);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            mApplication.finishActivity(mActivity);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_scan);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.mainSurfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurfaceBoolean) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        formatsVector = null;
        charString = null;

        playBeepBoolean = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeepBoolean = false;
        }
        initBeepSound();
        vibrateBoolean = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onCreate(new Bundle());
    }

    private void initView() {

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        backImageView = (ImageView) findViewById(R.id.backImageView);
        viewfinderView = (ViewfinderView) findViewById(R.id.mainViewfinderView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        hasSurfaceBoolean = false;
        titleTextView.setText("扫一扫");
        mInactivityTimer = new InactivityTimer(mActivity);
        CameraManager.init(mActivity);

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.finishActivity(mActivity);
            }
        });

    }

    private void initBeepSound() {
        if (playBeepBoolean && mMediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    public void handleDecode(Result res) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String result = res.getText();
        if (!TextUtil.isEmpty(result)) {
            //用户
            if (result.contains("[") && result.contains("]") && result.contains("uid:")) {
                String uid = result.substring(result.indexOf(":") + 1, result.indexOf("]"));
                mApplication.startChat(mActivity, uid);
                mApplication.finishActivity(mActivity);
            }
            //网址
            if (TextUtil.isUrlAddress(result)) {
                //自己的
                if (result.contains(mApplication.urlString)) {
                    //商品
                    if (result.contains("goods_id")) {
                        try {
                            String goods_id = result.substring(result.lastIndexOf("=") + 1, result.length());
                            mApplication.startGoods(mActivity, goods_id);
                            mApplication.finishActivity(mActivity);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //店铺
                    if (result.contains("store_id")) {
                        try {
                            String store_id = result.substring(result.indexOf("=") + 1, result.length());
                            mApplication.startStore(mActivity, store_id);
                            mApplication.finishActivity(mActivity);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Intent intent = new Intent(mActivity, BrowserActivity.class);
                intent.putExtra("model", "normal");
                intent.putExtra("link", result);
                mApplication.startActivity(mActivity, intent);
                mApplication.finishActivity(mActivity);
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeepBoolean && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (vibrateBoolean) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        if (mHandler == null) {
            mHandler = new CaptureActivityHandler(this, formatsVector, charString);
        }
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

}