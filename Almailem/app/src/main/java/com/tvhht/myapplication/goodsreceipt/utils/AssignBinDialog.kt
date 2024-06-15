package com.tvhht.myapplication.goodsreceipt.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.DialogGlobalInventoryBinding
import com.tvhht.myapplication.goodsreceipt.adapter.AssignBinAdapter
import com.tvhht.myapplication.goodsreceipt.model.HHTUser
import com.tvhht.myapplication.login.model.UserDetails


class AssignBinDialog(
    private val userList: List<UserDetails>,
    private val onUserSelected: (user: HHTUser) -> Unit
) :
    DialogFragment() {

    private lateinit var binding: DialogGlobalInventoryBinding
    private var ctx: Context? = null
    private var selectedUser: HHTUser? = null

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
        val dialogHeight =
            resources.getDimensionPixelSize(R.dimen.dp_450)
        dialog?.window?.setLayout(
            dialogWidth,
            dialogHeight
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogGlobalInventoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            textTitle.text = ctx?.resources?.getString(R.string.assign_binner)
            textCompanyId.text = ctx?.resources?.getString(R.string.hht_user_id)
            textBranchId.text = ctx?.resources?.getString(R.string.hht_user_name)
            textInvQty.text = ctx?.resources?.getString(R.string.sel)
            val layoutManager = LinearLayoutManager(ctx)
            globalInventoryList.layoutManager = layoutManager
            globalInventoryList.addItemDecoration(
                DividerItemDecoration(
                    ctx,
                    DividerItemDecoration.VERTICAL
                )
            )
            val hhtUserList: MutableList<HHTUser> = ArrayList()
            for (user in userList) {
                hhtUserList.add(HHTUser(user.userId, user.userName))
            }
            val adapter = AssignBinAdapter(hhtUserList) {
                selectedUser = it
            }
            globalInventoryList.adapter = adapter
            btnConfirm.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                dismissAllowingStateLoss()
            }
            btnConfirm.setOnClickListener {
                dismissAllowingStateLoss()
                selectedUser?.let { it1 -> onUserSelected.invoke(it1) }
            }
        }
    }
}