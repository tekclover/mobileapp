package com.clara.clientportal.ui.quotation

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
import com.clara.clientportal.databinding.FragmentMatterBinding
import com.clara.clientportal.databinding.FragmentQuotationBinding
import com.clara.clientportal.ui.matter.adapter.MatterAdapter
import com.clara.clientportal.ui.quotation.adpter.QuotationAdapter
import com.clara.clientportal.ui.webview.WebViewActivity
import com.clara.clientportal.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class QuotationFragment : BaseFragment() {

    private lateinit var binding: FragmentQuotationBinding
    private val viewModel: QuotationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuotationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lytQuotation.textMatter.text= String.format(Locale.getDefault(),"%s",requireActivity().resources.getString(
            R.string.menu_initial_retainer))
        setObservers()
    }

    private fun setObservers(){
       viewModel.quotationMutableLiveData.observe(viewLifecycleOwner){
           if (it.isNullOrEmpty().not()){
               val linearLayoutManager = LinearLayoutManager(requireContext())
               binding.lytQuotation.matterList.layoutManager = linearLayoutManager
               binding.lytQuotation.matterList.addItemDecoration(
                   DividerItemDecoration(
                       requireContext(),
                       DividerItemDecoration.VERTICAL
                   )
               )
             //  val quotationAdapter = QuotationAdapter(it,::paymentClick,preferenceHelper, commonUtils)
              // binding.lytQuotation.matterList.adapter = quotationAdapter
           }
       }
    }

    private fun paymentClick(url:String){
        val intent= Intent(requireContext(),WebViewActivity::class.java)
        intent.putExtra(Constants.WEB_VIEW_URL,url)
        startActivity(intent)
    }
}