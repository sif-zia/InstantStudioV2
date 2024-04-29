package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.widget.bitmapToUri
import com.example.myapplication.widget.AdvancedEditingWidgets.calculateScaledDimensions
import com.example.myapplication.widget.AdvancedEditingWidgets.cropImage
import com.example.myapplication.widget.AdvancedEditingWidgets.resizeBitmapWithAspectRatio
import com.example.myapplication.widget.AdvancedEditingWidgets.pasteBitmapOverAnother
import com.example.myapplication.widget.AdvancedEditingWidgets.uriToBitmap

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
                    val screenWidth = displayMetrics.widthPixels
                    val screenHeight = displayMetrics.heightPixels
                    val h: Float =  sourceImgBitmap.height.toFloat()
                    val w: Float =  sourceImgBitmap.width.toFloat()
                    val (scaledWidth, scaledHeight) = calculateScaledDimensions(w, h, screenWidth, screenHeight)

                    var copyCoordinates by remember { mutableStateOf(Offset(scaledWidth / 2, scaledHeight / 2)) }
                    var imageWidth: Int = scaledWidth.toInt()
                    var imageHeight: Int = scaledHeight.toInt()
                    var pasteCoordinates by remember { mutableStateOf(Offset.Unspecified) }
                    var croppedBitmap by remember { mutableStateOf(Bitmap.createBitmap(selectionSize.toInt(), selectionSize.toInt(), Bitmap.Config.ARGB_8888)) }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { isSelecting = !isSelecting; pasteCoordinates = Offset.Unspecified }) {
                                if (isSelecting)
                                    Text("Mode: Select")
                                else
                                    Text("Mode: Clone")
                            }

                            Button(onClick = {
                                Log.d("Done Pressed", "Final Image Bitmap Changed 1")
                                val returnedUri = bitmapToUri(context, sourceImgBitmap.asImageBitmap())

                                val resultIntent = Intent().apply {
                                    putExtra("editedImageUri", returnedUri)
                                }
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            }) {
                                Text("Done")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxSize(), contentAlignment = Alignment.Center) {

                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(sourceImgBitmap.width.toFloat() / sourceImgBitmap.height.toFloat())
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
                                    val canvasWidth = size.width.toInt()
                                    val canvasHeight = size.height.toInt()

                                    imageWidth = canvasWidth
                                    imageHeight = canvasHeight

                                    drawImage(
                                        image = sourceImgBitmap.asImageBitmap(),
                                        dstSize = IntSize(canvasWidth, canvasHeight)
                                    )

                                    drawRect(
                                        color = Color.Red,
                                        size = Size(selectionSize, selectionSize),
                                        topLeft = copyCoordinates,
                                        style = Stroke(width = 2.dp.toPx())
                                    )

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

                                if (pasteCoordinates != Offset.Unspecified) {
                                    sourceImgBitmap = resizeBitmapWithAspectRatio(sourceImgBitmap, imageWidth, imageHeight)
                                    val sourceImage = cropImage(sourceImgBitmap, copyCoordinates, selectionSize.toInt(), selectionSize.toInt());
                                    sourceImgBitmap = pasteBitmapOverAnother(sourceImgBitmap, sourceImage, pasteCoordinates)
                                }
                            }


                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .height(168.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Slider(
                                value = selectionSize,
                                onValueChange = {
                                    selectionSize = it
                                },
                                valueRange = 15f..50f, // Define the range of float values
                                steps = 50 // Optional: Define the number of steps in the range
                            )
                            Text("Selection Size: ${selectionSize.roundToInt()}")

                            Button(onClick = { pasteCoordinates = Offset.Unspecified }) {
                                Text("Reset Selection")
                            }
                        }

                    }
                }
            }
        }
    }
}
