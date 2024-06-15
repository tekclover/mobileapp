package com.clara.timekeeping.ui.logout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.DialogLogoutBinding
import com.clara.timekeeping.network.APIConstant
import com.clara.timekeeping.ui.login.LoginActivity
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.PreferenceHelper
import com.clara.timekeeping.utils.setEnable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FragmentLogout : DialogFragment {
    @Inject
    lateinit var commonUtils: CommonUtils

    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    private lateinit var binding: DialogLogoutBinding
    private val viewModel: LogoutViewModel by viewModels()
    private lateinit var frameAnimation: AnimationDrawable
    private var ctx: Context? = null

    constructor()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog)
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth =
            resources.getDimensionPixelSize(if (preferenceHelper.isTablet()) R.dimen.dp_500 else R.dimen.dp_300)
        dialog?.window?.setLayout(
            dialogWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLogoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObservers()
    }

    private fun setListener() {
        with(binding) {
            textYes.setOnClickListener {
                viewModel.getAuthToken(
                    Constants.MANAGEMENT_SERVICE,
                    APIConstant.CREATE_PUSH_NOTIFICATION_ID
                )
            }
            textNo.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun setObservers() {
        viewModel.apiFailureMutableLiveData.observe(this) {
            ctx?.let { it1 ->
                commonUtils.showToastMessage(
                    it1,
                    if (it.isNullOrEmpty()
                            .not()
                    ) it else this.resources.getString(R.string.api_failure_message)
                )
            }
        }
        viewModel.loaderMutableLiveData.observe(this) {
            with(binding) {
                if (it) {
                    lytProgressParent.lytProgress.visibility = View.VISIBLE
                    lytProgressParent.lytProgress.setEnable(false)
                    lytProgressParent.imgProgress.setBackgroundResource(R.drawable.progress_frame_animation)
                    frameAnimation =
                        lytProgressParent.imgProgress.background as AnimationDrawable
                    commonUtils.enableDisableViews(lytLogout, false)
                    frameAnimation.start()
                } else {
                    lytProgressParent.lytProgress.visibility = View.GONE
                    stopAnimation()
                    commonUtils.enableDisableViews(lytLogout, true)
                }
            }
        }
        viewModel.networkMutableLiveData.observe(this) {
            if (it) {
                ctx?.let { it1 ->
                    commonUtils.showToastMessage(it1, this.resources.getString(R.string.no_network))
                }
            }
        }
        viewModel.pushNotificationLiveData.observe(this) { response ->
            response?.let {
                preferenceHelper.clearSharedPreference()
                dismissAllowingStateLoss()
                ctx?.let {
                    it.startActivity(Intent(it, LoginActivity::class.java))
                    ActivityCompat.finishAffinity(it as Activity)
                }
            }
        }
    }

    private fun stopAnimation() {
        if (::frameAnimation.isInitialized && frameAnimation.isRunning) {
            frameAnimation.stop()
        }
    }
}