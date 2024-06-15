package com.clara.timekeeping.ui.verifyotp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.clara.timekeeping.BaseActivity
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.ActivityVerifyOtpBinding
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.ui.summary.TimeTicketSummaryActivity
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.setEnable
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerifyOtpActivity : BaseActivity() {
    lateinit var binding: ActivityVerifyOtpBinding
    private val mViewModel: VerifyOtpViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable
    private lateinit var backPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var gson: Gson

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        initIntent()
        setListener()
        observeObservers()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun initIntent() {
        intent?.extras?.let {
            binding.tvEmail.text = it.getString(Constants.EMAIL, "") ?: ""
            mViewModel.userId = it.getString(Constants.USER_ID, "") ?: ""
        }
    }

    private fun setListener() {
        with(binding) {
            btnVerify.setOnClickListener {
                initApiCall()
            }
            tvResend.setOnClickListener {
                mViewModel.getAuthToken(Constants.SETUP_SERVICE, APIConstant.RESEND_OTP_ID)
            }
            otp4.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    initApiCall()
                }
                false
            }
            tvBack.setOnClickListener {
                backPressed()
            }
            otp1.addTextChangedListener(otpWatcher(1))
            otp2.addTextChangedListener(otpWatcher(2))
            otp3.addTextChangedListener(otpWatcher(3))
            otp4.addTextChangedListener(otpWatcher(4))
        }
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }
    }

    private fun observeObservers() {
        mViewModel.verifyOtpMutableLiveData.observe(this) {
            it?.let { response ->
                response.userId?.let { it1 -> preferenceHelper.setUserId(it1) }
                preferenceHelper.setLoginDetails(gson.toJson(response))
                mViewModel.getAuthToken(
                    Constants.MANAGEMENT_SERVICE,
                    APIConstant.CREATE_PUSH_NOTIFICATION_ID
                )
            }
        }
        mViewModel.resendOtpMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.otp_success_message)
                )
            }
        }

        mViewModel.apiFailureMutableLiveData.observe(this) {
            commonUtils.showToastMessage(
                this,
                if (it.isNullOrEmpty()
                        .not()
                ) it else this.resources.getString(R.string.api_failure_message)
            )
        }
        mViewModel.loaderMutableLiveData.observe(this) {
            with(binding) {
                if (it) {
                    lytProgressParent.lytProgress.visibility = View.VISIBLE
                    lytProgressParent.lytProgress.setEnable(false)
                    lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
                    frameAnimation =
                        lytProgressParent.imgProgress.background as AnimationDrawable
                    commonUtils.enableDisableViews(lytLoginBottom, false)
                    frameAnimation.start()
                } else {
                    lytProgressParent.lytProgress.visibility = View.GONE
                    stopAnimation()
                    commonUtils.enableDisableViews(lytLoginBottom, true)
                }
            }
        }
        mViewModel.networkMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(this, this.resources.getString(R.string.no_network))
            }
        }
        mViewModel.pushNotificationLiveData.observe(this) {
            val intent = Intent(this, TimeTicketSummaryActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun initApiCall() {
        if (isValid()) {
            mViewModel.otp = getOtp()
            mViewModel.getAuthToken(Constants.SETUP_SERVICE, APIConstant.VERIFY_EMAIL_OTP_ID)
        }
    }

    private fun isValid(): Boolean {
        when {
            getOtp().isEmpty() -> {
                commonUtils.showToastMessage(
                    this@VerifyOtpActivity,
                    this@VerifyOtpActivity.resources.getString(R.string.otp_empty_message)
                )
                return false
            }

            getOtp().length < 4 -> {
                commonUtils.showToastMessage(
                    this@VerifyOtpActivity,
                    this@VerifyOtpActivity.resources.getString(R.string.otp_valid_message)
                )
                return false
            }

            else -> {
                return true
            }
        }
    }

    private fun getOtp(): String =
        binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString()

    private fun otpWatcher(position: Int): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                when (position) {
                    1 -> if (count > 0) binding.otp2.requestFocus()
                    2 -> if (count > 0) binding.otp3.requestFocus() else binding.otp1.requestFocus()
                    3 -> if (count > 0) binding.otp4.requestFocus() else binding.otp2.requestFocus()
                    4 -> if (count <= 0) binding.otp3.requestFocus()
                }
            }
        }
    }

    private fun stopAnimation() {
        if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
            frameAnimation.stop()
        }
    }

    private fun backPressed() {
        stopAnimation()
        finish()
    }
}