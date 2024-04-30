package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import java.io.ByteArrayOutputStream
import java.io.IOException
import androidx.compose.foundation.rememberScrollState
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.contract.LaunchAEModuleCloningToolContract
import com.example.myapplication.contract.LaunchAEModulePenToolContract
import com.example.myapplication.contract.LaunchBackgroundContract
import com.example.myapplication.contract.LaunchFilterModuleContract
import com.example.myapplication.contract.LaunchForegroundContract
import com.example.myapplication.contract.LaunchSelectionModuleContract
import java.io.InputStream
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.platform.LocalConfiguration
import com.example.myapplication.widget.CommonAppBar


var imageUri: Uri? = null
var bgColor = Color(12,32,63)
var fontColor = Color.White
var appbarColor = Color(25,56,106)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val painter = painterResource(id = R.drawable.b1)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
            )
//            {
//                ImageCard(painter = painter, contentDescription = ".")
//            }
            App()
        }
    }
}

@Composable
fun App(){
    val navController= rememberNavController()
    var isImageUploaded by remember { mutableStateOf(false) }
    val sharedViewModel: SharedViewModel = viewModel()


    NavHost(navController = navController,
        startDestination ="LandingScreen"  ){

        composable(route="EditingScreen"){
            EditingScreen(navController, imageUri ?: Uri.EMPTY, sharedViewModel)
        }

        composable(route="Brightness"){
            Brightness(navController)
        }

        composable(route="Hue"){
            Hue(navController)
        }

        composable(route="Black"){
            BlackWhite(navController)
        }

        composable(route="Clone"){
            Clone(navController)
        }

        composable(route="Pen"){
            Pen(navController)
        }

        composable(route="Selection"){
            Selection(navController)
        }

        composable(route="Advanced"){
            Advanced(navController, sharedViewModel)
        }

        composable(route="CropSelection"){
           CropSelection(navController, sharedViewModel)
        }

        composable(route="Basic"){
            Basic(navController)
        }

        composable(route="LandingScreen"){
            var showStudioFont by remember { mutableStateOf(true) }
            StudioFont(isImageUploaded)

            UploadImage(onImageUploaded = { uri ->
                imageUri = uri
                isImageUploaded = true
            })
            if (isImageUploaded) {
                NextButton(navController)
            }
        }
    }
}


fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        null
    }
}


