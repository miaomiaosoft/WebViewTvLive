package com.hxh19950701.webviewtvlive.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.hxh19950701.webviewtvlive.R
import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.settings.SettingsManager

class ChannelPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ChannelPlayer"
    }

    private val webView: WebpageAdapterWebView
    private val channelBarView: ChannelBarView
    var activity: Activity? = null
    var channel: Channel? = null
        set(value) {
            if (field == value) return
            field = value
            if (value == null) {
                webView.loadUrl(WebpageAdapterWebView.URL_BLANK)
                channelBarView.requestDismiss()
            } else {
                webView.loadUrl(value.url)
                channelBarView.setCurrentChannelAndShow(value)
            }
        }
    var dismissAllViewCallback: (() -> Unit)? = null

    private val gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {

        override fun onDown(e: MotionEvent) = true

        override fun onShowPress(e: MotionEvent) = Unit

        override fun onSingleTapUp(e: MotionEvent) = performClick()

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float) = false

        override fun onLongPress(e: MotionEvent) = Unit

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float) = false

    })

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_channel_player, this)
        webView = findViewById(R.id.webView)
        channelBarView = findViewById(R.id.channelBarView)
        webView.apply {
            onPageFinished = { channelBarView.requestDismiss() }
            onProgressChanged = { channelBarView.setProgress(it) }
            @Suppress("DEPRECATION")
            onFullscreenStateChanged = {
                val visibility = if (it) SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_FULLSCREEN else SYSTEM_UI_FLAG_FULLSCREEN
                activity?.apply { window?.decorView?.systemUiVisibility = visibility }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (SettingsManager.isWebViewTouchable()) {
            dismissAllViewCallback?.invoke()
            super.dispatchTouchEvent(ev)
        } else {
            gestureDetector.onTouchEvent(ev)
        }
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }
}