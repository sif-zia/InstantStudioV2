package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.widget.bitmapToUri
import com.example.myapplication.widget.AdvancedEditingWidgets.calculateScaledDimensions
import com.example.myapplication.widget.AdvancedEditingWidgets.cropImage
import com.example.myapplication.widget.AdvancedEditingWidgets.resizeBitmapWithAspectRatio
import com.example.myapplication.widget.AdvancedEditingWidgets.pasteBitmapOverAnother
import com.example.myapplication.widget.AdvancedEditingWidgets.uriToBitmap
import com.example.myapplication.widget.CommonAppBar
import java.io.ByteArrayOutputStream

class AdvanceEditingCloningTool : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUri: Uri? = intent?.getParcelableExtra("imageUri")
        var bitmap: Bitmap? = null

        imageUri?.let { uri ->
            bitmap = uriToBitmap(this, uri)
        }


        setContent {
            bitmap?.let {sourceBitmap ->
                MyApplicationTheme {
                    var isSelecting by remember { mutableStateOf(true) }
                    var selectionSize: Float by remember { mutableStateOf(15f) }
                    var sourceImgBitmap: Bitmap by remember { mutableStateOf(sourceBitmap) }

                    val context: Context = LocalContext.current
                    val displayMetrics = context.resources.displayMetrics
                    var screenWidth: Int by remember { mutableStateOf(displayMetrics.widthPixels)}
                    var screenHeight: Int by remember { mutableStateOf(displayMetrics.heightPixels)}
                    var isResized: Boolean by remember { mutableStateOf(false) }

                    val maxWidth: Int = 350
                    val maxHeight: Int = 450

                    var copyCoordinates by remember { mutableStateOf(Offset.Unspecified) }
                    var pasteCoordinates by remember { mutableStateOf(Offset.Unspecified) }
                    var croppedBitmap by remember { mutableStateOf(Bitmap.createBitmap(selectionSize.toInt(), selectionSize.toInt(), Bitmap.Config.ARGB_8888)) }

                    val done = painterResource(R.drawable.editcheck)
                    val cancel = painterResource(R.drawable.editcancel)
                    val reset = painterResource(R.drawable.reseticon)
                    val clone = painterResource(R.drawable.clone)
                    val select = painterResource(R.drawable.crop2)
                    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))

                    val gradientcolors = listOf(
                        Color.Transparent,  Color.Transparent, Color.Black.copy(alpha = 0.3f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgColor)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .background(brush = Brush.verticalGradient(gradientcolors)),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val width = if (!isResized) maxWidth else sourceImgBitmap.width.toFloat().pxToDp().toInt()
                        val height = if (!isResized) maxHeight else sourceImgBitmap.height.toFloat().pxToDp().toInt()
                        if(!isResized) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp)
                                    .height(height.dp)
                                    .clipToBounds()
                                    .drawWithContent {
                                        drawContent()
                                        screenWidth = size.width.toInt()
                                        screenHeight = size.height.toInt()
                                        if (!isResized) {
                                            sourceImgBitmap = resizeBitmapWithAspectRatio(
                                                sourceImgBitmap,
                                                screenWidth,
                                                screenHeight
                                            )
                                            isResized = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            )
                            {
                                Canvas(
                                    modifier = Modifier
                                        .width(
                                            sourceImgBitmap.width
                                                .toFloat()
                                                .pxToDp().dp
                                        )
                                        .height(
                                            sourceImgBitmap.height
                                                .toFloat()
                                                .pxToDp().dp
                                        )
                                        .clipToBounds()
                                        .pointerInput(isSelecting) {
                                            detectDragGestures { change, dragAmount ->
                                                if (!isSelecting) {
                                                    copyCoordinates += dragAmount
                                                    if (pasteCoordinates != Offset.Unspecified)
                                                        pasteCoordinates += dragAmount
                                                    change.consume()
                                                }
                                            }
                                        }
                                        .pointerInput(isSelecting)
                                        {
                                            detectTapGestures(onTap = { offset ->
                                                // Update tap coordinates
                                                if (isSelecting)
                                                    copyCoordinates = offset
                                                else
                                                    pasteCoordinates = offset
                                                Log.d("Drag Log", "$isSelecting")
                                            })
                                        }

                                ) {
                                    drawImage(
                                        image = sourceImgBitmap.asImageBitmap()
                                    )

                                    if (copyCoordinates != Offset.Unspecified) {
                                        drawRect(
                                            color = Color.Red,
                                            size = Size(selectionSize, selectionSize),
                                            topLeft = copyCoordinates,
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }

                                    if (pasteCoordinates != Offset.Unspecified && !isSelecting) {
                                        drawImage(
                                            image = croppedBitmap.asImageBitmap(),
                                            pasteCoordinates
                                        )

                                        drawRect(
                                            color = Color.Green,
                                            size = Size(selectionSize, selectionSize),
                                            topLeft = pasteCoordinates,
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }

                                }


                                if (pasteCoordinates != Offset.Unspecified && copyCoordinates != Offset.Unspecified) {
                                    val sourceImage = cropImage(
                                        sourceImgBitmap,
                                        copyCoordinates,
                                        selectionSize.toInt(),
                                        selectionSize.toInt()
                                    );
                                    sourceImgBitmap = pasteBitmapOverAnother(
                                        sourceImgBitmap,
                                        sourceImage,
                                        pasteCoordinates
                                    )
                                }

                            }
                        }
                        else
                        {
                            Box(
                                modifier = Modifier
                                    .width(width.dp)
                                    .height(height.dp)
                                    .clipToBounds()
                                    .drawWithContent {
                                        drawContent()
                                        screenWidth = size.width.toInt()
                                        screenHeight = size.height.toInt()
                                        if (!isResized) {
                                            sourceImgBitmap = resizeBitmapWithAspectRatio(
                                                sourceImgBitmap,
                                                screenWidth,
                                                screenHeight
                                            )
                                            isResized = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            )
                            {
                                Canvas(
                                    modifier = Modifier
                                        .width(
                                            sourceImgBitmap.width
                                                .toFloat()
                                                .pxToDp().dp
                                        )
                                        .height(
                                            sourceImgBitmap.height
                                                .toFloat()
                                                .pxToDp().dp
                                        )
                                        .clipToBounds()
                                        .pointerInput(isSelecting) {
                                            detectDragGestures { change, dragAmount ->
                                                if (!isSelecting) {
                                                    copyCoordinates += dragAmount
                                                    if (pasteCoordinates != Offset.Unspecified)
                                                        pasteCoordinates += dragAmount
                                                    change.consume()
                                                }
                                            }
                                        }
                                        .pointerInput(isSelecting)
                                        {
                                            detectTapGestures(onTap = { offset ->
                                                // Update tap coordinates
                                                if (isSelecting)
                                                    copyCoordinates = offset
                                                else
                                                    pasteCoordinates = offset
                                                Log.d("Drag Log", "$isSelecting")
                                            })
                                        }

                                ) {
                                    drawImage(
                                        image = sourceImgBitmap.asImageBitmap()
                                    )

                                    if (copyCoordinates != Offset.Unspecified) {
                                        drawRect(
                                            color = Color.Red,
                                            size = Size(selectionSize, selectionSize),
                                            topLeft = copyCoordinates,
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }

                                    if (pasteCoordinates != Offset.Unspecified && !isSelecting) {
                                        drawImage(
                                            image = croppedBitmap.asImageBitmap(),
                                            pasteCoordinates
                                        )

                                        drawRect(
                                            color = Color.Green,
                                            size = Size(selectionSize, selectionSize),
                                            topLeft = pasteCoordinates,
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }

                                }


                                if (pasteCoordinates != Offset.Unspecified && copyCoordinates != Offset.Unspecified) {
                                    val sourceImage = cropImage(
                                        sourceImgBitmap,
                                        copyCoordinates,
                                        selectionSize.toInt(),
                                        selectionSize.toInt()
                                    );
                                    sourceImgBitmap = pasteBitmapOverAnother(
                                        sourceImgBitmap,
                                        sourceImage,
                                        pasteCoordinates
                                    )
                                }

                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .height(128.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Slider(
                                value = selectionSize,
                                onValueChange = {
                                    selectionSize = it
                                },
                                valueRange = 15f..50f, // Define the range of float values
                                steps = 50, // Optional: Define the number of steps in the range
                                colors = SliderDefaults.colors(
                                    thumbColor = appbarColor, // Set the color of the thumb
                                    activeTrackColor = appbarColor, // Set the color of the active track
                                    inactiveTrackColor = Color.White // Set the color of the inactive track
                                )
                            )
                            Text("Selection Size: ${selectionSize.roundToInt()}",
                                color = Color.White,
                                fontSize = 18.sp,
                            )
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .padding(18.dp)
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {

                            item {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(appbarColor)
                                        .clickable {
                                            val resultIntent = Intent().apply {
                                                putExtra("editedImageUri", imageUri)
                                            }
                                            setResult(Activity.RESULT_OK, resultIntent)
                                            finish()
                                        }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = cancel,
                                            contentDescription = "Your Icon Description",
                                            modifier = Modifier
                                                .size(28.dp)
                                        )
                                        Text(
                                            text = "Cancel",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Justify,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                    }
                                }
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(appbarColor)
                                        .clickable {
                                            if (copyCoordinates != Offset.Unspecified) {
                                                isSelecting = !isSelecting
                                                pasteCoordinates = Offset.Unspecified
                                            }
                                        }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        if(!isSelecting) {
                                            Image(
                                                painter = clone,
                                                contentDescription = "Your Icon Description",
                                                modifier = Modifier
                                                    .size(28.dp)

                                            )
                                            Text(
                                                text = "Clone",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                        else {
                                            Image(
                                                painter = clone,
                                                contentDescription = "Your Icon Description",
                                                modifier = Modifier
                                                    .size(28.dp)

                                            )
                                            Text(
                                                text = "Select",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(appbarColor)
                                        .clickable {
                                            pasteCoordinates = Offset.Unspecified
                                        }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = reset,
                                            contentDescription = "Your Icon Description",
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Text(
                                            text = "Reset",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Justify,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                    }
                                }
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(appbarColor)
                                        .clickable {
                                            val returnedUri = bitmapToUri(
                                                context,
                                                sourceImgBitmap.asImageBitmap()
                                            )

                                            val resultIntent = Intent().apply {
                                                putExtra("editedImageUri", returnedUri)
                                            }
                                            setResult(Activity.RESULT_OK, resultIntent)
                                            finish()
                                        }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = done,
                                            contentDescription = "Your Icon Description",
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Text(
                                            text = "Done",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Justify,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    CommonAppBar(title = "Cloning Tool")
                }
            }
        }
    }
}

@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(15.dp)
    ) {

        TextButton(
            onClick = onClick,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color.LightGray.copy(0.5f))
        ) {
            Text(
                text = text,
                color = Color.Black,
                fontSize = 15.sp,
                fontFamily = merriFont,
                fontWeight = FontWeight.Thin,
                fontStyle = FontStyle.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}