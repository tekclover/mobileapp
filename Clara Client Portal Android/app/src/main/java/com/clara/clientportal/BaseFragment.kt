package com.clara.clientportal

import androidx.fragment.app.Fragment
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var commonUtils: CommonUtils
}