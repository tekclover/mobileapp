package com.clara.clientportal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityResultContractDocument extends ActivityResultContract<String[], Uri> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String[] input) {
        return new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .setType("*/*")
                .putExtra(Intent.EXTRA_MIME_TYPES, input)
                .putExtra(Intent.EXTRA_LOCAL_ONLY, true);
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || resultCode != Activity.RESULT_OK) return null;
        return intent.getData();
    }
}
