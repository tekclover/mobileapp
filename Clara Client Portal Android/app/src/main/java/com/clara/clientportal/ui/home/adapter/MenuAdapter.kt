package com.clara.clientportal.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.GridRowViewBinding
import com.clara.clientportal.databinding.MenuRowViewBinding
import com.clara.clientportal.enums.HomeMenuEnum
import com.clara.clientportal.model.HomeMenu

class MenuAdapter(
    private var menuList: List<HomeMenu>,
    private val isDrawerMenu: Boolean,
    private var onClickMenu: (name: String) -> Unit,
    private var onCountClick: (name: String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context? = null
    private val drawerMenu = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == drawerMenu) {
            mContext = parent.context
            val binding: MenuRowViewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.menu_row_view,
                parent,
                false
            )
            return DrawerMenuViewHolder(binding)
        } else {
            mContext = parent.context
            val binding: GridRowViewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.grid_row_view,
                parent,
                false
            )
            return HomeMenuViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                val homeMenuHolder = holder as HomeMenuViewHolder
                homeMenuHolder.bindHomeMenu()
            }

            1 -> {
                val drawerMenu = holder as DrawerMenuViewHolder
                drawerMenu.bindDrawerMenu()
            }
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (isDrawerMenu) {
            return drawerMenu
        }
        return 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: List<HomeMenu>) {
        this.menuList = updateList
        notifyDataSetChanged()
    }


    inner class DrawerMenuViewHolder(private val binding: MenuRowViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("DiscouragedApi")
        fun bindDrawerMenu() {
            with(binding) {
                val homeMenu = menuList[adapterPosition]
                menuName.text = homeMenu.name ?: ""
                mContext?.let {
                    val drawableResourceId: Int? =
                        it.resources?.getIdentifier(homeMenu.icon, "drawable", it.packageName)
                    drawableResourceId?.let { it1 -> binding.imgMenuIcon.setImageResource(it1) }
                }
                root.setOnClickListener {
                    onClickMenu(homeMenu.name ?: "")
                }
            }
        }
    }

    inner class HomeMenuViewHolder(private val binding: GridRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("DiscouragedApi")
        fun bindHomeMenu() {
            with(binding) {
                val homeMenu = menuList[adapterPosition]
                textTitle.text = homeMenu.name ?: ""
                mContext?.let {
                    val drawableResourceId: Int? =
                        it.resources?.getIdentifier(homeMenu.icon, "drawable", it.packageName)
                    drawableResourceId?.let { it1 -> binding.imgMatter.setImageResource(it1) }
                    lytCount.background =
                        ContextCompat.getDrawable(it, R.drawable.matter_count_background)
                    textTitle.setTextColor(
                        ContextCompat.getColor(
                            it,
                            R.color.color_blue_low
                        )
                    )
                    when (homeMenu.name) {
                        HomeMenuEnum.MATTER.homeLandingMenu -> {
                            textCount.text = homeMenu.matterCount?.toString()
                        }

                        HomeMenuEnum.INITIAL_RETAINER.homeLandingMenu -> {
                            textCount.text = homeMenu.quotationCount?.toString()
                        }

                        HomeMenuEnum.PAYMENT_PLAN.homeLandingMenu -> {
                            textCount.text = homeMenu.paymentPlanCount?.toString()
                        }

                        HomeMenuEnum.INVOICE.homeLandingMenu -> {
                            textCount.text = homeMenu.invoiceCount?.toString()
                        }

                        HomeMenuEnum.CHECKLIST.homeLandingMenu -> {
                            textCount.text = homeMenu.documentsCount?.toString()
                        }

                        HomeMenuEnum.DOCUMENT_UPLOAD.homeLandingMenu -> {
                            textCount.text = homeMenu.documentsCountUploadCount
                        }

                        HomeMenuEnum.RECEIPT_NO.homeLandingMenu -> {
                            textCount.text = homeMenu.receiptNoCount?.toString()
                        }
                    }
                }
                root.setOnClickListener {
                    onClickMenu(homeMenu.name ?: "")
                }
                lytCount.setOnClickListener {
                    onCountClick(homeMenu.name ?: "")
                }
            }
        }
    }
}