@Composable
fun Brightness(navController: NavController) {
    var brightness by remember { mutableStateOf(1f) }
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val imagePainter = rememberAsyncImagePainter(imageUri)
    val done = painterResource(R.drawable.editcheck)
    val cancel = painterResource(R.drawable.editcancel)

    // Load and modify the bitmap when brightness changes
    LaunchedEffect(brightness) {
        val originalBitmap = uriToBitmap(context, imageUri!!)
        bitmapState.value = applyBrightness(originalBitmap, brightness)
    }

    CommonAppBar(title = "Brightness", modifier = Modifier.background(color = Color.DarkGray))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(600.dp)
                .padding(20.dp)
                .padding(top =29.dp)
        ) {
            bitmapState.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(550.dp)
                )
            }
        }

        val sliderColors = SliderDefaults.colors(
            thumbColor = appbarColor, // Color of the slider thumb
            activeTrackColor = appbarColor,  // Color of the track to the right of the thumb
            inactiveTrackColor = Color.LightGray // Color of the track to the left of the thumb
        )

        Slider(
            value = brightness,
            onValueChange = { newBrightness ->
                brightness = newBrightness
                val originalBitmap = uriToBitmap(context, imageUri!!)
                bitmapState.value = applyBrightness(originalBitmap, newBrightness)
            },
            valueRange = 0.3f..1.5f, // Adjust the range as needed
            steps = 10,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val halfScreenWidth = (screenWidth / 2)
            Spacer(modifier = Modifier.weight(0.2f).width(halfScreenWidth*4))
            //First row, where cancel and done buttons should be
            LazyRow(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = halfScreenWidth/2 +24.25.dp, bottom = 13.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(appbarColor)
                            .padding(4.dp)
                            .clickable {
                                navController.navigate("EditingScreen")
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
                            .size(70.dp)
                            .background(appbarColor)
                            .padding(4.dp)
                            .clickable {
                                bitmapState.value?.let { bitmap ->
                                    val updatedUri = bitmapToUri(context, bitmap.asImageBitmap())
                                    imageUri = updatedUri

                                    if (updatedUri != Uri.EMPTY) {
                                        navController.navigate("EditingScreen")
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
    }
}


fun applyBrightness(bitmap: Bitmap?, brightness: Float): Bitmap? {
    bitmap ?: return null

    val brightnessMatrix = ColorMatrix().apply {
        setScale(brightness, brightness, brightness, 1f)
    }

    val mutableBitmap = bitmap.copy(bitmap.config, true)
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(brightnessMatrix)
    }

    val canvas = Canvas(mutableBitmap)
    canvas.drawBitmap(mutableBitmap, 0f, 0f, paint)

    return mutableBitmap
}

@Composable
fun Hue(navController: NavController) {
    var saturation by remember { mutableStateOf(0.5f) }
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val imagePainter = rememberAsyncImagePainter(imageUri)
    val done = painterResource(R.drawable.editcheck)
    val cancel = painterResource(R.drawable.editcancel)

    // Load and modify the bitmap when saturation changes
    LaunchedEffect(saturation) {
        val originalBitmap = uriToBitmap(context, imageUri!!)
        bitmapState.value = applyHue(originalBitmap, saturation)
    }

    CommonAppBar(title = "Hue", modifier = Modifier.background(color = Color.DarkGray))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(600.dp)
                .padding(20.dp)
                .padding(top =29.dp)
        ) {
            bitmapState.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(550.dp)
                )
            }
        }

        val sliderColors = SliderDefaults.colors(
            thumbColor = appbarColor, // Color of the slider thumb
            activeTrackColor = appbarColor,  // Color of the track to the right of the thumb
            inactiveTrackColor = Color.LightGray // Color of the track to the left of the thumb
        )

        Slider(
            value = saturation,
            onValueChange = { newSaturation ->
                saturation = newSaturation
                val originalBitmap = uriToBitmap(context, imageUri!!)
                bitmapState.value = applyHue(originalBitmap, newSaturation)
            },
            valueRange = 0f..1f, // Adjust the range as needed
            steps = 25,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val halfScreenWidth = (screenWidth / 2)
            Spacer(modifier = Modifier.weight(0.2f).width(halfScreenWidth*4))
            //First row, where cancel and done buttons should be
            LazyRow(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = halfScreenWidth/2 +24.25.dp, bottom = 13.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(appbarColor)
                            .padding(4.dp)
                            .clickable {
                                navController.navigate("EditingScreen")
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
                            .size(70.dp)
                            .background(appbarColor)
                            .padding(4.dp)
                            .clickable {
                                bitmapState.value?.let { bitmap ->
                                    val updatedUri = bitmapToUri(context, bitmap.asImageBitmap())
                                    imageUri = updatedUri

                                    if (updatedUri != Uri.EMPTY) {
                                        navController.navigate("EditingScreen")
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
    }
}

fun applyHue(bitmap: Bitmap?, saturation: Float): Bitmap? {
    bitmap ?: return null

    val matrix = ColorMatrix()

    val cos = cos(saturation.toDouble()).toFloat()
    val sin = sin(saturation.toDouble()).toFloat()

    val lumR = 0.213f
    val lumG = 0.715f
    val lumB = 0.072f

    matrix.set(floatArrayOf(
        lumR + cos * (1 - lumR) + sin * (-lumR), lumG + cos * (-lumG) + sin * (-lumG), lumB + cos * (-lumB) + sin * (1 - lumB), 0f, 0f,
        lumR + cos * (-lumR) + sin * (0.143f), lumG + cos * (1 - lumG) + sin * (0.140f), lumB + cos * (-lumB) + sin * (-0.283f), 0f, 0f,
        lumR + cos * (-lumR) + sin * (-(1 - lumR)), lumG + cos * (-lumG) + sin * (lumG), lumB + cos * (1 - lumB) + sin * (lumB), 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
        0f, 0f, 0f, 0f, 1f
    ))

    val mutableBitmap = bitmap.copy(bitmap.config, true)
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(matrix)
    }

    val canvas = Canvas(mutableBitmap)
    canvas.drawBitmap(mutableBitmap, 0f, 0f, paint)

    return mutableBitmap
}

@Composable
fun BlackWhite(navController: NavController) {
    var saturation by remember { mutableStateOf(0.5f) }
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val imagePainter = rememberAsyncImagePainter(imageUri)
    val done = painterResource(R.drawable.editcheck)
    val cancel = painterResource(R.drawable.editcancel)

    // Load and modify the bitmap when saturation changes
    LaunchedEffect(saturation) {
        val originalBitmap = uriToBitmap(context, imageUri!!)
        bitmapState.value = applyBlackWhite(originalBitmap, saturation)
    }

    CommonAppBar(title = "B&W", modifier = Modifier.background(color = Color.DarkGray))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(600.dp)
                .padding(20.dp)
                .padding(top =29.dp)
        ) {
            bitmapState.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(550.dp)
                )
            }
        }

        val sliderColors = SliderDefaults.colors(
            thumbColor = appbarColor, // Color of the slider thumb
            activeTrackColor = appbarColor,  // Color of the track to the right of the thumb
            inactiveTrackColor = Color.LightGray // Color of the track to the left of the thumb
        )

        Slider(
            value = saturation,
            onValueChange = { newSaturation ->
                saturation = newSaturation
                val originalBitmap = uriToBitmap(context, imageUri!!)
                bitmapState.value = applyBlackWhite(originalBitmap, newSaturation)
            },
            valueRange = 0f..1f, // Adjust the range as needed
            steps = 15,
            colors = sliderColors,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val halfScreenWidth = (screenWidth / 2)
            Spacer(modifier = Modifier.weight(0.2f).width(halfScreenWidth*4))
            //First row, where cancel and done buttons should be
            LazyRow(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = halfScreenWidth/2 +24.25.dp, bottom = 13.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(appbarColor)
                            .padding(4.dp)
                            .clickable {
                                navController.navigate("EditingScreen")
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
                            .size(70.dp)
                            .background(appbarColor)
                            .padding(4.dp)
                            .clickable {
                                bitmapState.value?.let { bitmap ->
                                    val updatedUri = bitmapToUri(context, bitmap.asImageBitmap())
                                    imageUri = updatedUri

                                    if (updatedUri != Uri.EMPTY) {
                                        navController.navigate("EditingScreen")
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
    }
}

fun applyBlackWhite(bitmap: Bitmap?, saturation: Float): Bitmap? {
    bitmap ?: return null

    val matrix = ColorMatrix()
    val lumR = 0.3f
    val lumG = 0.59f
    val lumB = 0.11f

    val sr = (1 - saturation) * lumR
    val sg = (1 - saturation) * lumG
    val sb = (1 - saturation) * lumB

    matrix.set(floatArrayOf(
        sr + saturation, sg, sb, 0f, 0f,
        sr, sg + saturation, sb, 0f, 0f,
        sr, sg, sb + saturation, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
        0f, 0f, 0f, 0f, 1f
    ))

    val mutableBitmap = bitmap.copy(bitmap.config, true)
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(matrix)
    }

    val canvas = Canvas(mutableBitmap)
    canvas.drawBitmap(mutableBitmap, 0f, 0f, paint)

    return mutableBitmap
}



fun bitmapToUri(context: Context, bitmap: ImageBitmap): Uri {
    // Generate a unique file name for the cropped image
    val timestamp = System.currentTimeMillis()
    val fileName = "instant_studio_$timestamp.png"

    // Create the file in the cache directory
    val file = File(context.cacheDir, fileName)

    // Convert ImageBitmap to Bitmap (assuming ImageBitmap is equivalent to Bitmap)
    val bitmapImage = bitmap.asAndroidBitmap()

    try {
        // Create an output stream to write the bitmap to the file
        val outputStream = FileOutputStream(file)
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Compress and save the bitmap
        outputStream.close() // Close the output stream after saving

        // Return the URI for the saved cropped image
        return FileProvider.getUriForFile(context, "com.example.myapplication.provider", file)
    } catch (e: IOException) {
        // Handle any errors that occur during saving
        Log.e("bitmapToUri Function", "Error saving cropped image: ${e.message}")
        // Return a default URI or handle the error as appropriate for your app
        return Uri.EMPTY
    }
}



@Composable
fun Basic(navController: NavController){
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))
    val exp = painterResource(R.drawable.exp)
    val hue = painterResource(R.drawable.blur)
    val black = painterResource(R.drawable.contrast)

    CommonAppBar(title = "Basic Tools", modifier = Modifier.background(color = Color.DarkGray))

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(500.dp)
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val halfScreenWidth = (screenWidth / 2)
        Spacer(modifier = Modifier.weight(0.2f).width(halfScreenWidth*4))
        //First row, where cancel and done buttons should be
        LazyRow(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = halfScreenWidth/2 +8.dp, bottom = 22.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            item {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .background(appbarColor)

                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = exp,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { navController.navigate(route = "Brightness") }
                        )
                        Text(
                            text = "Exposure",
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
                        .size(65.dp)
                        .background(appbarColor)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter =black ,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { navController.navigate(route = "Black") }
                        )
                        Text(
                            text = "B&W",
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
                        .size(65.dp)
                        .background(appbarColor)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = hue,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { navController.navigate(route = "Hue") }
                        )
                        Text(
                            text = "Hue",
                            color = Color.White,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(5.dp) // Add padding at the bottom
                        )
                    }

                }
            }
        }
    }
}



