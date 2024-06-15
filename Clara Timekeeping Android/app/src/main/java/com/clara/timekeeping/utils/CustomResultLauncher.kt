package com.clara.timekeeping.utils

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract

class CustomResultLauncher<Input, Result> private constructor(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<Input, Result>
) {
    private val launcher: ActivityResultLauncher<Input>
    private var activityResultListener: ActivityResultListener<Result>? = null

    init {
        launcher =
            caller.registerForActivityResult(contract) { result: Result -> setActivityResult(result) }
    }

    fun launch(input: Input, activityResultListener: ActivityResultListener<Result>?) {
        if (activityResultListener != null) {
            this.activityResultListener = activityResultListener
        }
        if (input != null) {
            launcher.launch(input)
        }
    }

    private fun setActivityResult(result: Result) {
        if (activityResultListener != null) activityResultListener?.onActivityResult(result)
    }

    interface ActivityResultListener<O> {
        fun onActivityResult(result: O)
    }

    companion object {
        fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>
        ): CustomResultLauncher<Input, Result> {
            return CustomResultLauncher(caller, contract)
        }
    }
}