package com.example.myapplication.contract


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.example.myapplication.BackgroundModule

class LaunchBackgroundContract: ActivityResultContract<Uri, Uri?>() {
    override fun createIntent(context: Context, input: Uri): Intent {
        return Intent(context, BackgroundModule::class.java).apply {
            putExtra("imageUri", input)
        }
    }
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK) {
            intent?.getParcelableExtra("bgImageUri")
        } else {
            null
        }
    }
}