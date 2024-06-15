package com.clara.clientportal.ui.webview

import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.clara.clientportal.BaseActivity
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ActivityWebViewBinding
import com.clara.clientportal.utils.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WebViewActivity : BaseActivity() {
    private lateinit var binding: ActivityWebViewBinding
    private lateinit var frameAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar(binding.lytToolbar.toolbar)
        setWebViewSettings(binding.webView)
        showLoaderView(true)
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.webViewClient = WebClient()
        binding.webView.loadUrl(getUrl())
    }

    private fun getUrl(): String {
        return intent?.getStringExtra(Constants.WEB_VIEW_URL) ?: ""
    }

    inner class WebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            showLoaderView(false)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            showLoaderView(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return false
    }

    private fun showLoaderView(isVisible: Boolean) {
        if (isVisible) {
            binding.lytProgressParent.lytProgress.visibility = View.VISIBLE
            binding.lytProgressParent.lytProgress.isEnabled = false
            binding.lytProgressParent.lytProgress.isClickable = false
            binding.lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
            frameAnimation = binding.lytProgressParent.imgProgress.background as AnimationDrawable
            frameAnimation.start()
        } else {
            binding.lytProgressParent.lytProgress.visibility = View.GONE
            if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
                frameAnimation.stop()
            }
        }
    }
}