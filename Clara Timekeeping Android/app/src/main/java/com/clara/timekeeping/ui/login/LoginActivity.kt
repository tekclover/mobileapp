package com.clara.timekeeping.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import com.clara.timekeeping.BaseActivity
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.ActivityLoginBinding
import com.clara.timekeeping.ui.verifyotp.VerifyOtpActivity
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.setEnable
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    private val mViewModel: LoginViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setListener()
        observeObservers()
        fetchFCMToken()
    }

    private fun setListener() {
        with(binding) {
            btnLogin.setOnClickListener {
                initApiCall()
            }
            edtTxtPassword.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    initApiCall()
                }
                false
            }
        }
    }

    private fun initApiCall() {
        if (isValid()) {
            mViewModel.userId = binding.edtTxtUserId.text.toString()
            mViewModel.password = binding.edtTxtPassword.text.toString()
            mViewModel.getAuthToken()
        }
    }

    private fun isValid(): Boolean {
        with(binding) {
            when {
                edtTxtUserId.text.toString().isEmpty() && edtTxtPassword.text.toString()
                    .isEmpty() -> {
                    commonUtils.showToastMessage(
                        this@LoginActivity,
                        this@LoginActivity.resources.getString(R.string.user_id_password_empty_message)
                    )
                    return false
                }

                edtTxtUserId.text.toString().isEmpty() -> {
                    commonUtils.showToastMessage(
                        this@LoginActivity,
                        this@LoginActivity.resources.getString(R.string.user_id_empty_message)
                    )
                    return false
                }

                edtTxtPassword.text.toString().isEmpty() -> {
                    commonUtils.showToastMessage(
                        this@LoginActivity,
                        this@LoginActivity.resources.getString(R.string.password_empty_message)
                    )
                    return false
                }

                else -> {
                    return true
                }
            }
        }
    }

    private fun observeObservers() {
        mViewModel.loginMutableLiveDat.observe(this) {
            it?.let { response ->
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.otp_success_message)
                )
                val intent = Intent(this, VerifyOtpActivity::class.java)
                intent.putExtra(Constants.EMAIL, response.emailId ?: "")
                intent.putExtra(Constants.USER_ID, response.userId ?: "")
                startActivity(intent)
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
                    if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
                        frameAnimation.stop()
                    }
                    commonUtils.enableDisableViews(lytLoginBottom, true)
                }
            }
        }
        mViewModel.networkMutableLiveDat.observe(this) {
            if (it) {
                commonUtils.showToastMessage(this, this.resources.getString(R.string.no_network))
            }
        }
    }
    private fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                val token = task.result
                preferenceHelper.setFCMToken(token)
            }
    }
}