package com.clara.clientportal.ui.home

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.clara.clientportal.BaseFragment
import com.clara.clientportal.R
import com.clara.clientportal.databinding.FragmentHomeBinding
import com.clara.clientportal.enums.HomeMenuEnum
import com.clara.clientportal.model.FindClientGeneralResponse
import com.clara.clientportal.model.HomeMenu
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.ui.home.adapter.MenuAdapter
import com.clara.clientportal.utils.Constants
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeFragmentListener: HomeFragmentListener
    private var homeMenuList: MutableList<HomeMenu> = mutableListOf()
    private lateinit var homeAdapter: MenuAdapter
    private lateinit var frameAnimation: AnimationDrawable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeFragmentListener = context as HomeFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClientDetails()
        homeMenuList = getHomeMenuList()
        setObservers()
        homeMenuList =
            homeMenuList.filter { it.name != HomeMenuEnum.HOME.homeLandingMenu && it.name != HomeMenuEnum.LOGOUT.homeLandingMenu }
                .toMutableList()
        homeMenuList = homeMenuList.filter { it.classId != 1 }.toMutableList()

        val gridLayoutManager = GridLayoutManager(requireContext(), if (preferenceHelper.isTablet()) 3 else 2)
        binding.homeMenuList.layoutManager = gridLayoutManager
        homeAdapter = MenuAdapter(homeMenuList, false, ::itemClickListener,::onCountListener)
        binding.homeMenuList.adapter = homeAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAuthToken(Constants.SETUP_SERVICE, APIConstant.FIND_CLIENT_USER_NEW_ID)
        viewModel.getAuthToken(Constants.MANAGEMENT_SERVICE, APIConstant.NOTIFICATION_COUNT_ID)
    }

    private fun getHomeMenuList(): MutableList<HomeMenu> {
        val menuList: MutableList<HomeMenu> = mutableListOf()
        val titleList = activity?.resources?.getStringArray(R.array.home_items)
        val iconList = activity?.resources?.getStringArray(R.array.icons)
        if (titleList.isNullOrEmpty().not()) {
            for ((position, title) in titleList?.withIndex()!!) {
                val homeMenu = HomeMenu(
                    title,
                    iconList?.get(position)
                )
                menuList.add(homeMenu)
            }
        }
        return menuList
    }

    private fun itemClickListener(name: String) {
        homeFragmentListener.onClickMenu(name)
    }

    private fun setObservers() {
        viewModel.clientUserMutableLiveData.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty().not()) {
                val clientData = it.firstOrNull()
                preferenceHelper.setClientUserId(clientData?.clientUserId ?: "")
                val findCheckList = homeMenuList.find { it1 -> it1.name == HomeMenuEnum.CHECKLIST.homeLandingMenu }
                findCheckList?.let { checkListMenu ->
                    checkListMenu.classId = preferenceHelper.getClassId()
                }
               /* for (homeMenu in homeMenuList) {
                    when (homeMenu.name) {
                        HomeMenuEnum.MATTER.homeLandingMenu -> {
                            homeMenu.matterCount = clientData?.matter ?: 0
                        }

                        HomeMenuEnum.INITIAL_RETAINER.homeLandingMenu -> {
                            homeMenu.quotationCount = clientData?.quotation ?: 0
                        }

                        HomeMenuEnum.PAYMENT_PLAN.homeLandingMenu -> {
                            homeMenu.paymentPlanCount = clientData?.paymentPlan ?: 0
                        }

                        HomeMenuEnum.INVOICE.homeLandingMenu -> {
                            homeMenu.invoiceCount = clientData?.invoice ?: 0
                        }

                        HomeMenuEnum.CHECKLIST.homeLandingMenu -> {
                            homeMenu.documentsCount = clientData?.documents ?: 0
                            homeMenu.classId = preferenceHelper.getClassId()
                        }

                        HomeMenuEnum.DOCUMENT_UPLOAD.homeLandingMenu -> {
                            homeMenu.documentsCountUploadCount = clientData?.referenceField2 ?: "0"
                        }

                        HomeMenuEnum.RECEIPT_NO.homeLandingMenu -> {
                            homeMenu.receiptNoCount = clientData?.receiptNumber ?: 0
                        }
                    }
                }
                if (this::homeAdapter.isInitialized) {
                    homeAdapter.updateAdapter(homeMenuList)
                }*/
            }
            viewModel.notificationCountLiveData.observe(viewLifecycleOwner) { it1 ->
                it1?.let { notificationCount ->
                    preferenceHelper.setNotificationCount(notificationCount.overAllCount ?: 0)
                    homeFragmentListener.notificationCountUpdate()
                    for (homeMenu in homeMenuList) {
                        when (homeMenu.name) {
                            HomeMenuEnum.MATTER.homeLandingMenu -> {
                                homeMenu.matterCount = notificationCount.matterCount ?: 0
                            }

                            HomeMenuEnum.INITIAL_RETAINER.homeLandingMenu -> {
                                homeMenu.quotationCount =
                                    notificationCount.initialRetainerCount ?: 0
                            }

                            HomeMenuEnum.PAYMENT_PLAN.homeLandingMenu -> {
                                homeMenu.paymentPlanCount = notificationCount.paymentPlantCount ?: 0
                            }

                            HomeMenuEnum.INVOICE.homeLandingMenu -> {
                                homeMenu.invoiceCount = notificationCount.invoiceCount ?: 0
                            }

                            HomeMenuEnum.CHECKLIST.homeLandingMenu -> {
                                homeMenu.documentsCount = notificationCount.checkListCount ?: 0
                            }

                            HomeMenuEnum.DOCUMENT_UPLOAD.homeLandingMenu -> {
                                homeMenu.documentsCountUploadCount =
                                    notificationCount.documentUploadCount?.toString() ?: "0"
                            }

                            HomeMenuEnum.RECEIPT_NO.homeLandingMenu -> {
                                homeMenu.receiptNoCount = notificationCount.receiptNoCount ?: 0
                            }
                        }
                    }
                    if (this::homeAdapter.isInitialized) {
                        homeAdapter.updateAdapter(homeMenuList)
                    }
                }
            }
        }
        viewModel.loaderMutableLiveData.observe(viewLifecycleOwner) {
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
                    frameAnimation.start()
                }
            }
        }
        viewModel.networkMutableLiveDat.observe(viewLifecycleOwner) {
            if (it) {
                commonUtils.showToastMessage(
                    requireContext(),
                    this.resources.getString(R.string.no_network)
                )
            }
        }
        viewModel.apiFailureMutableLiveData.observe(viewLifecycleOwner) {
            if (it) {
                commonUtils.showToastMessage(
                    requireContext(),
                    this.resources.getString(R.string.api_failure_message)
                )
            }
        }

    }

    private fun setClientDetails() {
        try {
            val clientDetails = Gson().fromJson(
                preferenceHelper.getClientDetails(),
                FindClientGeneralResponse::class.java
            )
            clientDetails?.let {
                viewModel.emailId = it.emailId ?: ""
                with(binding) {
                    textName.text = String.format(
                        Locale.getDefault(),
                        "%s%s",
                        activity?.resources?.getString(R.string.hi),
                        it.firstNameLastName
                    )
                    textAddress.text = String.format(
                        Locale.getDefault(),
                        "%s%s%s%s%s%s%s",
                        (it.addressLine1 ?: ""),
                        "\n",
                        (it.city ?: ""),
                        ", ",
                        (it.state
                            ?: ""),
                        ", ",
                        (it.zipCode ?: "")
                    )
                    textEmail.text = it.emailId ?: ""
                    textPhone.text = it.contactNumber ?: ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onCountListener(menu:String){
        homeFragmentListener.onCountClick(menu)
    }

    interface HomeFragmentListener {
        fun onClickMenu(menuItem: String)
        fun onCountClick(menuItem: String)
        fun notificationCountUpdate()
    }
}