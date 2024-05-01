package com.example.myapplication
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.example.myapplication.widget.CommonAppBar
import com.example.myapplication.widget.CropSelectionWidgets.loadImageBitmap
import kotlin.math.pow
import kotlin.math.sqrt

class SelectionModule : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri: Uri? = intent?.getParcelableExtra("imageUri")

        setContent {
            var tapPosition by remember { mutableStateOf(Pair(0f, 0f)) }
            val tappedPoints = remember { mutableStateListOf<Pair<Float, Float>>() }
            val thresholdDistance = 20f

            var selectedImageBitmap by remember(imageUri) { mutableStateOf<ImageBitmap?>(null) }
            var sticker by remember(imageUri) { mutableStateOf<ImageBitmap?>(null) }
            val context = LocalContext.current

            var height by remember { mutableIntStateOf(0) }
            var width by remember { mutableIntStateOf(0) }

            var originalHeight by remember { mutableIntStateOf(0) }
            var originalWidth by remember { mutableIntStateOf(0) }
            var originalImageBitmap by remember(imageUri) { mutableStateOf<ImageBitmap?>(null) }

            var imageHeight by remember { mutableFloatStateOf(0f) }
            var imageWidth by remember { mutableFloatStateOf(0f) }

            var offsetX by remember { mutableFloatStateOf(0f) }
            var stickerOffsetX by remember { mutableFloatStateOf(0f) }
            var stickerOffsetY by remember { mutableFloatStateOf(0f) }

            var isSelected by remember { mutableStateOf(false) }
            var isPasting by remember { mutableStateOf(true) }
            var hasScaled by remember { mutableIntStateOf(2) }
            var isPolygon by remember { mutableStateOf(false) }
            var isFreehand by remember { mutableStateOf(false) }
            var isCancelled by remember { mutableStateOf(false) }

            var padding by remember { mutableFloatStateOf(16f) }

            val gradientcolors = listOf(
                Color.Transparent,  Color.Transparent, Color.Black.copy(alpha = 0.3f)
            )
            var bgColor = Color(12,32,63)
            var appbarColor = Color(25,56,106)

            LaunchedEffect(imageUri) {
                selectedImageBitmap = try {
                    loadImageBitmap(context, imageUri)
                } catch (t: Throwable) {
                    null
                }
                originalWidth = selectedImageBitmap!!.width
                originalHeight = selectedImageBitmap!!.height
                originalImageBitmap = selectedImageBitmap
            }
            Column(modifier=Modifier.background(bgColor)){
                CommonAppBar(title = "Sticker Selection")
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                Column(modifier = Modifier.background(brush = Brush.verticalGradient(gradientcolors))) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight(0.8f)
                            .drawWithContent {
                                drawContent()
                                width = size.width.toInt()
                                height = size.height.toInt()
                            }
                    ) {
                        selectedImageBitmap?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(horizontal = padding.pxToDp().dp)
                                    .clipToBounds()
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        detectTapGestures { offset ->
                                            if (isPolygon || isFreehand) {
                                                val point = Pair(offset.x, offset.y)
                                                if (!isSelected && isPolygon) {
                                                    tappedPoints.add(point)
                                                    tapPosition = point
                                                }
                                            }
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            if (isPolygon || isFreehand) {
                                                if (!isSelected && isFreehand) {
                                                    tappedPoints.add(
                                                        Pair(
                                                            change.position.x,
                                                            change.position.y
                                                        )
                                                    )
                                                }
                                                if (isSelected && isPasting) {
                                                    stickerOffsetX += dragAmount.x
                                                    stickerOffsetY += dragAmount.y
                                                    if (change.positionChange() != Offset.Zero) change.consume()
                                                }
                                            }
                                        }
                                    }
                                    .drawWithContent {
                                        drawContent()
                                        println(height.toString() + "-----height")
                                        println(width.toString() + "-----width")
                                        offsetX = 16.dpToPx(context)
                                        if (hasScaled != 0) {
                                            if (hasScaled == 1) {
                                                selectedImageBitmap?.let {
                                                    selectedImageBitmap =
                                                        resizeBitmapWithAspectRatio(
                                                            selectedImageBitmap!!.asAndroidBitmap(),
                                                            width,
                                                            height,
                                                            offsetX
                                                        ).asImageBitmap()
                                                }
                                                imageWidth = selectedImageBitmap!!.width.toFloat()
                                                imageHeight = selectedImageBitmap!!.height.toFloat()
                                                if (imageWidth < width) {
                                                    padding = ((width - imageWidth) / 2)
                                                }

                                                println(padding.toString() + "----padding")
                                                println(imageWidth.toString() + "-----iwidth")
                                                println(imageHeight.toString() + "-----iheight")
                                            }
                                            hasScaled--
                                        }
                                        tappedPoints.forEachIndexed { index, point ->
                                            drawCircle(
                                                color = Color.Red,
                                                center = Offset(point.first, point.second),
                                                radius = 5f,
                                                style = Fill
                                            )

                                            if (index > 0) {
                                                val previousPoint = tappedPoints[index - 1]
                                                drawLine(
                                                    color = Color.Red,
                                                    start = Offset(
                                                        previousPoint.first,
                                                        previousPoint.second
                                                    ),
                                                    end = Offset(point.first, point.second),
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
                                                    sticker = extractSticker(
                                                        selectedImageBitmap!!.asAndroidBitmap(),
                                                        tappedPoints,
                                                        imageWidth,
                                                        imageHeight
                                                    ).asImageBitmap()
                                                }
                                            }

                                        }
                                        sticker?.let { it1 ->
                                            drawImage(
                                                image = it1,
                                                topLeft = Offset(stickerOffsetX, stickerOffsetY)
                                            )
                                        }
                                        if (!isPasting) {
                                            selectedImageBitmap = pasteSticker(
                                                originalImageBitmap!!.asAndroidBitmap(),
                                                sticker!!.asAndroidBitmap(),
                                                Offset(stickerOffsetX, stickerOffsetY),
                                                originalWidth,
                                                originalHeight
                                            ).asImageBitmap()
                                            sticker = null
                                            tapPosition = Pair(0f, 0f)
                                            tappedPoints.clear()
                                            stickerOffsetX = 0f
                                            stickerOffsetY = 0f
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
                                        if (isCancelled) {
                                            sticker = null
                                            tapPosition = Pair(0f, 0f)
                                            tappedPoints.clear()
                                            stickerOffsetX = 0f
                                            stickerOffsetY = 0f
                                            isPasting = true
                                            isSelected = false
                                            isPolygon = false
                                            isFreehand = false
                                            isCancelled = false
                                        }
                                    }
                            )
                        }
                    }
                    if (isPolygon || isFreehand) {
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.weight(0.2f))
                            LazyRow(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.padding(18.dp).padding(bottom = 10.dp).clip(
                                    RoundedCornerShape(8.dp)
                                )
                            ) {


                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(appbarColor)
                                            .clickable { isCancelled = true }
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.Bottom,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.editcancel),
                                                contentDescription = "Cancel",
                                                modifier = Modifier
                                                    .size(28.dp)

                                            )
                                            Text(
                                                text = "Cancel",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                    }
                                }

                                if (isSelected) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .background(appbarColor)
                                                .clickable { isPasting = false }
                                        ) {
                                            Column(
                                                verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.editcheck),
                                                    contentDescription = "Paste",
                                                    modifier = Modifier
                                                        .size(28.dp)

                                                )
                                                Text(
                                                    text = "Paste",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(5.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!isPolygon && !isFreehand) {
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.weight(0.2f))
                            LazyRow(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.padding(18.dp).padding(bottom = 10.dp).clip(RoundedCornerShape(8.dp))
                            ) {


                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(appbarColor)
                                            .clickable { isPolygon = true }
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.polygon_select),
                                                contentDescription = "Polygon Selection",
                                                modifier = Modifier
                                                    .size(28.dp)

                                            )
                                            Text(
                                                text = "Polygon",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.Center,
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
                                            .clickable { isFreehand = true }
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.Bottom,
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.freehand_select),
                                                contentDescription = "Freehand Selection",
                                                modifier = Modifier
                                                    .size(28.dp)

                                            )
                                            Text(
                                                text = "Freehand",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            }
        }
    }
    @Composable
    fun Float.pxToDp(): Float = (this / (LocalContext.current.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))

    private fun Int.dpToPx(context: Context): Float = (this * context.resources.displayMetrics.density)

    private fun extractSticker(sourceImgBitmap: Bitmap, tappedPoints: SnapshotStateList<Pair<Float, Float>>, newWidth: Float, newHeight: Float): Bitmap {
        //shift path if offset > 0
        val drawPath = Path().apply {
            tappedPoints.forEachIndexed { index, point ->
                val shiftedX = point.first
                if (index == 0) moveTo(
                    shiftedX,
                    point.second
                )
                else lineTo(shiftedX, point.second)
            }
            close()
        }

        //make stencil
        val mutableBitmap = Bitmap.createBitmap(newWidth.toInt(), newHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            style = Paint.Style.FILL
        }
        canvas.drawPath(drawPath.asAndroidPath(), paint)

        //extract image
        val mode: PorterDuff.Mode = android.graphics.PorterDuff.Mode.SRC_IN
        paint.setXfermode(PorterDuffXfermode(mode))
        canvas.drawBitmap(sourceImgBitmap, 0f, 0f, paint)

        return mutableBitmap

    }

    private fun pasteSticker(originalBitmap: Bitmap, stickerBitmap: Bitmap, offset: Offset, originalWidth: Int, originalHeight: Int): Bitmap {

        val resultBitmap = Bitmap.createBitmap(originalWidth, originalHeight, originalBitmap.config)
        val canvas = Canvas(resultBitmap)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)  // Draw the large bitmap first

        val scaledSticker = Bitmap.createScaledBitmap(stickerBitmap, originalWidth , originalHeight , true)
        val x = (scaledSticker.width.toFloat()/stickerBitmap.width.toFloat())*offset.x
        val y = (scaledSticker.height.toFloat()/stickerBitmap.height.toFloat())*offset.y
        canvas.drawBitmap(scaledSticker, x, y, null)  // Draw the small bitmap at the specified offset

        return resultBitmap
    }

    private fun resizeBitmapWithAspectRatio(bitmap: Bitmap,newWidth: Int,newHeight: Int,offsetX: Float): Bitmap {

        var ratio: Float
        var scaledWidth: Float
        var scaledHeight: Float

        if(bitmap.height > bitmap.width){
            ratio = bitmap.height.toFloat() / bitmap.width.toFloat()

            scaledWidth = newWidth.toFloat()
            scaledHeight = newWidth.toFloat()*ratio

            if(scaledHeight.toInt() > newHeight){
                scaledHeight = newHeight.toFloat()
                scaledWidth = newHeight.toFloat()*(1/ratio)
            }

        }
        else{
            ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            scaledWidth = newWidth.toFloat()-offsetX
            scaledHeight = (newWidth.toFloat()-offsetX)*(1/ratio)
        }

        return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt() , scaledHeight.toInt() , true)
    }

}
