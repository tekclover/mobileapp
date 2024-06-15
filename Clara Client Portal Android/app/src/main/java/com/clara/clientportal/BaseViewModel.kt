package com.clara.clientportal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clara.clientportal.model.SetupServiceAuthResponse

open class BaseViewModel : ViewModel() {
    var authTokenMutableLiveData: MutableLiveData<SetupServiceAuthResponse> = MutableLiveData()
}