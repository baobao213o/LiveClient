package com.live.ui.juhe;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.cloud.media.player.IMediaPlayer;
import com.live.R;
import com.live.base.BaseActivity;
import com.live.utils.DeviceUtil;
import com.live.view.BDCloudVideo.BDCloudVideoView;
import com.live.view.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.prototypez.savestate.core.annotation.AutoRestore;

public class BDLiveActivity extends BaseActivity implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener,
        BDCloudVideoView.OnPlayerStateListener {

    @BindView(R.id.vv_activty_bdlive)
    BDCloudVideoView mVV;

    @AutoRestore("url")
    String url;

    private int bright = 0;

    private AudioManager mAudioManager;

    private boolean isClickBack = false;
    //0 activity未启动 1 activity在后台 2 activity在前台
    public static int startBackground = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(0xff282828);
        }
        //防闪屏
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_bdlive);
        ButterKnife.bind(this);

        url = getIntent().getStringExtra("url");

        bright = getScreenBrightness();
//        //初始化音频管理器
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        initPlayer();

        mVV.setVideoPath(url);
        // 初始化好之后立即播放（您也可以在onPrepared回调中调用该方法）
        if (!mVV.isPlaying()) {
            mVV.start();
        }
    }

    private float startY = 0;//手指按下时的Y坐标
    private float startX = 0;//手指按下时的X坐标


    private void initPlayer() {
        //设置ak
//        BDCloudVideoView.setAK("b98ce254325147ec83572bcf2abfc43d");
        //注册listener
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVV.setOnBufferingUpdateListener(this);
        mVV.setOnPlayerStateListener(this);

        mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);

        mVV.setLogEnabled(false);
//        mVV.setDecodeMode(BDCloudMediaPlayer.DECODE_SW);
        mVV.setMaxProbeTime(2000); // 设置首次缓冲的最大时长
        mVV.setTimeoutInUs(1000000);
        // Options for live stream only
//        mVV.setMaxProbeSize(1 * 2048);
//        mVV.setMaxProbeTime(40); // 设置首次缓冲的最大时长
//        mVV.setMaxCacheSizeInBytes(32 * 1024);
//        mVV.setBufferTimeInMs(100);
//        mVV.toggleFrameChasing(true);
    }


    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception ignored) {

        }
        return screenBrightness;
    }

    private void setLight(int brightness) {
        int temp = bright + brightness;
        if (temp < 0 || temp > 255) {
            return;
        }
        bright = temp;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = bright * (1f / 255f);
        getWindow().setAttributes(lp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int screenWidth = mVV.getWidth();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                if (startY < DeviceUtil.dip2px(50)) {
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY < DeviceUtil.dip2px(50)) {
                    break;
                }
                float endY = event.getY();
                float distanceY = startY - endY;
                if (startX > screenWidth / 2) {
                    //右边
                    //在这里处理音量
                    final double FLING_MIN_DISTANCE = DeviceUtil.dip2px(3);
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
                    }
                } else {
                    //屏幕左半部分上滑，亮度变大，下滑，亮度变小
                    final double FLING_MIN_DISTANCE = 10;
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        setLight(10);
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        setLight(-10);
                    }
                }
                startY = endY;
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mVV == null) {
            mVV = findViewById(R.id.vv_activty_bdlive);
            initPlayer();
        }
        String url = intent.getStringExtra("url");

        if (this.url.equals(url)) {
            mVV.enterForeground();
            this.url = url;
        } else {
            mVV.setVideoPath(url);
            mVV.start();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isClickBack) {
            return;
        }
        if (mVV != null) {
            mVV.enterForeground();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClickBack = false;
        startBackground = 2;
    }

    private CountDownTimer countDownTimer;

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (intent == null) {
            intent = new Intent(this, AnchorListActivity.class);
        } else {
            ComponentName componentName = new ComponentName(this, AnchorListActivity.class);
            intent.setComponent(componentName);
        }
        startActivity(intent);
        moveTaskToBack(isTaskRoot());
        isClickBack = true;
        if (mVV != null) {
            mVV.stopPlayback();
            this.url = "";
            //后台60s自动关闭
            if (countDownTimer == null) {
                countDownTimer = new CountDownTimer(60000, 30000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }
                };
            }
            countDownTimer.start();
        }
    }


    @Override
    protected void onStop() {
        // enterBackground should be invoke before super.onStop()
        if (isClickBack) {
            super.onStop();
            return;
        }
        if (mVV != null) {
            mVV.enterBackground();
        }
        startBackground = 1;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mVV != null) {
            mVV.stopPlayback(); // 释放播放器资源
            mVV.release(); // 释放播放器资源和显示资源
        }
        mVV = null;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        startBackground = 0;
        super.onDestroy();
    }


    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        ToastHelper.showToast("出错啦出错啦出错啦");
        finish();
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {

    }

    @Override
    public void onPrepared(IMediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }

    @Override
    public void onPlayerStateChanged(BDCloudVideoView.PlayerState nowState) {

    }
}
