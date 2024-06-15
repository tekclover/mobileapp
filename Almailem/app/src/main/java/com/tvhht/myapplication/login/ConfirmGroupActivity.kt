package com.tvhht.myapplication.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tvhht.myapplication.databinding.LoginGroupBinding
import com.tvhht.myapplication.home.HomePageActivity
import com.tvhht.myapplication.login.model.UserDetails
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class ConfirmGroupActivity : AppCompatActivity() {
    private lateinit var binding: LoginGroupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        branchSelection()
    }

    private fun branchSelection() {
        with(binding) {
            val userInfo = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).userInfo
            userInfo?.let {
                val adapter = BranchAdapter(this@ConfirmGroupActivity, it)
                selectionValue.adapter = adapter
            }
            btnConfirm.setOnClickListener {
                val plantDetails = selectionValue.selectedItem as UserDetails
                plantDetails.plantId?.let {
                    WMSSharedPref.getAppPrefs(
                        WMSApplication.getInstance().context
                    ).saveStringValue(PrefConstant.USER_DETAILS_INDEX, it)
                    loginSuccess()
                }
            }
        }
    }

    private fun loginSuccess() {
        val i = Intent(this@ConfirmGroupActivity, HomePageActivity::class.java)
        startActivity(i)
        finish()
    }
}