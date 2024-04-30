package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.widget.CommonAppBar
import com.example.myapplication.widget.CropSelectionWidgets.AppBar
import com.example.myapplication.widget.CropSelectionWidgets.CropFAB
import com.example.myapplication.widget.CropSelectionWidgets.CropifyOptionSelector
import com.example.myapplication.widget.bitmapToUri
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.CropifyOption
import io.moyuru.cropify.rememberCropifyState

class CroppingModule : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri: Uri? = intent?.getParcelableExtra("imageUri")

        setContent {
            val cropifyState = rememberCropifyState()
            var cropifyOption by remember { mutableStateOf(CropifyOption()) }
            var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }
            var isPreview by remember { mutableStateOf(false) }

            val scaffoldState = rememberBottomSheetScaffoldState()

            croppedImage?.let {
                isPreview = true
                ImagePreviewDialog(bitmap = it, onDismissRequest = { croppedImage = null ; isPreview = false}) { returnedUri ->
                    // Pass the returnedUri back to the launcher activity
                    isPreview = false
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
                        bottomSheetState = scaffoldState.bottomSheetState,
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
                    CropFAB(modifier = Modifier.navigationBarsPadding(), isPreview) { cropifyState.crop() }
                },
                sheetPeekHeight = 0.dp,
                scaffoldState = scaffoldState,
                sheetBackgroundColor = Color.DarkGray,
                sheetContentColor = contentColorFor(androidx.compose.material.MaterialTheme.colors.surface),
                backgroundColor = androidx.compose.material.MaterialTheme.colors.background,
                contentColor = contentColorFor(backgroundColor = androidx.compose.material.MaterialTheme.colors.background),
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
    @Composable
    fun ImagePreviewDialog(
        bitmap: ImageBitmap,
        onDismissRequest: () -> Unit,
        onReturnImage: (Uri) -> Unit // Change the callback to return Uri
    ) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val context = LocalContext.current
            Column(
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f))
            ) {
                CommonAppBar(title = "Crop Image Preview", modifier = Modifier.background(color = Color.DarkGray))
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                Column {
                    Box(
                        modifier = Modifier.fillMaxHeight(0.8f)
                    ) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Cropped Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )

                    }
                }
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.weight(0.2f))
                    LazyRow(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth()
                    ) {


                        item {
                            Spacer(modifier = Modifier.width(12.dp)) // Add space between buttons
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray.copy(0.5f))
                                    .padding(4.dp)
                                    .clickable {
                                        onDismissRequest()
                                    }
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.cancel_button),
                                        contentDescription = "Cancel",
                                        modifier = Modifier
                                            .size(28.dp)

                                    )
                                    Text(
                                        text = "Cancel",
                                        color = Color.Black,
                                        fontSize = 10.sp,
//                            fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(5.dp) // Add padding at the bottom
                                    )
                                }
                            }
                        }


                        item {
                            Spacer(modifier = Modifier.width(12.dp)) // Add space between buttons
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray.copy(0.5f))
                                    .padding(4.dp)
                                    .clickable {
                                        val uri = bitmapToUri(context, bitmap)
                                        onReturnImage(uri)
                                    }
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_check_24),
                                        contentDescription = "Done",
                                        modifier = Modifier
                                            .size(28.dp)

                                    )
                                    Text(
                                        text = "Done",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(5.dp) // Add padding at the bottom
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            }
        }
    }
}