package com.tvhht.myapplication.home.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.ListHomeCellsOutboundBinding
import com.tvhht.myapplication.outboundreturns.ReturnPickingListActivity
import com.tvhht.myapplication.picking.PickingListActivity
import com.tvhht.myapplication.quality.QualityListActivity
import com.tvhht.myapplication.quality.QualityOrderTrackingReportActivity
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class OutBound : Fragment() {
    private lateinit var binding: ListHomeCellsOutboundBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListHomeCellsOutboundBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countNo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).countInfo

        with(binding) {
            count1.text = countNo?.outboundCount?.picking.toString()
            count2.text = countNo?.outboundCount?.quality.toString()
            count3.text = countNo?.outboundCount?.reversals.toString()
            count4.text = countNo?.outboundCount?.tracking.toString()
        }
        showHideViews(true)
        setListener()
    }

    private fun setListener() {
        with(binding) {
            textOutbound.setOnClickListener {
                showHideViews(true)
            }
            textReport.setOnClickListener {
                showHideViews(false)
            }
            itemCell1.setOnClickListener {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 2)

                val myIntent = Intent(context, PickingListActivity::class.java)
                requireContext().startActivity(myIntent)
            }

            itemCell2.setOnClickListener {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 2)
                val myIntent = Intent(context, QualityListActivity::class.java)
                requireContext().startActivity(myIntent)

            }

            itemCell3.setOnClickListener {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 2)

                val myIntent = Intent(context, ReturnPickingListActivity::class.java)
                requireContext().startActivity(myIntent)
            }
            itemCell4.setOnClickListener {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 2)

                val myIntent = Intent(context, QualityOrderTrackingReportActivity::class.java)
                requireContext().startActivity(myIntent)
            }
        }
    }

    private fun showHideViews(isVisible: Boolean) {
        with(binding) {
            val userInfoSingle = WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).userInfoSingle

            itemCell1.visibility =
                if ((userInfoSingle?.picking == true) && isVisible) View.VISIBLE else View.GONE
            itemCell2.visibility =
                if ((userInfoSingle?.quality == true) && isVisible) View.VISIBLE else View.GONE
            itemCell3.visibility =
                if ((userInfoSingle?.customerReturn == true) && isVisible) View.VISIBLE else View.GONE
            itemCell4.visibility = if (isVisible) View.GONE else View.VISIBLE
            textOutbound.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isVisible) R.color.dark_grayish_blue else R.color.light_gray
                )
            )
            textReport.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isVisible) R.color.light_gray else R.color.dark_grayish_blue
                )
            )
        }
    }
}