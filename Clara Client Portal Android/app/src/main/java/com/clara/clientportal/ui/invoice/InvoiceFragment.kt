package com.clara.clientportal.ui.invoice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.clara.clientportal.BaseFragment
import com.clara.clientportal.R
import com.clara.clientportal.databinding.FragmentInvoiceBinding
import com.clara.clientportal.ui.invoice.adapter.InvoiceAdapter
import com.clara.clientportal.ui.webview.WebViewActivity
import com.clara.clientportal.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class InvoiceFragment : BaseFragment() {

    private lateinit var binding: FragmentInvoiceBinding
    private val viewModel: InvoiceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lytInvoice.textMatter.text = String.format(
            Locale.getDefault(), "%s", requireActivity().resources.getString(
                R.string.menu_invoice
            )
        )
        setObservers()
    }

    private fun setObservers() {
        viewModel.invoiceMutableLiveData.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty().not()) {
                val linearLayoutManager = LinearLayoutManager(requireContext())
                binding.lytInvoice.matterList.layoutManager = linearLayoutManager
                binding.lytInvoice.matterList.addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
              //  val invoiceAdapter = InvoiceAdapter(it, ::inVoicePaymentClick,preferenceHelper,commonUtils)
              //  binding.lytInvoice.matterList.adapter = invoiceAdapter
            }
        }
    }

    private fun inVoicePaymentClick(url: String) {
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra(Constants.WEB_VIEW_URL, url)
        startActivity(intent)
    }
}