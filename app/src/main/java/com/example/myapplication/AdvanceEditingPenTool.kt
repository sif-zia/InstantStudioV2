package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawBoxPayLoad
import io.ak1.drawbox.rememberDrawController
import kotlin.math.roundToInt
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.widget.bitmapToUri
import com.example.myapplication.widget.AdvancedEditingWidgets.calculateScaledDimensions
import com.example.myapplication.widget.AdvancedEditingWidgets.resizeBitmapWithAspectRatio
import com.example.myapplication.widget.AdvancedEditingWidgets.uriToBitmap
import com.example.myapplication.widget.AdvancedEditingWidgets.modifyBitmap
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.myapplication.widget.CommonAppBar

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
                    var isResized: Boolean by remember { mutableStateOf(false) }

                    val context: Context = LocalContext.current

//                    val displayMetrics = context.resources.displayMetrics
                    var maxWidth: Int = 350
                    var maxHeight: Int = 400
                    var screenWidth: Int by remember{ mutableStateOf(maxWidth)}  //displayMetrics.widthPixels
                    var screenHeight: Int by remember{ mutableStateOf(maxHeight) } //displayMetrics.heightPixels

                    val done = painterResource(R.drawable.baseline_check_24)
                    val cancel = painterResource(R.drawable.cancel_button)

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .background(Color.Gray),

                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CommonAppBar(title = "Pen Tool")
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
                                        sourceImgBitmap = sourceBitmap
                                        sourceImgBitmap = resizeBitmapWithAspectRatio(
                                            sourceImgBitmap, screenWidth, screenHeight
                                        )
                                    }, valueRange = 1f..30f, // Define the range of float values
                                    steps = 50 // Optional: Define the number of steps in the range
                                )
                                Text("Pen Size: ${penSize.roundToInt()}")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val width = if (!isResized) maxWidth else sourceImgBitmap.width.toFloat().pxToDp().toInt()
                        val height = if (!isResized) maxHeight else sourceImgBitmap.height.toFloat().pxToDp().toInt()
                        Box(
                            modifier = Modifier
                                .width(
                                    width.dp
                                )
                                .height(
                                    height.dp
                                )
                                .background(Color.Red)
                                .clipToBounds()
                                .drawWithContent {
                                    drawContent()
                                    if (!isResized) {
                                        screenWidth = size.width.toInt()
                                        screenHeight = size.height.toInt()
                                        sourceImgBitmap = resizeBitmapWithAspectRatio(
                                            sourceImgBitmap, size.width.toInt(), size.height.toInt()
                                        )
                                        isResized = true
                                    }
                                    drawImage(
                                        image = sourceImgBitmap.asImageBitmap()
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
                        ) {
                            val controller = rememberDrawController()

                            DrawBox(drawController = controller,
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

                            sourceImgBitmap =
                                modifyBitmap(sourceImgBitmap, drawPath, penColor, penSize)

                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
                                        .clickable { finalImage = sourceBitmap }
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
                                            color = Color.Black,
                                            fontSize = 10.sp,
//                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Justify,
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
                                        .clickable { finalImage = sourceImgBitmap }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = done,
                                            contentDescription = "Your Icon Description",
                                            modifier = Modifier
                                                .size(28.dp)
                                        )
                                        Text(
                                            text = "Done",
                                            color = Color.Black,
                                            fontSize = 10.sp,
//                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Justify,
                                            modifier = Modifier.padding(5.dp) // Add padding at the bottom
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Float.pxToDp(): Float = (this / (LocalContext.current.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
private fun Int.dpToPx(context: Context): Float = (this * context.resources.displayMetrics.density)
