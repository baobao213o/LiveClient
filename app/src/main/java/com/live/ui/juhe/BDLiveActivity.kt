package com.live.ui.juhe

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.MotionEvent
import android.view.WindowManager
import com.baidu.cloud.media.player.IMediaPlayer
import com.live.R
import com.live.base.BaseActivity
import com.live.utils.DeviceUtil
import com.live.view.BDCloudVideo.BDCloudVideoView
import com.live.view.toast
import kotlinx.android.synthetic.main.activity_bdlive.*

class BDLiveActivity : BaseActivity(), IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener, BDCloudVideoView.OnPlayerStateListener {

    private var url: String? = null

    private var bright = 0

    private var mAudioManager: AudioManager? = null

    private var isClickBack = false

    private var startY = 0f//手指按下时的Y坐标
    private var startX = 0f//手指按下时的X坐标


    private val screenBrightness: Int
        get() {
            var screenBrightness = 255
            try {
                screenBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            } catch (ignored: Exception) {

            }

            return screenBrightness
        }

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.statusBarColor = -0xd7d7d8
        }
        //防闪屏
        window.setFormat(PixelFormat.TRANSLUCENT)
        setContentView(R.layout.activity_bdlive)

        url = savedInstanceState?.getString("url") ?: intent.getStringExtra("url")

        bright = screenBrightness
        //        //初始化音频管理器
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        initPlayer()

        vv_activty_bdlive.setVideoPath(url)
        // 初始化好之后立即播放（您也可以在onPrepared回调中调用该方法）
        if (!vv_activty_bdlive.isPlaying) {
            vv_activty_bdlive.start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("url", url)
        super.onSaveInstanceState(outState)
    }


    private fun initPlayer() {
        //设置ak
        //        BDCloudVideoView.setAK("b98ce254325147ec83572bcf2abfc43d");
        //注册listener
        vv_activty_bdlive.setOnPreparedListener(this)
        vv_activty_bdlive.setOnCompletionListener(this)
        vv_activty_bdlive.setOnErrorListener(this)
        vv_activty_bdlive.setOnInfoListener(this)
        vv_activty_bdlive.setOnBufferingUpdateListener(this)
        vv_activty_bdlive.setOnPlayerStateListener(this)

        vv_activty_bdlive.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT)

        vv_activty_bdlive.setLogEnabled(false)
        //        vv_activty_bdlive.setDecodeMode(BDCloudMediaPlayer.DECODE_SW);
        vv_activty_bdlive.setMaxProbeTime(2000) // 设置首次缓冲的最大时长
        vv_activty_bdlive.setTimeoutInUs(1000000)
        // Options for live stream only
        //        vv_activty_bdlive.setMaxProbeSize(1 * 2048);
        //        vv_activty_bdlive.setMaxProbeTime(40); // 设置首次缓冲的最大时长
        //        vv_activty_bdlive.setMaxCacheSizeInBytes(32 * 1024);
        //        vv_activty_bdlive.setBufferTimeInMs(100);
        //        vv_activty_bdlive.toggleFrameChasing(true);
    }

    private fun setLight(brightness: Int) {
        val temp = bright + brightness
        if (temp < 0 || temp > 255) {
            return
        }
        bright = temp
        val lp = window.attributes
        lp.screenBrightness = bright * (1f / 255f)
        window.attributes = lp
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val screenWidth = vv_activty_bdlive.width
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                if (startY < DeviceUtil.dip2px(50f)) {
                    return super.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (startY < DeviceUtil.dip2px(50f)) {
                    return super.onTouchEvent(event)
                }
                val endY = event.y
                val distanceY = startY - endY
                if (startX > screenWidth / 2) {
                    //右边
                    //在这里处理音量
                    val FLING_MIN_DISTANCE = DeviceUtil.dip2px(3f).toDouble()
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP)
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP)
                    }
                } else {
                    //屏幕左半部分上滑，亮度变大，下滑，亮度变小
                    val FLING_MIN_DISTANCE = 10.0
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        setLight(10)
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_DISTANCE) {
                        setLight(-10)
                    }
                }
                startY = endY
            }
        }
        return super.onTouchEvent(event)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }

        val url = intent.getStringExtra("url")

        if (this.url == url) {
            vv_activty_bdlive.enterForeground()
            this.url = url
        } else {
            vv_activty_bdlive.setVideoPath(url)
            vv_activty_bdlive.start()
        }

    }

    override fun onRestart() {
        super.onRestart()
        if (isClickBack) {
            return
        }
        if (vv_activty_bdlive != null) {
            vv_activty_bdlive.enterForeground()
        }
    }

    override fun onResume() {
        super.onResume()
        isClickBack = false
        startBackground = 2
    }

    override fun onBackPressed() {
        var intent: Intent? = intent
        if (intent == null) {
            intent = Intent(this, AnchorListActivity::class.java)
        } else {
            val componentName = ComponentName(this, AnchorListActivity::class.java)
            intent.component = componentName
        }
        startActivity(intent)
        moveTaskToBack(isTaskRoot)
        isClickBack = true
        if (vv_activty_bdlive != null) {
            vv_activty_bdlive.stopPlayback()
            this.url = ""
            //后台60s自动关闭
            if (countDownTimer == null) {
                countDownTimer = object : CountDownTimer(60000, 30000) {
                    override fun onTick(millisUntilFinished: Long) {

                    }

                    override fun onFinish() {
                        finish()
                    }
                }
            }
            countDownTimer!!.start()
        }
    }


    override fun onStop() {
        // enterBackground should be invoke before super.onStop()
        if (isClickBack) {
            super.onStop()
            return
        }
        if (vv_activty_bdlive != null) {
            vv_activty_bdlive.enterBackground()
        }
        startBackground = 1
        super.onStop()
    }

    override fun onDestroy() {
        if (vv_activty_bdlive != null) {
            vv_activty_bdlive.stopPlayback() // 释放播放器资源
            vv_activty_bdlive.release() // 释放播放器资源和显示资源
        }
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
        startBackground = 0
        super.onDestroy()
    }


    override fun onInfo(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onError(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
        toast("出错啦出错啦出错啦")
        finish()
        return false
    }

    override fun onCompletion(mp: IMediaPlayer) {

    }

    override fun onPrepared(mp: IMediaPlayer) {

    }

    override fun onBufferingUpdate(mp: IMediaPlayer, percent: Int) {

    }

    override fun onPlayerStateChanged(nowState: BDCloudVideoView.PlayerState) {

    }

    companion object {
        //0 activity未启动 1 activity在后台 2 activity在前台
        var startBackground = 0
    }
}
