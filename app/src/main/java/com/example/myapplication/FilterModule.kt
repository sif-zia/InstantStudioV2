
package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.widget.CommonAppBar

var allowDoubleUndo: Boolean = false
var currentFilter: Int? = null

class FilterModule : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var imageUri: Uri? = intent?.getParcelableExtra("imageUri")
        var tempImageUri:Uri? = intent?.getParcelableExtra("imageUri")
        setContent{
            val context = LocalContext.current
            val filterStack = remember { mutableListOf<Int?>() }
            var currentFilter by remember { mutableStateOf<Int?>(null) }
            val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
            val lightImage = remember { mutableStateOf(false) }
            val DarkImage = remember { mutableStateOf(false) }
            val MediumImage = remember { mutableStateOf(false) }
            val painter = painterResource(id = R.drawable.b1)
            var showDialog by remember { mutableStateOf(false) }
            val filtericon = painterResource(R.drawable.filtersicon)
            val undoicon = painterResource(R.drawable.undoicon)
            val reseticon = painterResource(R.drawable.reseticon)
            val autoapplyicon = painterResource(R.drawable.autoapply)
            val cancel_image = painterResource(R.drawable.cancel_button)
            val done_image = painterResource(R.drawable.baseline_check_24)
            val gradientcolors = listOf(
                Color.Transparent,  Color.Transparent, Color.Black.copy(alpha = 0.3f)
            )
            var bgColor = Color(12,32,63)
            var appbarColor = Color(25,56,106)

            if (showDialog) {
                Dialog(
                    onDismissRequest = { showDialog = false }
                ) {
                    // The outermost container defines the dialog's background color and shape
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.Black
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp) // Padding around column content
                                .heightIn(max = 400.dp) // Set a maximum height to limit the dialog size
                        ) {
                            // Scrollable list of filters
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f) // This makes LazyColumn take up all available space
                            ) {
                                itemsIndexed(
                                    listOf("Inverse", "Brightness", "Posterize", "Black & White", "Desaturation", "Contrast Reduction", "Color Tint", "Red Boost", "Green Boost", "Blue Boost", "Saturation", "Night Vision", "Shadow Lift", "Grayscale Average", "Highlight", "Low Light", "Cyanotype", "Old Photo", "Channel Mix", "Partial Color Enhance", "Soft Light", "Aqua Boost", "Sepia Tone", "Infrared", "Lomo Camera Effect", "Cross-Channel Enhancement", "Vibrance", "Soft Transparency", "Enhanced Brightness", "Balanced Saturation"),
                                    key = { _, filter -> filter.hashCode() }
                                ) { index, filterName ->
                                    val filterImage = painterResource(id = getResourceIdForFilter(filterName))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                currentFilter = index + 1
                                                filterStack.add(currentFilter)
                                                allowDoubleUndo = true
                                                showDialog = false
                                            }
                                            .background(Color.DarkGray)
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = filterImage,
                                            contentDescription = filterName,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .padding(end = 8.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        Text(
                                            text = filterName,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                }
                            }

                            // Close button at the bottom
                            Button(
                                onClick = { showDialog = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp), // Space above the button
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray) // Dark themed background
                            ) {
                                Text("Close", color = Color.White) // White text
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)

            )
            CommonAppBar(title = "Filters", modifier = Modifier.background(color = Color.DarkGray))


            Box(modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(gradientcolors))) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp), // Add vertical scroll
                    verticalArrangement = Arrangement.Top, // Align the content at the top
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    bitmapState.value?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .padding(top = 100.dp)
                                .height(500.dp)
                        )
                    }
                    imageUri?.let { uri->
                        LaunchedEffect(currentFilter) {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                            bitmapState.value = applyColorFilter(originalBitmap, currentFilter)
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
               // Spacer(modifier = Modifier.weight(0.2f).width(200.dp))
                 val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                 val halfScreenWidth = (screenWidth / 2)
                 Spacer(modifier = Modifier.weight(0.2f).width(halfScreenWidth*4))
                //First row, where cancel and done buttons should be
                LazyRow(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = halfScreenWidth/2 +24.25.dp, bottom = 13.dp)
                ) {
                    //Cancel Button
                    item {
                        Spacer(modifier = Modifier.width(12.dp)) // Add space between buttons
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(0.5f))
                                .padding(4.dp)
                                .clickable {
                                    bitmapState.value?.let {
                                        if (it != null) {
                                            imageUri = getImageUri(context, it)
                                            val resultIntent = Intent().apply {
                                                putExtra("filterImageUri", tempImageUri)
                                            }
                                            setResult(RESULT_OK, resultIntent)
                                            finish()
                                        } else {
                                            Log.e("Filters", "Bitmap is null, cannot navigate to EditingScreen")
                                        }
                                    }
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = cancel_image,
                                    contentDescription = "Your Icon Description",
                                    modifier = Modifier
                                        .size(20.dp)
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
                    //Proceed/Done button
                    item {
                        Spacer(modifier = Modifier.width(12.dp)) // Add space between buttons
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(0.5f))
                                .padding(4.dp)
                                .clickable {
                                    bitmapState.value?.let {
                                        if (it != null) {
                                            imageUri = getImageUri(context, it)
                                            val resultIntent = Intent().apply {
                                                putExtra("filterImageUri", imageUri)
                                            }
                                            setResult(RESULT_OK, resultIntent)
                                            finish()
                                        } else {
                                            Log.e("Filters", "Bitmap is null, cannot navigate to EditingScreen")
                                        }
                                    }
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = done_image,
                                    contentDescription = "Your Icon Description",
                                    modifier = Modifier
                                        .size(20.dp)
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

                //Second row, where the 4 buttons, undo,autoapply, filters , reset button will be
                LazyRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(18.dp).padding(bottom = 10.dp).clip(RoundedCornerShape(8.dp)).align(Alignment.CenterHorizontally)
                ) {

                    item {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(appbarColor)
                                .clickable {
                                    if(allowDoubleUndo) {
                                        currentFilter=undoFilter(filterStack)
                                        allowDoubleUndo=false
                                    }
                                    currentFilter= undoFilter(filterStack)
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = undoicon,
                                    contentDescription = "Your Icon Description",
                                    modifier = Modifier
                                        .size(23.dp)

                                )
                                Text(
                                    text = "Undo",
                                    color = Color.White,
                                    fontSize = 10.sp,
//                            fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.padding(5.dp) // Add padding at the bottom
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
                                    val inputStream =
                                        context.contentResolver.openInputStream(com.example.myapplication.imageUri!!)
                                    val bitmap = BitmapFactory.decodeStream(inputStream)
                                    var lightPixels = 0.00
                                    var darkPixels = 0.00
                                    val bitwiseBitmap =
                                        bitmap.copy(Bitmap.Config.ARGB_8888, true)
                                    for (x in 0 until bitwiseBitmap.width) {
                                        for (y in 0 until bitwiseBitmap.height) {
                                            val pixel = bitwiseBitmap.getPixel(x, y)
                                            val red = pixel shr 16 and 0xff
                                            val green = pixel shr 8 and 0xff
                                            val blue = pixel and 0xff
                                            val brightness =
                                                (maxOf(red, green, blue) + minOf(
                                                    red,
                                                    green,
                                                    blue
                                                )) / 2
                                            if (brightness > 127) {
                                                lightPixels += 1
                                            } else {
                                                darkPixels += 1
                                            }
                                        }
                                    }
                                    val resultedPixels = lightPixels / darkPixels
                                    if (resultedPixels > 1.05) {
                                        lightImage.value = true
                                        currentFilter = 5
                                        filterStack.add(5)
                                    } else if (resultedPixels < 0.4) {
                                        DarkImage.value = true
                                        currentFilter = 2
                                        filterStack.add(2)
                                    } else {
                                        MediumImage.value = true
                                        currentFilter = 11
                                        filterStack.add(11)
                                    }
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = autoapplyicon,
                                    contentDescription = "Your Icon Description",
                                    modifier = Modifier
                                        .size(23.dp)

                                )
                                Text(
                                    text = "Auto",
                                    color = Color.White,
                                    fontSize = 10.sp,
//                            fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.padding(5.dp) // Add padding at the bottom
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
                                    showDialog = true
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = filtericon,
                                    contentDescription = "Your Icon Description",
                                    modifier = Modifier
                                        .size(23.dp)

                                )
                                Text(
                                    text = "Filters",
                                    color = Color.White,
                                    fontSize = 10.sp,
//                            fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.padding(5.dp) // Add padding at the bottom
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
                                    currentFilter = null
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = reseticon,
                                    contentDescription = "Your Icon Description",
                                    modifier = Modifier
                                        .size(23.dp)

                                )
                                Text(
                                    text = "Reset",
                                    color = Color.White,
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

fun getResourceIdForFilter(filterName: String): Int {
    return when (filterName) {
        "Inverse" -> R.drawable.inverse
        "Brightness" -> R.drawable.brightnessfilter
        "Posterize" -> R.drawable.posterize
        "Black & White" -> R.drawable.black_and_white
        "Desaturation" -> R.drawable.desaturation
        "Contrast Reduction" -> R.drawable.contrast_reduction
        "Aqua Boost"->R.drawable.aqua_boost
        "Color Tint"->R.drawable.color_tint
        "Red Boost"->R.drawable.red_boost
        "Green Boost"->R.drawable.green_boost
        "Blue Boost"->R.drawable.blue_boost
        "Saturation"->R.drawable.saturationfilter
        "Night Vision"->R.drawable.night_vision
        "Shadow Lift"->R.drawable.shadow_lift
        "Grayscale Average"->R.drawable.grayscale_average
        "Highlight"->R.drawable.highlight
        "Low Light"->R.drawable.low_light
        "Cyanotype"->R.drawable.cyanotype
        "Old Photo"->R.drawable.old_photo
        "Channel Mix"->R.drawable.channel_mi
        "Partial Color Enhance"->R.drawable.partial_color_enhance
        "Soft Light"->R.drawable.soft_light
        "Sepia Tone"->R.drawable.sepia_tone
        "Infrared"->R.drawable.infrared
        "Lomo Camera Effect"->R.drawable.lomo_camera_effect
        "Cross-Channel Enhancement"->R.drawable.cross_channel_enhance
        "Vibrance"->R.drawable.vibrance
        "Soft Transparency"->R.drawable.soft_transperancy
        "Enhanced Brightness"->R.drawable.enhanced_brightness
        "Balanced Saturation"->R.drawable.balanced_saturation
        else -> R.drawable.inverse
    }
}

fun createColorFilter1(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        -1f, 0f, 0f, 0f, 255f,
        0f, -1f, 0f, 0f, 255f,
        0f, 0f, -1f, 0f, 255f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter2(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, 50f,
        0f, 1f, 0f, 0f, 50f,
        0f, 0f, 1f, 0f, 50f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter3(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.3f, 0.3f, 0.3f, 0f, 0f,
        0.59f, 0.59f, 0.59f, 0f, 0f,
        0.11f, 0.11f, 0.11f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter4(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.33f, 0.59f, 0.11f, 0f, 0f,
        0.33f, 0.59f, 0.11f, 0f, 0f,
        0.33f, 0.59f, 0.11f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter5(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.5f, 0f, 0f, 0f, 0f,
        0f, 0.5f, 0f, 0f, 0f,
        0f, 0f, 0.5f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter6(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f, 1f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter7(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f, 1f,
        0f, 0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter8(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0f, 0f, 1f, 0f, 0f,
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter9(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        1f, 0f, 0f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter10(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, -20f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 20f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter11(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1.5f, 0f, 0f, 0f, -75f,
        0f, 1.5f, 0f, 0f, -75f,
        0f, 0f, 1.5f, 0f, -75f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter12(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.1f, 0.4f, 0f, 0f, 0f,
        0.1f, 0.4f, 0f, 0f, 0f,
        0.1f, 0.4f, 0.5f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter13(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, -100f,
        0f, 0f, 1f, 0f, -100f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter14(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter15(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1.2f, 0f, 0f, 0f, 0f,
        0f, 1.2f, 0f, 0f, 0f,
        0f, 0f, 1.2f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter16(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.8f, 0f, 0f, 0f, 50f,
        0f, 0.8f, 0f, 0f, 50f,
        0f, 0f, 0.8f, 0f, 50f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter17(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.2f, 0.6f, 0.2f, 0f, 0f,
        0.2f, 0.6f, 0.2f, 0f, 0f,
        0.2f, 0.6f, 0.2f, 0f, 50f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter18(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, -10f,
        0f, 1f, 0f, 0f, -10f,
        0f, 0f, 1f, 0f, -40f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter19(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0f, 0.5f, 0.5f, 0f, 0f,
        0.5f, 0f, 0.5f, 0f, 0f,
        0.5f, 0.5f, 0f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter20(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1.5f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 0.5f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter21(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.5f, 0f, 0f, 0f, 100f,
        0f, 0.5f, 0f, 0f, 100f,
        0f, 0f, 0.5f, 0f, 100f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter22(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.6f, 0f, 0f, 0f, 0f,
        0f, 1.2f, 0f, 0f, 0f,
        0f, 0f, 1.2f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    ))
)
fun createColorFilter23(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter24(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0f, 0f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter25(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1.2f, 0f, 0f, 0f, 0f,
        0f, 1.5f, 0f, 0f, 0f,
        0f, 0f, 1.1f, 0f, -20f,
        0f, 0f, 0f, 1f, 0f))
)
fun createColorFilter26(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.8f, 0.2f, 0f, 0f, 0f,
        0.2f, 0.8f, 0f, 0f, 0f,
        0f, 0f, 0.8f, 0.2f, 0f,
        0f, 0f, 0f, 1f, 0f
    ))
)
fun createColorFilter27(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1.2f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 0.8f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    ))
)
fun createColorFilter28(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.9f, 0f, 0f, 0f, 0f,
        0f, 0.9f, 0f, 0f, 0f,
        0f, 0f, 0.9f, 0f, 0f,
        0f, 0.1f, 0f, 1f, 0f
    ))
)
fun createColorFilter29(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        1.1f, 0f, 0f, 0f, 0f,
        0f, 1.1f, 0f, 0f, 0f,
        0f, 0f, 1.1f, 0f, 0f,
        0f, 0f, 0f, 0.9f, 0f
    ))
)
fun createColorFilter30(): ColorFilter = ColorMatrixColorFilter(
    ColorMatrix(floatArrayOf(
        0.8f, 0.1f, 0.1f, 0f, 0f,
        0.1f, 0.8f, 0.1f, 0f, 0f,
        0.1f, 0.1f, 0.8f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    ))
)

fun applyColorFilter(bitmap: Bitmap?, filterIndex: Int?): Bitmap? {
    bitmap ?: return null
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    // Apply the appropriate color filter based on filterIndex
    val paint = Paint().apply {
        colorFilter = when (filterIndex) {
            1 -> createColorFilter1()
            2 -> createColorFilter2()
            3 -> createColorFilter3()
            4 -> createColorFilter4()
            5 -> createColorFilter5()
            6 -> createColorFilter6()
            7 -> createColorFilter7()
            8 -> createColorFilter8()
            9 -> createColorFilter9()
            10 -> createColorFilter10()
            11->createColorFilter11()
            12->createColorFilter12()
            13->createColorFilter13()
            14->createColorFilter14()
            15->createColorFilter15()
            16->createColorFilter16()
            17->createColorFilter17()
            18->createColorFilter18()
            19->createColorFilter19()
            20->createColorFilter20()
            21->createColorFilter21()
            22->createColorFilter22()
            23->createColorFilter23()
            24->createColorFilter24()
            25->createColorFilter25()
            26->createColorFilter26()
            27->createColorFilter27()
            28->createColorFilter28()
            29->createColorFilter29()
            30->createColorFilter30()
            else -> null
        }
    }
    Canvas(mutableBitmap).drawBitmap(mutableBitmap, 0f, 0f, paint)
    return mutableBitmap
}

fun undoFilter(filterStack: MutableList<Int?>) :Int?{
    if (filterStack.isNotEmpty()) {
        currentFilter = filterStack.removeLast()
    } else {
        currentFilter = null
    }
    return currentFilter

}