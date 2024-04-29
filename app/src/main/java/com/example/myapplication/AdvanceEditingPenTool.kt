package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawBoxPayLoad
import io.ak1.drawbox.rememberDrawController
import java.io.InputStream
import kotlin.math.roundToInt
import android.graphics.Canvas
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.widget.bitmapToUri
import com.example.myapplication.widget.AdvancedEditingWidgets.calculateScaledDimensions
import com.example.myapplication.widget.AdvancedEditingWidgets.cropImage
import com.example.myapplication.widget.AdvancedEditingWidgets.resizeBitmapWithAspectRatio
import com.example.myapplication.widget.AdvancedEditingWidgets.pasteBitmapOverAnother
import com.example.myapplication.widget.AdvancedEditingWidgets.uriToBitmap
import com.example.myapplication.widget.AdvancedEditingWidgets.modifyBitmap
import androidx.activity.ComponentActivity

class AdvanceEditingPenTool : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUri: Uri? = intent?.getParcelableExtra("imageUri")
        var bitmap: Bitmap? = null

        imageUri?.let { uri ->
            bitmap = uriToBitmap(this, uri)
        }


        setContent {
            var finalImage: Bitmap? by remember { mutableStateOf(null) }

            finalImage?.let { finalImage ->
                val context = LocalContext.current
                val returnedUri = bitmapToUri(context, finalImage.asImageBitmap())

                val resultIntent = Intent().apply {
                    putExtra("editedImageUri", returnedUri)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }

            bitmap?.let { sourceBitmap ->
                MyApplicationTheme {
                    var redoVisitbility by remember { mutableStateOf(false) }
                    var colorBarVisitbility by remember { mutableStateOf(false) }
                    var sizeBarVisitbility by remember { mutableStateOf(false) }
                    var penColor by remember { mutableStateOf(Color.Red) }
                    var penSize by remember { mutableStateOf(1f) }
                    var drawPath: DrawBoxPayLoad? by remember { mutableStateOf(null) }
                    var sourceImgBitmap: Bitmap by remember { mutableStateOf(sourceBitmap) }

                    val context: Context = LocalContext.current
                    val displayMetrics = context.resources.displayMetrics
                    val screenWidth = displayMetrics.widthPixels
                    val screenHeight = displayMetrics.heightPixels
                    val h: Float = sourceImgBitmap.height.toFloat()
                    val w: Float = sourceImgBitmap.width.toFloat()
                    val (scaledWidth, scaledHeight) = calculateScaledDimensions(
                        w, h, screenWidth, screenHeight
                    )

                    var imageWidth: Int = scaledWidth.toInt()
                    var imageHeight: Int = scaledHeight.toInt()
                    val controller = rememberDrawController()

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { finalImage = sourceImgBitmap }) {
                                Text("Done")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {


//                controller.changeBgColor(Color.Transparent)
                            DrawBox(drawController = controller,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(imageWidth.toFloat() / imageHeight.toFloat())
                                    .clipToBounds(),
                                bitmapCallback = { imageBitmap, error ->
                                    imageBitmap?.let {
//                                save(it.asAndroidBitmap())
                                    }

                                }) { undoCount, redoCount ->

                                sizeBarVisitbility = false
                                colorBarVisitbility = false
                                redoVisitbility = redoCount != 0
                                drawPath = controller.exportPath()
                            }

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(imageWidth.toFloat() / imageHeight.toFloat())
                                    .clipToBounds()
                            ) {
                                val canvasWidth = size.width.toInt()
                                val canvasHeight = size.height.toInt()

                                drawImage(
                                    image = sourceImgBitmap.asImageBitmap(),
                                    dstSize = IntSize(canvasWidth, canvasHeight)
                                )

                                drawPath?.let { drawPath ->
                                    for (pathLines in drawPath.path) {
                                        drawPoints(
                                            points = pathLines.points,
                                            pointMode = PointMode.Polygon,
                                            color = penColor,
                                            strokeWidth = penSize
                                        )
                                    }
                                }
                            }

                            sourceImgBitmap = resizeBitmapWithAspectRatio(
                                sourceImgBitmap, imageWidth, imageHeight
                            )
                            sourceImgBitmap =
                                modifyBitmap(sourceImgBitmap, drawPath, penColor, penSize)

                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.height(184.dp)) {
                            HarmonyColorPicker(harmonyMode = ColorHarmonyMode.SHADES,
                                modifier = Modifier.size(184.dp),
                                onColorChanged = { color ->
                                    penColor = color.toColor()
                                })

                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .height(168.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Slider(
                                    value = penSize, onValueChange = {
                                        penSize = it
                                        if (imageUri != null) {
                                            sourceImgBitmap = sourceBitmap
                                        }
                                    }, valueRange = 1f..30f, // Define the range of float values
                                    steps = 50 // Optional: Define the number of steps in the range
                                )
                                Text("Pen Size: ${penSize.roundToInt()}")
                            }
                        }
                    }
                }
            }
        }
    }
}