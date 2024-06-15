package com.clara.clientportal.ui.signin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.clara.clientportal.BaseActivity
import com.clara.clientportal.BuildConfig
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ActivitySignInBinding
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.ui.home.HomeActivity
import com.clara.clientportal.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.isMobileOtp = true
        setListener()
        setObservers()
        if (preferenceHelper.isTablet().not()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        fetchFCMToken()
        setPrivacyPolicy()
    }

    private fun setListener() {
        binding.lytMobileOtp.setOnClickListener {
            clearOtp()
            viewModel.isMobileOtp = true
            binding.otp5.visibility = View.VISIBLE
            binding.otp6.visibility = View.VISIBLE

            binding.viewEmailOtp.setBackgroundColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_grey_low
                )
            )
            binding.tvEmailOtp.setTextColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_grey_low
                )
            )
            binding.viewMobileOtp.setBackgroundColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_devil_blue
                )
            )
            binding.tvMobileOtp.setTextColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_devil_blue
                )
            )
            binding.tvOtpHeader.text = getString(R.string.mobile_otp_header)
            binding.edTxtOtpEmail.hint = this@SignInActivity.resources.getString(R.string.mobile_no)
            binding.edTxtOtpEmail.inputType = InputType.TYPE_CLASS_PHONE
            setMobileNoWatcher(binding.edTxtOtpEmail)
            binding.edTxtOtpEmail.requestFocus()
        }

        binding.lytEmailOtp.setOnClickListener {
            clearOtp()
            viewModel.isMobileOtp = false
            binding.otp5.visibility = View.GONE
            binding.otp6.visibility = View.GONE
            binding.viewMobileOtp.setBackgroundColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_grey_low
                )
            )
            binding.tvMobileOtp.setTextColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_grey_low
                )
            )
            binding.viewEmailOtp.setBackgroundColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_devil_blue
                )
            )
            binding.tvEmailOtp.setTextColor(
                ContextCompat.getColor(
                    this@SignInActivity,
                    R.color.color_devil_blue
                )
            )
            binding.tvOtpHeader.text =
                this@SignInActivity.resources.getString(R.string.email_otp_header)
            binding.edTxtOtpEmail.hint = this@SignInActivity.resources.getString(R.string.email)
            binding.edTxtOtpEmail.inputType = InputType.TYPE_CLASS_TEXT
            setMobileNoWatcher(binding.edTxtOtpEmail)
            binding.edTxtOtpEmail.requestFocus()
        }
        binding.btnLogin.setOnClickListener {
            when {
                binding.edTxtOtpEmail.text.toString().isEmpty() && getOtp().isEmpty() -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        String.format(
                            this@SignInActivity.resources.getString(R.string.edit_text_and_otp_empty_message),
                            this@SignInActivity.resources.getString(if (viewModel.isMobileOtp) R.string.mobile_no else R.string.email_id)
                        )
                    )
                }

                binding.edTxtOtpEmail.text.toString().isEmpty() -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(if (viewModel.isMobileOtp) R.string.mobile_no_empty_message else R.string.email_empty_message)
                    )
                }

                binding.edTxtOtpEmail.text.toString()
                    .isNotEmpty() && (viewModel.isMobileOtp) && (binding.edTxtOtpEmail.text.toString().length < 12) -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(R.string.valid_mobile_message)
                    )
                }

                binding.edTxtOtpEmail.text.toString()
                    .isNotEmpty() && (viewModel.isMobileOtp.not()) && (commonUtils.isValidEmail(
                    binding.edTxtOtpEmail.text.toString()
                ).not()) -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(R.string.valid_email_message)
                    )
                }

                getOtp().isEmpty() -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(R.string.otp_empty_message)
                    )
                }

                getOtp().isNotEmpty() && viewModel.isMobileOtp && (getOtp().length < 6) -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(R.string.otp_empty_message)
                    )
                }

                getOtp().isNotEmpty() && !viewModel.isMobileOtp && (getOtp().length < 4) -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(R.string.otp_empty_message)
                    )
                }

                else -> {
                    viewModel.otp = getOtp()
                    viewModel.getAuthToken(
                        Constants.SETUP_SERVICE,
                        if (viewModel.isMobileOtp) APIConstant.VERIFY_MOBILE_OTP_ID else APIConstant.VERIFY_EMAIL_OTP_ID
                    )
                }
            }
        }
        binding.imgArrow.setOnClickListener {
            when {
                binding.edTxtOtpEmail.text.toString().isEmpty() -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(if (viewModel.isMobileOtp) R.string.mobile_no_empty_message else R.string.email_empty_message)
                    )
                }

                binding.edTxtOtpEmail.text.toString()
                    .isNotEmpty() && (viewModel.isMobileOtp) && (binding.edTxtOtpEmail.text.toString().length < 12) -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(R.string.valid_mobile_message)
                    )
                }

                binding.edTxtOtpEmail.text.toString()
                    .isNotEmpty() && (viewModel.isMobileOtp.not()) && (commonUtils.isValidEmail(
                    binding.edTxtOtpEmail.text.toString()
                ).not()) -> {
                    commonUtils.showToastMessage(
                        this@SignInActivity,
                        this@SignInActivity.resources.getString(R.string.valid_email_message)
                    )
                }

                else -> {
                    viewModel.mobileOrEmail = binding.edTxtOtpEmail.text.toString()
                    viewModel.getAuthToken(
                        Constants.SETUP_SERVICE,
                        if (viewModel.isMobileOtp) APIConstant.SENT_MOBILE_OTP_ID else APIConstant.SENT_EMAIL_OTP_ID
                    )
                }
            }
        }
        binding.otp1.addTextChangedListener(otpWatcher(1))
        binding.otp2.addTextChangedListener(otpWatcher(2))
        binding.otp3.addTextChangedListener(otpWatcher(3))
        binding.otp4.addTextChangedListener(otpWatcher(4))
        binding.otp5.addTextChangedListener(otpWatcher(5))
        binding.otp6.addTextChangedListener(otpWatcher(6))
        setMobileNoWatcher(binding.edTxtOtpEmail)
    }

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
                    4 -> if (count > 0) binding.otp5.requestFocus() else binding.otp3.requestFocus()
                    5 -> if (count > 0) binding.otp6.requestFocus() else binding.otp4.requestFocus()
                    6 -> if (count <= 0) binding.otp5.requestFocus()
                }
            }
        }
    }

    private fun setMobileNoWatcher(editText: EditText) {
        if (viewModel.isMobileOtp) {
            editText.addTextChangedListener(mobileNoWatcher())
            val filter = InputFilter { source, start, end, _, _, _ ->
                for (i in start until end) {
                    if (!Character.isDigit(source[i]) && source[i] != '-') {
                        return@InputFilter ""
                    }
                }
                null
            }
            editText.filters = arrayOf(filter, InputFilter.LengthFilter(12))
        } else {
            editText.addTextChangedListener(mobileNoWatcher())
            editText.filters = arrayOf(InputFilter.LengthFilter(200))
        }
    }

    private fun mobileNoWatcher(): TextWatcher {
        return object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (viewModel.isMobileOtp) {
                    s?.let {
                        if (it.length > 3 && it[3] != '-') {
                            it.insert(3, "-")
                        }

                        if (it.length > 7 && it[7] != '-') {
                            it.insert(7, "-")
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
    }

    private fun setObservers() {
        viewModel.sendOtpMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.otp_success_message)
                )
                binding.otp1.requestFocus()
            } else {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(if (viewModel.isMobileOtp) R.string.valid_mobile_message else R.string.valid_email_message)
                )
            }
        }

        viewModel.verifyOtpMutableLiveData.observe(this) {
            it?.clientId?.let { id ->
                preferenceHelper.setClientId(id)
            }
            it?.clientUserId?.let { userId ->
                preferenceHelper.setClientUserId(userId)
            }
            viewModel.getAuthToken(
                Constants.MANAGEMENT_SERVICE,
                APIConstant.FIND_CLIENT_GENERAL_ID
            )
        }

        viewModel.findClientGeneralMutableLiveData.observe(this) { clientList ->
            if (clientList.isNullOrEmpty().not()) {
                val findClientGeneral =
                    clientList.firstOrNull { preferenceHelper.getClientId() == it.clientId }
                if ((findClientGeneral?.contactNumber.equals(viewModel.mobileOrEmail)) || (findClientGeneral?.emailId.equals(
                        viewModel.mobileOrEmail
                    ))
                ) {
                    preferenceHelper.setClientGeneralDetails(Gson().toJson(findClientGeneral))
                    preferenceHelper.setClassId(findClientGeneral?.classId ?: -1)
                    /* startActivity(Intent(this, HomeActivity::class.java))
                     finishAffinity()*/
                    viewModel.getAuthToken(
                        Constants.MANAGEMENT_SERVICE,
                        APIConstant.CREATE_PUSH_NOTIFICATION_ID
                    )
                }
            }
        }

        viewModel.loaderMutableLiveData.observe(this) {
            if (it) {
                binding.lytProgressParent.lytProgress.visibility = View.VISIBLE
                binding.lytProgressParent.lytProgress.isEnabled = false
                binding.lytProgressParent.lytProgress.isClickable = false
                binding.lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
                frameAnimation =
                    binding.lytProgressParent.imgProgress.background as AnimationDrawable
                frameAnimation.start()
            } else {
                binding.lytProgressParent.lytProgress.visibility = View.GONE
                if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
                    frameAnimation.stop()
                }
            }
        }
        viewModel.networkMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(this, this.resources.getString(R.string.no_network))
            }
        }
        viewModel.apiErrorMutableLiveData.observe(this) {
            if (it) {
                commonUtils.showToastMessage(
                    this,
                    this.resources.getString(R.string.api_failure_message)
                )
            }
        }
        viewModel.apiFailureMutableLiveData.observe(this) {
            commonUtils.showToastMessage(
                this,
                if (it != null && it.error.isNullOrEmpty().not()) it.error
                    ?: "" else this.resources.getString(R.string.api_failure_message)
            )
        }
        viewModel.pushNotificationLiveData.observe(this) {
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()
        }
    }

    private fun getOtp(): String = when {
        viewModel.isMobileOtp -> {
            binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString() + binding.otp5.text.toString() + binding.otp6.text.toString()
        }

        else -> {
            binding.otp1.text.toString() + binding.otp2.text.toString() + binding.otp3.text.toString() + binding.otp4.text.toString()
        }
    }

    private fun clearOtp() {
        with(binding) {
            otp1.text = null
            otp2.text = null
            otp3.text = null
            otp4.text = null
            otp5.text = null
            otp6.text = null
            edTxtOtpEmail.text = null
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

    private fun setPrivacyPolicy() {
        val spannable = SpannableString(getString(R.string.privacy_policy))
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(BuildConfig.PRIVACY_POLICY_URL)
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            32,
            46,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this,
                    R.color.color_devil_blue
                )
            ), 32, 46, 0
        )
        with(binding) {
            txtPrivacyPolicy?.text = spannable
            txtPrivacyPolicy?.movementMethod = LinkMovementMethod.getInstance()
            txtPrivacyPolicy?.highlightColor = Color.TRANSPARENT
        }
    }
}