@Composable
fun Advanced(navController: NavController, sharedViewModel: SharedViewModel){
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))
//    val imageUriVM by sharedViewModel.imageUri.observeAsState(initial = Uri.EMPTY)
    var crrImageUri: Uri? by remember { mutableStateOf(imageUri) }
    val pen = painterResource(R.drawable.pen)
    val clone = painterResource(R.drawable.clone)
    val context = LocalContext.current



    val aEPenToolLauncher = rememberLauncherForActivityResult(LaunchAEModulePenToolContract()) { drawnImageUri ->
        drawnImageUri?.let {
            Log.d("aEPenToolLauncher", "Current Image URI: $drawnImageUri") // Add this log statement
            crrImageUri = drawnImageUri
            imageUri = drawnImageUri
            sharedViewModel.setImageUri(drawnImageUri)
            Toast.makeText(context, "Drawing Applied", Toast.LENGTH_LONG).show()
        }
    }
    val aECloningToolLauncher = rememberLauncherForActivityResult(LaunchAEModuleCloningToolContract()) { clonedImageUri ->
        clonedImageUri?.let {
            Log.d("aECloningToolLauncher", "Current Image URI: $clonedImageUri") // Add this log statement
            crrImageUri = clonedImageUri
            imageUri = clonedImageUri
            sharedViewModel.setImageUri(clonedImageUri)
            Toast.makeText(context, "Cloning Applied", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(

                painter = rememberAsyncImagePainter(crrImageUri),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(550.dp)
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.weight(0.2f))
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp).clip(RoundedCornerShape(35.dp))
        ) {

            item {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.DarkGray)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = clone,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { aECloningToolLauncher.launch(imageUri) }
                        )
                        Text(
                            text = "Clone",
                            color = Color.Black,
                            fontSize = 10.sp,
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
                        .background(Color.DarkGray)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = pen,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { aEPenToolLauncher.launch(imageUri) }
                        )
                        Text(
                            text = "Pen",
                            color = Color.Black,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Clone(navController: NavController){
    Text(text = "Clone",
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.clickable { navController.navigate(route = "EditingScreen") })

}


@Composable
fun Selection(navController: NavController){
    Text(text = "Selection",
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.clickable { navController.navigate(route = "EditingScreen") })

}


@Composable
fun CropSelection(navController: NavController, sharedViewModel: SharedViewModel)
{
    val crop = painterResource(R.drawable.crop)
    val select = painterResource(R.drawable.select)

    val context = LocalContext.current

    var currentImageUri by remember { mutableStateOf(imageUri) }

    val cropModuleLauncher = rememberLauncherForActivityResult(LaunchCroppingModuleContract()) { croppedImageUri ->
        croppedImageUri?.let {
            //Log.d("MainActivity", "Current Image URI: $currentImageUri") // Add this log statement
            currentImageUri = croppedImageUri
            imageUri = croppedImageUri
            sharedViewModel.setImageUri(croppedImageUri)
            Toast.makeText(context, "Image Cropped", Toast.LENGTH_LONG).show()
        }
    }
    val selectionModuleLauncher = rememberLauncherForActivityResult(LaunchSelectionModuleContract()) { croppedImageUri ->
        croppedImageUri?.let {
            //Log.d("MainActivity", "Current Image URI: $currentImageUri") // Add this log statement
            currentImageUri = croppedImageUri
            imageUri = croppedImageUri
            sharedViewModel.setImageUri(croppedImageUri)
            Toast.makeText(context, "Sticker Pasted", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(currentImageUri),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(550.dp),
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
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
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = crop,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    cropModuleLauncher.launch(currentImageUri) // Launch with the current image URI
                                }
                        )
                        Text(
                            text = "Crop",
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
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = select,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { selectionModuleLauncher.launch(currentImageUri) }
                        )
                        Text(
                            text = "Select",
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

@Composable
fun Pen(navController: NavController){
    Text(text = "Pen",
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.clickable { navController.navigate(route = "EditingScreen") })

}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri {

    // Generate a unique file name for the cropped image
    val timestamp = System.currentTimeMillis()
    val fileName = "cropped_image_$timestamp.png"

    // Create the file in the cache directory
    val file = File(inContext.cacheDir, fileName)

    // Convert ImageBitmap to Bitmap (assuming ImageBitmap is equivalent to Bitmap)
    val bitmapImage = inImage

    try {
        // Create an output stream to write the bitmap to the file
        val outputStream = FileOutputStream(file)
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Compress and save the bitmap
        outputStream.close() // Close the output stream after saving

        // Return the URI for the saved cropped image
        return FileProvider.getUriForFile(inContext, "com.example.myapplication.provider", file)
    } catch (e: IOException) {
        // Handle any errors that occur during saving
        Log.e("CroppingModule", "Error saving cropped image: ${e.message}")
        // Return a default URI or handle the error as appropriate for your app
        return Uri.EMPTY
    }
}

@Composable
fun EditingScreen(navController: NavController,imageUri: Uri, sharedViewModel: SharedViewModel) {
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))
    val bright = painterResource(R.drawable.outline_exposure_24)
    val crop = painterResource(R.drawable.outline_photo_size_select_large_24)
    val fgc = painterResource(R.drawable.outline_foreground_24)
    val bgc = painterResource(R.drawable.background_24)
    val adv = painterResource(R.drawable.outline_advance_24)
    val filter = painterResource(R.drawable.outline_filter_frames_24)
    val context = LocalContext.current
    val imageBitmap: Bitmap? = com.example.myapplication.imageUri?.let { uriToBitmap2(context, it) }

    var currentEditingImageUri by remember { mutableStateOf(com.example.myapplication.imageUri) }


    val bgModuleLauncher = rememberLauncherForActivityResult(LaunchBackgroundContract()) { bgImageUri ->
        bgImageUri?.let {
            //Log.d("MainActivity", "Current Image URI: $currentImageUri") // Add this log statement
            currentEditingImageUri = bgImageUri

            System.out.println("RECEIVED URI  "+ bgImageUri)
            com.example.myapplication.imageUri= bgImageUri
            System.out.println("UPDATE GLOBAL  URI  "+  com.example.myapplication.imageUri)
            sharedViewModel.setImageUri(bgImageUri)
        }
    }

    val fgModuleLauncher = rememberLauncherForActivityResult(LaunchForegroundContract()) { fgImageUri ->
        fgImageUri?.let {
            //Log.d("MainActivity", "Current Image URI: $currentImageUri") // Add this log statement
            currentEditingImageUri = fgImageUri
            com.example.myapplication.imageUri = fgImageUri
            sharedViewModel.setImageUri(fgImageUri)
        }
    }

    val filterModuleLauncher = rememberLauncherForActivityResult(LaunchFilterModuleContract()) { filterImageUri ->
        filterImageUri?.let {
            //Log.d("MainActivity", "Current Image URI: $currentImageUri") // Add this log statement
            currentEditingImageUri = filterImageUri
            com.example.myapplication.imageUri = filterImageUri
            sharedViewModel.setImageUri(filterImageUri)
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box()
        {
        if (imageBitmap != null)
            SaveButton(context, imageBitmap, navController)
        Image(
                painter = rememberAsyncImagePainter(currentEditingImageUri),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 84.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            )
        }
    }


    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.weight(0.2f))
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(18.dp).padding(bottom = 10.dp).clip(RoundedCornerShape(8.dp))
        ) {

            item {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(appbarColor)
                        .clickable { navController.navigate(route = "Basic") }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = bright,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                        )
                        Text(
                            text = "Light",
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
                        .clickable { filterModuleLauncher.launch(currentEditingImageUri) }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = filter,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)

                        )
                        Text(
                            text = "Filters",
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
                        .clickable { navController.navigate(route = "Advanced") }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = adv,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Advanced",
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
                        .clickable { fgModuleLauncher.launch(currentEditingImageUri) }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = fgc,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "FG Color",
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
                        .clickable { bgModuleLauncher.launch(currentEditingImageUri) }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = bgc,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                        )
                        Text(
                            text = "BG Color",
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
                        .clickable { navController.navigate(route = "CropSelection") }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = crop,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)

                        )
                        Text(
                            text = "Crop",
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
}


@Composable
fun NextButton(navController: NavController) {
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .width(120.dp)
                .padding(15.dp)
        ) {

            Row(
                modifier = Modifier
                    .background(
                        color = appbarColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(onClick = {navController.navigate(route = "EditingScreen")}),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    fontSize = 19.sp
                )
            }
        }}
}


