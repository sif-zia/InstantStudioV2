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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.SliderDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
                    var isSelectingColor: Boolean by remember { mutableStateOf(false) }

                    var maxWidth: Int = 350
                    var maxHeight: Int = 450
                    var screenWidth: Int by remember{ mutableStateOf(maxWidth)}  //displayMetrics.widthPixels
                    var screenHeight: Int by remember{ mutableStateOf(maxHeight) } //displayMetrics.heightPixels

                    val done = painterResource(R.drawable.editcheck)
                    val cancel = painterResource(R.drawable.editcancel)
                    val color_picker = painterResource(R.drawable.baseline_color_lens_24)

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
                                    .height(
                                        height.dp
                                    )
                                    .clipToBounds()
                                    .drawWithContent {
                                        drawContent()
                                        if (!isResized) {
                                            screenWidth = size.width.toInt()
                                            screenHeight = size.height.toInt()
                                            sourceImgBitmap = resizeBitmapWithAspectRatio(
                                                sourceImgBitmap,
                                                size.width.toInt(),
                                                size.height.toInt()
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
                        }
                        else {
                            Box(
                                modifier = Modifier
                                    .width(width.dp)
                                    .height(height.dp)
                                    .clipToBounds()
                                    .drawWithContent {
                                        drawContent()
                                        if (!isResized) {
                                            screenWidth = size.width.toInt()
                                            screenHeight = size.height.toInt()
                                            sourceImgBitmap = resizeBitmapWithAspectRatio(
                                                sourceImgBitmap,
                                                size.width.toInt(),
                                                size.height.toInt()
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
                        }
                        Row(modifier = Modifier.height(184.dp)) {
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
                                    steps = 50, // Optional: Define the number of steps in the range
                                    colors = SliderDefaults.colors(
                                        thumbColor = appbarColor, // Set the color of the thumb
                                        activeTrackColor = appbarColor, // Set the color of the active track
                                        inactiveTrackColor = Color.White // Set the color of the inactive track
                                    )
                                )
                                Text("Pen Size: ${penSize.roundToInt()}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                )
                            }
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .padding(18.dp)
                                .padding(bottom = 10.dp)
                                .clip(
                                    RoundedCornerShape(8.dp)
                                )
                        ) {

                            item {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(appbarColor)
                                        .clickable {
                                            if(!isSelectingColor)
                                                finalImage = sourceBitmap
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
                                        .height(60.dp)
                                        .width(80.dp)
                                        .background(appbarColor)
                                        .clickable { isSelectingColor = true }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(28.dp),
                                            painter = color_picker,
                                            contentDescription = null,
                                            tint = penColor
                                        )
                                        Text(
                                            text = "Change Color",
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
                                            if(!isSelectingColor)
                                                finalImage = sourceImgBitmap
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
                                            modifier = Modifier
                                                .size(28.dp)

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
                    }

                    if(isSelectingColor) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            HarmonyColorPicker(harmonyMode = ColorHarmonyMode.SHADES,
                                modifier = Modifier.size(200.dp),
                                onColorChanged = { color ->
                                    penColor = color.toColor()
                                })
                        }
                        Box( modifier = Modifier
                            .fillMaxSize().padding(top = 80.dp, end = 20.dp),
                        contentAlignment = Alignment.TopEnd)
                        {
                            Row(
                                modifier = Modifier
                                    .background(
                                        color = appbarColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable(onClick = { isSelectingColor = false }),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Done",
                                    color = Color.White,
                                    fontSize = 19.sp
                                )
                            }
                        }
                    }

                    CommonAppBar(title = "Pen Tool")
                }
            }
        }
    }
}

@Composable
fun Float.pxToDp(): Float = (this / (LocalContext.current.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
private fun Int.dpToPx(context: Context): Float = (this * context.resources.displayMetrics.density)
