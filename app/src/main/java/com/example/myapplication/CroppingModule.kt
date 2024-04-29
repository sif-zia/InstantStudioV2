package com.example.myapplication

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.myapplication.widget.CropSelectionWidgets.AppBar
import com.example.myapplication.widget.CropSelectionWidgets.CropFAB
import com.example.myapplication.widget.CropSelectionWidgets.CropifyOptionSelector
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.CropifyOption
import io.moyuru.cropify.rememberCropifyState
import java.io.File

import android.content.Intent
import com.example.myapplication.widget.bitmapToUri
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.FileOutputStream
import java.io.IOException

class CroppingModule : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri: Uri? = intent?.getParcelableExtra("imageUri")

        setContent {
            val cropifyState = rememberCropifyState()
            var cropifyOption by remember { mutableStateOf(CropifyOption()) }
            var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }

            val scaffoldState = rememberBottomSheetScaffoldState()

            croppedImage?.let {
                ImagePreviewDialog(bitmap = it, onDismissRequest = { croppedImage = null }) { returnedUri ->
                    // Pass the returnedUri back to the launcher activity
                    val resultIntent = Intent().apply {
                        putExtra("croppedImageUri", returnedUri)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish() // Finish the activity to return to the launcher
                }
            }


            BottomSheetScaffold(
                topBar = {
                    AppBar(
                        title = "Crop Image",
                        bottomSheetState = scaffoldState.bottomSheetState
                    )
                },
                content = { it ->
                    val context = LocalContext.current
                    if (imageUri != null) {
                        Cropify(
                            uri = imageUri,
                            state = cropifyState,
                            option = cropifyOption,
                            onImageCropped = { croppedImage = it },
                            onFailedToLoadImage = {
                                Toast.makeText(
                                    context,
                                    "Failed to load image",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        )
                    }
                },
                sheetContent = {
                    CropifyOptionSelector(
                        option = cropifyOption,
                        onOptionChanged = { cropifyOption = it },
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                },
                floatingActionButton = {
                    CropFAB(modifier = Modifier.navigationBarsPadding()) { cropifyState.crop() }
                },
                sheetPeekHeight = 0.dp,
                scaffoldState = scaffoldState,
                sheetBackgroundColor = androidx.compose.material.MaterialTheme.colors.surface,
                sheetContentColor = contentColorFor(androidx.compose.material.MaterialTheme.colors.surface),
                backgroundColor = androidx.compose.material.MaterialTheme.colors.background,
                contentColor = contentColorFor(backgroundColor = androidx.compose.material.MaterialTheme.colors.background),
                modifier = Modifier.fillMaxSize()
            )

        }
    }

    @Composable
    fun ImagePreviewDialog(
        bitmap: ImageBitmap,
        onDismissRequest: () -> Unit,
        onReturnImage: (Uri) -> Unit // Change the callback to return Uri
    ) {
        Dialog(onDismissRequest = onDismissRequest) {
            val context = LocalContext.current

            // Display the cropped image
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Cropped Image",
                    modifier = Modifier.fillMaxWidth()
                )
                // Action button to return the cropped image as Uri
                // Position the button in the lower-right corner
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp) // Adjust as needed
                        .align(Alignment.BottomEnd)
                ) {
                    Button(
                        onClick = {
                            // Convert ImageBitmap to Uri and pass it to the callback
                            val uri = bitmapToUri(context,bitmap)

                            onReturnImage(uri)
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Blue), // Change the background color as needed
                        contentPadding = PaddingValues(0.dp) // Remove default padding
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_check_24),
                            contentDescription = "Return Cropped Image Icon",
                            tint = Color.White // Adjust the icon color as needed
                        )
                    }
                }
            }
        }
    }
}