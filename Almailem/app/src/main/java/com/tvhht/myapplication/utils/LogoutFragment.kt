package com.tvhht.myapplication.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.DialogViewLogoutBinding
import com.tvhht.myapplication.login.LoginActivity
import com.tvhht.myapplication.pushnotification.PushNotificationViewModel

class LogoutFragment : DialogFragment() {
    private lateinit var binding: DialogViewLogoutBinding
    private lateinit var viewModel: PushNotificationViewModel
    private var ctx: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ReasonDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth =
            resources.getDimensionPixelSize(R.dimen.dp_350)
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
        binding = DialogViewLogoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setListener()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[PushNotificationViewModel::class.java]
    }

    private fun setListener() {
        with(binding) {
            buttonYes.setOnClickListener {
                progress.visibility = View.VISIBLE
                viewModel.getAuthToken(false)
            }
            buttonNo.setOnClickListener {
                dismissAllowingStateLoss()
            }

            viewModel.fcmTokenLiveData.observe(viewLifecycleOwner) {
                progress.visibility = View.GONE
                it?.let {
                    dismissAllowingStateLoss()
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().applicationContext
                    ).clearAllSharedPrefs()

                    val intent = Intent(ctx, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }
    }
}