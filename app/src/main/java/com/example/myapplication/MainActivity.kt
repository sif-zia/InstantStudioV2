package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Base64
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
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




var imageUri: Uri? = null


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val painter = painterResource(id = R.drawable.b1)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            ) {
                ImageCard(painter = painter, contentDescription = ".")
            }
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

        composable(route="Blur"){
            Blur(navController)
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
            StudioFont()

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

    // Load and modify the bitmap when brightness changes
    LaunchedEffect(brightness) {
        val originalBitmap = uriToBitmap(context, imageUri!!)
        bitmapState.value = applyBrightness(originalBitmap, brightness)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(600.dp)
                .padding(20.dp)
        ) {
            bitmapState.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(500.dp)
                )
            }
        }

        Slider(
            value = brightness,
            onValueChange = { newBrightness ->
                brightness = newBrightness
                val originalBitmap = uriToBitmap(context, imageUri!!)
                bitmapState.value = applyBrightness(originalBitmap, newBrightness)
            },
            valueRange = 0.3f..1.5f, // Adjust the range as needed
            steps = 10,
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        )

        Button(
            onClick = {
                bitmapState.value?.let { bitmap ->
                    val updatedUri = bitmapToUri(context, bitmap.asImageBitmap())
                    imageUri=updatedUri

                    if (updatedUri != Uri.EMPTY) {
                        navController.navigate("EditingScreen")
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Done")
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

    // Load and modify the bitmap when saturation changes
    LaunchedEffect(saturation) {
        val originalBitmap = uriToBitmap(context, imageUri!!)
        bitmapState.value = applyHue(originalBitmap, saturation)
    }

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

        Slider(
            value = saturation,
            onValueChange = { newSaturation ->
                saturation = newSaturation
                val originalBitmap = uriToBitmap(context, imageUri!!)
                bitmapState.value = applyHue(originalBitmap, newSaturation)
            },
            valueRange = 0f..1f, // Adjust the range as needed
            steps = 25,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        Button(
            onClick = {
                bitmapState.value?.let { bitmap ->
                    val updatedUri = bitmapToUri(context, bitmap.asImageBitmap())
                    imageUri = updatedUri

                    if (updatedUri != Uri.EMPTY) {
                        navController.navigate("EditingScreen")
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Done")
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

    // Load and modify the bitmap when saturation changes
    LaunchedEffect(saturation) {
        val originalBitmap = uriToBitmap(context, imageUri!!)
        bitmapState.value = applyBlackWhite(originalBitmap, saturation)
    }

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

        Slider(
            value = saturation,
            onValueChange = { newSaturation ->
                saturation = newSaturation
                val originalBitmap = uriToBitmap(context, imageUri!!)
                bitmapState.value = applyBlackWhite(originalBitmap, newSaturation)
            },
            valueRange = 0f..1f, // Adjust the range as needed
            steps = 15,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        Button(
            onClick = {
                bitmapState.value?.let { bitmap ->
                    val updatedUri = bitmapToUri(context, bitmap.asImageBitmap())
                    imageUri = updatedUri

                    if (updatedUri != Uri.EMPTY) {
                        navController.navigate("EditingScreen")
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Done")
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


@Composable
fun Blur(navController: NavController) {
    val context = LocalContext.current
    var blurRadius by remember { mutableStateOf(0f) }
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    // Initialize bitmapState with the modified bitmap when the composable is first called
    DisposableEffect(imageUri) {
        if (imageUri != null) {
            val originalBitmap = uriToBitmap(context, imageUri!!)
            bitmapState.value = applyBlur(originalBitmap, blurRadius)
        }
        onDispose { }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(600.dp)
                .padding(20.dp)
        ){
            bitmapState.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Blurry Image",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(550.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    1f to Color.Black
                                ),
                                blendMode = BlendMode.DstOut
                            )
                        }
                        .blur(blurRadius.dp)
                )
            }
        }

        Slider(
            value = blurRadius,
            onValueChange = { newBlurRadius ->
                blurRadius = newBlurRadius
                val originalBitmap = uriToBitmap(context, imageUri!!)
                bitmapState.value = applyBlur(originalBitmap, newBlurRadius)
            },
            valueRange = 0f..25f,
            steps = 15,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        Button(
            onClick = {
                bitmapState.value?.let { bitmap ->
                    val updatedUri = bitmapToUri(context, bitmap.asImageBitmap())
                    imageUri = updatedUri

                    if (updatedUri != Uri.EMPTY) {
                        navController.navigate("EditingScreen")
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Done")
        }
    }
}


fun applyBlur(bitmap: Bitmap?, blurRadius: Float): Bitmap? {
    if (bitmap == null) return null // Check if the bitmap is null and return null immediately

    return if (blurRadius > 0) {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 4, bitmap.height / 4, false)
        val blurredBitmap = Bitmap.createBitmap(scaledBitmap.width, scaledBitmap.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(blurredBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        }

        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
        blurredBitmap
    } else {
        bitmap.copy(bitmap.config, true)
    }
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
    val contrast = painterResource(R.drawable.contrast)
    val saturation = painterResource(R.drawable.saturation)
    val black = painterResource(R.drawable.advanced)

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
                            painter = exp,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { navController.navigate(route = "Brightness") }
                        )
                        Text(
                            text = "Exposure",
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
                            painter =black ,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { navController.navigate(route = "Black") }
                        )
                        Text(
                            text = "B&W",
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
                            painter = contrast,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { navController.navigate(route = "Hue") }
                        )
                        Text(
                            text = "Hue",
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
                            painter = saturation,
                            contentDescription = "Your Icon Description",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { navController.navigate(route = "Blur") }
                        )
                        Text(
                            text = "Blur",
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
    val bright = painterResource(R.drawable.brightness)
    val crop = painterResource(R.drawable.crop)
    val fgc = painterResource(R.drawable.fgc)
    val bgc = painterResource(R.drawable.bgc)
    val adv = painterResource(R.drawable.advanced)
    val filter = painterResource(R.drawable.filters)
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
            Row(
                modifier = Modifier.fillMaxHeight(0.9f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(currentEditingImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(550.dp)
                )
            }
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
            modifier = Modifier.padding(18.dp).padding(bottom = 10.dp).clip(RoundedCornerShape(35.dp))
        ) {

            item {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.DarkGray)
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
                            color = Color.Black,
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
                        .background(Color.DarkGray)
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
                            color = Color.Black,
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
                        .background(Color.DarkGray)
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
                            color = Color.Black,
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
                        .background(Color.DarkGray)
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
                            color = Color.Black,
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
                        .background(Color.DarkGray)
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
                            color = Color.Black,
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
                        .background(Color.DarkGray)
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

            TextButton(
                onClick = {
                    navController.navigate(route = "EditingScreen")
                    },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.DarkGray)
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = merriFont,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center
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
fun StudioFont() {
    val merriFont = FontFamily(Font(R.font.merri, FontWeight.Normal))

    val gradientcolors = listOf(
        Color.Transparent,  Color.Transparent, Color.Black.copy(alpha = 0.3f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
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
                    fontFamily =merriFont,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp) // Add padding to center vertically
                )
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
                    .width(360.dp)
                    .fillMaxWidth()
                    .height(600.dp)
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
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .clickable { // Make the whole box clickable
                        galleryLauncher.launch("image/*")
                    }
            ) {
                Text(
                    color = Color.White,
                    fontSize = 40.sp,
                    fontFamily = merriFont,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center,
                    text = "+",
                    modifier = Modifier.align(Alignment.Center)
                )
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

            TextButton(
                onClick = {
                    val bytes = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path =
                        MediaStore.Images.Media.insertImage(context.contentResolver, imageBitmap, "Title", null)
                    Uri.parse(path)
                    System.out.println("ABOUT TO SAVE IMAGE URI: ")

                    navController.navigate(route = "LandingScreen")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.DarkGray)
            ) {
                Text(
                    text = "Save",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = merriFont,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }}
}