@Composable
fun ImageCard(
    painter: Painter,//for displaying the image
    contentDescription: String,
    modifier: Modifier=Modifier
)
{
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(modifier = Modifier
            .height(800.dp)
            .fillMaxSize())
        {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )

            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(0.5f)
                        ),
                        startY = 300f
                    )
                ))
        }
    }
}


@Composable
fun StudioFont(isImageUploaded: Boolean) {
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))

    val gradientcolors = listOf(
        Color.Transparent,  Color.Transparent, Color.Black.copy(alpha = 0.3f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(gradientcolors))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(!isImageUploaded) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.White,
                                    fontSize = 60.sp,
                                    fontFamily = merriFont
                                )
                            ) {
                                append("I")
                            }
                            append("nstant ")
                            withStyle(
                                style = SpanStyle(
                                    color = Color.White,
                                    fontSize = 60.sp,
                                    fontFamily = merriFont
                                )
                            ) {
                                append("S")
                            }
                            append("tudio")
                        },
                        fontSize = 45.sp,
                        color = Color.White.copy(0.9f),
                        fontFamily = merriFont,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp) // Add padding to center vertically
                    )
                }
            }
        }
    }
}


@Composable
fun UploadImage(onImageUploaded: (Uri) -> Unit) {
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                onImageUploaded(it)
            }
        }
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 29.dp)
            .fillMaxSize()
    ) {
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 55.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.weight(0.8f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .height(200.dp)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = appbarColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(onClick = {galleryLauncher.launch("image/*") }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                if(imageUri!=null){
                    Text(
                        text = "Reselect",
                        color = Color.White,
                        fontSize = 19.sp
                    )
                }
                else {
                    Text(
                        text = "Import",
                        color = Color.White,
                        fontSize = 19.sp
                    )
                }
            }
        }
    }
}

fun uriToBitmap2(context: Context, uri: Uri): Bitmap? {
    var inputStream: InputStream? = null
    return try {
        inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        inputStream?.close()
    }
}
@Composable
fun SaveButton(context: Context, imageBitmap: Bitmap, navController: NavController) {
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .width(120.dp)
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = appbarColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(onClick = {
                        val bytes = ByteArrayOutputStream()
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                        val path =
                            MediaStore.Images.Media.insertImage(context.contentResolver, imageBitmap, "Title", null)
                        Uri.parse(path)
                        System.out.println("ABOUT TO SAVE IMAGE URI: ")

                        navController.navigate(route = "LandingScreen")
                    }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Save",
                    color = Color.White,
                    fontSize = 19.sp
                )
            }
        }}
}

