package com.clara.clientportal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityResultContractImage extends ActivityResultContract<String, Uri> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String input) {
        return new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setAction(Intent.ACTION_GET_CONTENT)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                .setType(input);
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || resultCode != Activity.RESULT_OK) return null;
        return intent.getData();
    }
}
