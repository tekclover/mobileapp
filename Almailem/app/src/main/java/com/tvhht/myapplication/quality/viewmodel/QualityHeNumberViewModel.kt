package com.tvhht.myapplication.quality.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvhht.myapplication.picking.viewmodel.SingleLiveData
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.utils.DatabaseBuilder
import com.tvhht.myapplication.utils.DatabaseHelperImpl
import com.tvhht.myapplication.utils.WMSApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QualityHeNumberViewModel : ViewModel() {
    var heNumberLiveData: MutableLiveData<List<QualityListResponse>> = MutableLiveData()
    var deleteLiveData: SingleLiveData<Boolean> = SingleLiveData()
    var orderNo = ""
    var selectedHeNumber = ""
    fun getHeNumberList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbHelper =
                    DatabaseHelperImpl(DatabaseBuilder.getInstance(WMSApplication.getInstance().applicationContext))
                withContext(Dispatchers.Main) {
                    heNumberLiveData.value = dbHelper.getQAOrderList(orderNo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun deleteRecord(deleteRecord:String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbHelper =
                    DatabaseHelperImpl(DatabaseBuilder.getInstance(WMSApplication.getInstance().applicationContext))
                val deleteHeNo = async {
                    dbHelper.deleteHeNumber(deleteRecord)
                }
                deleteResponse(deleteHeNo.await())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun deleteResponse(deleteRecord: Int) {
        if (deleteRecord > 0) {
            viewModelScope.launch(Dispatchers.Main) {
                deleteLiveData.value = true
            }
        }
    }
}