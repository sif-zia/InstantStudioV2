package com.example.myapplication
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.widget.CropSelectionWidgets.loadImageBitmap
import kotlin.math.pow
import kotlin.math.sqrt

class SelectionModule : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri: Uri? = intent?.getParcelableExtra("imageUri")

        setContent {
            val navController = rememberNavController()
            var tapPosition by remember { mutableStateOf(Pair(0f, 0f)) }
            val tappedPoints = remember { mutableStateListOf<Pair<Float, Float>>() }
            val thresholdDistance = 10f
            var selectedImageBitmap by remember(imageUri) { mutableStateOf<ImageBitmap?>(null) }
            var bitmap by remember() { mutableStateOf<ImageBitmap?>(null) }
            var sticker by remember(imageUri) { mutableStateOf<ImageBitmap?>(null) }
            val context = LocalContext.current
            var screenWidth = context.resources.displayMetrics.widthPixels
            var screenHeight = context.resources.displayMetrics.heightPixels

            var height by remember { mutableStateOf(0) }
            var width by remember { mutableStateOf(0) }

            var imageHeight by remember { mutableStateOf(0) }
            var imageWidth by remember { mutableStateOf(0) }

            var offsetX by remember { mutableStateOf(0f) }
            var isSelected by remember { mutableStateOf(false) }
            var isPasting by remember { mutableStateOf(true) }
            var hasScaled by remember { mutableStateOf(false) }

            var isPolygon by remember { mutableStateOf(false) }
            var isFreehand by remember { mutableStateOf(false) }

            LaunchedEffect(imageUri) {
                selectedImageBitmap = try {
                        loadImageBitmap(context, imageUri)
                } catch (t: Throwable) {
                    null
                }
            }
            Column{
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight(0.9f)
                ) {
                    selectedImageBitmap?.let {
                        var stickeroffsetX by remember { mutableStateOf(0f) }
                        var stickeroffsetY by remember { mutableStateOf(0f) }

                        if(!isPolygon && !isFreehand){
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .clipToBounds()
                                    .fillMaxWidth()
                            )
                        }
                        if(isPolygon || isFreehand) {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .clipToBounds()
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        detectTapGestures { offset ->
                                            val point = Pair(offset.x, offset.y)
                                            if (!isSelected && isPolygon) {
                                                tappedPoints.add(point)
                                                tapPosition = point
                                            }
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            if (!isSelected && isFreehand) {
                                                tappedPoints.add(Pair(change.position.x, change.position.y))
                                            }
                                            if (isSelected && isPasting) {
                                                stickeroffsetX += dragAmount.x
                                                stickeroffsetY += dragAmount.y
                                                if (change.positionChange() != androidx.compose.ui.geometry.Offset.Zero) change.consume()
                                            }
                                        }
                                    }
                                    .drawWithContent {
                                        drawContent()
                                        width = size.width.toInt()
                                        height = size.height.toInt()
                                        if (!hasScaled) {
                                            selectedImageBitmap?.let {
                                                if (selectedImageBitmap!!.width < screenWidth) {
                                                    selectedImageBitmap = resizeBitmapWithAspectRatio(
                                                        selectedImageBitmap!!.asAndroidBitmap(),
                                                        width,
                                                        width
                                                    ).asImageBitmap()
                                                }
                                            }
                                            hasScaled = true
                                        }
                                        tappedPoints.forEachIndexed { index, point ->
                                            drawCircle(
                                                color = Color.Red,
                                                center = androidx.compose.ui.geometry.Offset(point.first, point.second),
                                                radius = 5f,
                                                style = Fill
                                            )

                                            if (index > 0) {
                                                val previousPoint = tappedPoints[index - 1]
                                                drawLine(
                                                    color = Color.Red,
                                                    start = androidx.compose.ui.geometry.Offset(
                                                        previousPoint.first,
                                                        previousPoint.second
                                                    ),
                                                    end = androidx.compose.ui.geometry.Offset(point.first, point.second),
                                                    strokeWidth = 4f
                                                )
                                            }

                                            if (index == tappedPoints.lastIndex && tappedPoints.size > 2) {
                                                val firstPoint = tappedPoints.first()
                                                val lastPoint = tappedPoints.last()
                                                val distance = sqrt(
                                                    (lastPoint.first - firstPoint.first).pow(2) + (lastPoint.second - firstPoint.second).pow(
                                                        2
                                                    )
                                                )
                                                if (distance <= thresholdDistance) {
                                                    isSelected = true
                                                    val path = Path().apply {
                                                        tappedPoints.forEachIndexed { index, point ->
                                                            if (index == 0) moveTo(
                                                                point.first,
                                                                point.second
                                                            )
                                                            else lineTo(point.first, point.second)
                                                        }
                                                        close()
                                                    }
//                                        drawPath(path, color = Color.Red, style = Fill)
                                                    imageWidth = selectedImageBitmap!!.width
                                                    imageHeight = selectedImageBitmap!!.height

                                                    offsetX = 0f
                                                    if (imageWidth < screenWidth) {
                                                        offsetX =
                                                            (screenWidth.toFloat() - imageWidth.toFloat()) / 2.0f
                                                    }
                                                    sticker = getSticker(
                                                        selectedImageBitmap!!.asAndroidBitmap(),
                                                        path,
                                                        width,
                                                        height,
                                                        offsetX
                                                    ).asImageBitmap()
                                                }
                                            }

                                        }
                                        sticker?.let { it1 ->
                                            drawImage(
                                                image = it1,
                                                topLeft = androidx.compose.ui.geometry.Offset(stickeroffsetX, stickeroffsetY)
                                            )
                                        }
                                        if (!isPasting) {
                                            selectedImageBitmap = pasteBitmapOverAnother(
                                                selectedImageBitmap!!.asAndroidBitmap(),
                                                sticker!!.asAndroidBitmap(),
                                                androidx.compose.ui.geometry.Offset(stickeroffsetX, stickeroffsetY)
                                            ).asImageBitmap()
                                            sticker = null
                                            tapPosition = Pair(0f, 0f)
                                            tappedPoints.clear()
                                            stickeroffsetX = 0f
                                            stickeroffsetY = 0f
                                            isPasting = true
                                            isSelected = false
                                            isPolygon = false
                                            isFreehand = false
                                            val returnedUri = selectedImageBitmap?.let {
                                                com.example.myapplication.widget.bitmapToUri(
                                                    context,
                                                    it
                                                )
                                            }
                                            val resultIntent = Intent().apply {
                                                putExtra("croppedImageUri", returnedUri)
                                            }
                                            setResult(Activity.RESULT_OK, resultIntent)
                                            finish() // Finish the activity to return to the launcher
                                        }
                                    }
                            )
                        }
                    }
                }
                if(isSelected) {
                    Button(
                        onClick = { isPasting = false },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_check_24),
                            contentDescription = "Paste Sticker",
                            tint = Color.White // Adjust the icon color as needed
                        )
                    }


                }
                if(!isPolygon && !isFreehand) {
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
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.polygon_select),
                                            contentDescription = "Freehand Selection",
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable { isPolygon = true }
                                        )
                                        Text(
                                            text = "Freehand Selection",
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
                                            painter = painterResource(id = R.drawable.freehand_select),
                                            contentDescription = "Freehand Selection",
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable { isFreehand = true }
                                        )
                                        Text(
                                            text = "Freehand Selection",
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
    fun getSticker(
        sourceImgBitmap: Bitmap,
        drawPath: Path,
        newWidth: Int,
        newHeight: Int,
        offsetX: Float
    ): Bitmap {
        val mutableBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            style = Paint.Style.FILL
        }
        canvas.drawPath(drawPath.asAndroidPath(), paint)
        return extractImage(sourceImgBitmap,mutableBitmap,newWidth, newHeight,offsetX)
    }

    fun extractImage(
        sourceImgBitmap: Bitmap,
        destinationImgBitmap: Bitmap,
        newWidth: Int,
        newHeight: Int,
        offsetX: Float
    ):Bitmap{

        val mutableBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        val canvas = android.graphics.Canvas(mutableBitmap)
        val paint = Paint().apply {  }
        canvas.drawBitmap(destinationImgBitmap, 0f, 0f, paint)

        val mode: PorterDuff.Mode = android.graphics.PorterDuff.Mode.SRC_IN
        paint.setXfermode(PorterDuffXfermode(mode))
        val bit = resizeBitmapWithAspectRatio(sourceImgBitmap, newWidth, newHeight)
        canvas.drawBitmap(bit, offsetX, 0f, paint)
        return mutableBitmap
    }

    fun resizeBitmapWithAspectRatio(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()

        val scaledWidth: Int = (newHeight * ratio).toInt()
        val scaledHeight: Int = newHeight

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }

    fun pasteBitmapOverAnother(largeBitmap: Bitmap, smallBitmap: Bitmap, offset: Offset): Bitmap {
        val x = (largeBitmap.width.toFloat()/smallBitmap.width.toFloat())*offset.x
        val y = (largeBitmap.height.toFloat()/smallBitmap.height.toFloat())*offset.y
        val scaledSticker = Bitmap.createScaledBitmap(smallBitmap, largeBitmap.width, largeBitmap.height, true)
        val resultBitmap = Bitmap.createBitmap(largeBitmap.width, largeBitmap.height, largeBitmap.config)
        val canvas = android.graphics.Canvas(resultBitmap)
        canvas.drawBitmap(largeBitmap, 0f, 0f, null)  // Draw the large bitmap first
        canvas.drawBitmap(scaledSticker, x, y, null)  // Draw the small bitmap at the specified offset

        return resultBitmap
    }
}
