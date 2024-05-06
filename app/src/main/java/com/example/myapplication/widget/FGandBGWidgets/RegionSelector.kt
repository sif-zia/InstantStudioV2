package com.example.myapplication.widget.FGandBGWidgets


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.InputStream
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.getImageUri
import com.example.myapplication.loading
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import android.util.TypedValue
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun RegionSelctor(imageUri: Uri, onSelection: (String) -> Unit) {
//    val controller = rememberColorPickerController()
//    var selectedColor by remember { mutableStateOf(controller.selectedColor) }
//    LaunchedEffect(controller.selectedColor) {
//        selectedColor = controller.selectedColor
//    }

    var hexCodeRegion by remember { mutableStateOf("#000000") }
    System.out.println("Region PICKER WAS CALLED -----")
    val context = LocalContext.current
    var decodedImage: Bitmap? = uriToBitmap(context,imageUri)
    val transparentBlack = Color(0xFF0C203F)
    var heightBox by remember { mutableStateOf(100) }
    var widthBox by remember { mutableStateOf(100) }
    var count by remember { mutableStateOf(2) }
    androidx.compose.material.Surface(color = transparentBlack) {
        var yRatioForBitmap = remember {mutableStateOf(1f)}
        var xRatioForBitmap = remember {mutableStateOf(1f)}

        Column(
            modifier = Modifier
                .fillMaxSize(),
               // .padding(all = 30.dp),
          //  verticalArrangement = Arrangement.Center,
             horizontalAlignment = Alignment.CenterHorizontally
        ) {

       //     Box(
        //        modifier=Modifier.fillMaxHeight(0.8f)
        //            .fillMaxWidth()
        //            .background(Color.Yellow)

           // ){

                decodedImage?.let { bitmap ->

                  //  if(count==2) {
                   //    decodedImage =
                    //        resizeBitmapWithAspectRatio(decodedImage!!, widthBox, heightBox)
                   // }

                    var maxHeight = LocalConfiguration.current.screenHeightDp.dp * 0.7f
                    var maxWidth = LocalConfiguration.current.screenWidthDp.dp
                    var calcHeight = 0.dp
                    var calcWidth = 0.dp
                    var aspectRatio = 0f
                    if (bitmap.width > bitmap.height){
                        aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                        calcHeight = maxWidth / aspectRatio
                        calcWidth = maxWidth

                        if (calcHeight > maxHeight){
                            calcHeight = maxHeight
                            calcWidth = calcHeight * aspectRatio
                        }
                    }
                    else if (bitmap.height > bitmap.width){
                        aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
                        calcWidth = maxHeight / aspectRatio
                        calcHeight = maxHeight

                        if (calcWidth > maxWidth){
                            calcWidth = maxWidth
                            calcHeight = calcWidth * aspectRatio
                        }
                    }
                    else{
                        calcWidth = maxWidth
                        calcHeight = maxWidth
                    }


                    println("maxScreenWidth: " + maxWidth + " maxScreenHeight: " + maxHeight)
                    println("bitmap width: " + bitmap.width + "bitmap height: " + bitmap.height)
                    println("calculated width: " + calcWidth + "calculated height: " + calcHeight)
                    println("aspectRatio: " + aspectRatio)
                    //println("W::"+ decodedImage!!.width.toString())
                    //println("H::"+ decodedImage!!.height.toString())
                    Image(
                        //painter = rememberAsyncImagePainter(imageUri),
                       // alignment=Alignment.Center,
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        alignment = Alignment.TopStart,

                        modifier = Modifier
//                            drawWithContent {
//                                println("Wx::"+size.width)
//                                println("Hx::"+size.height)
//                                heightBox=size.height.toInt()
//                                widthBox=size.width.toInt()
//                                drawContent()
//                            }

                            //.padding(top = 84.dp)
                            .width(calcWidth)
                            .height(calcHeight)
                            //.align(Alignment.Center)
                            .onSizeChanged{
                                xRatioForBitmap.value = bitmap.width.toFloat() / it.width.toFloat()
                                yRatioForBitmap.value = bitmap.height.toFloat() / it.height.toFloat()
                            }
                            //.size(200.dp)
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    // Handle touch event here

//                                    System.out.println("DecIm Height: "+ decodedImage.height)
//                                    System.out.println("DecIm  Width: "+ decodedImage.width)
//                                    System.out.println("Box   Height: $heightBox")
//                                    System.out.println("Box    Width: $widthBox")
//                                    System.out.println("ofst  Height: "+ offset.y)
//                                    System.out.println("ofst   Width: "+ offset.x)
//                                    System.out.println("----------------------------------------------------")

                                  //  val x = (decodedImage.width.toFloat()/widthBox.toFloat())*offset.x
                                  //  val y = (decodedImage.height.toFloat()/heightBox.toFloat())*(offset.y  )//- dpToPx(context,30f)
                                //    val x = offset.x
                                 //   val y= offset.y

                                    val x = (offset.x * xRatioForBitmap.value).toInt()
                                    val y = (offset.y * yRatioForBitmap.value).toInt()
                                    println("offsetx: $x offsety: $y")
                                    // Get the color of the pixel
                                    val colorInt = bitmap.getPixel(x, y)
                                    val red = android.graphics.Color.red(colorInt)
                                    val green = android.graphics.Color.green(colorInt)
                                    val blue = android.graphics.Color.blue(colorInt)
                                    val alpha = android.graphics.Color.alpha(colorInt)
                                    // Do something with the color values
                                    // For example, log the color values
                                    println("Touched pixel color: (R:$red, G:$green, B:$blue)")
                                    println(colorInt.toString())
                                    val hexString = String.format("#%02X%02X%02X", red, green, blue)
                                    println(hexString)
                                    hexCodeRegion = hexString
                                    }
//                                    println("Hb:: "+y+"  Wb::"+x)
//
//
//                                    val region_hexCode = detectRegion(x, y , bitmap)
//                                    hexCodeRegion = region_hexCode
//
//
//                                    /*System.out.println(
//                                        "DETECTED USER TOUCH:: REGION_PIXEL IS: " + bitmap.getPixel(
//                                            x.toInt(),
//                                            y.toInt()
//                                        )
//                                    )*/
//                                    System.out.println("DETECTED USER TOUCH:: REGION_HEXCODE IS: " + region_hexCode)
//                                    System.out.println("DETECTED USER TOUCH:: REGION_X_Y IS: (" + x + "," + y + ")")
//                                    System.out.println(
//                                        "DETECTED USER TOUCH:: COLOR CONSTRUCTOR: " + android.graphics.Color.parseColor(
//                                            region_hexCode
//                                        )
//                                    )
                                    //val color: Color = Color(android.graphics.Color.parseColor(colorInt))
                                    // val color: Color = Color(0xFF123456)
                                      //selectedColor = mutableStateOf(color)
                                    System.out.println("=====================================================")
                                    //onSelection(region)

                            }
                    )
               // }
            }
            Spacer(modifier= Modifier.padding(15.dp))
            // Displaying region_hexCode color value in a separate box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(android.graphics.Color.parseColor(hexCodeRegion)))
                    .padding(4.dp)
            )

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val halfScreenWidth = (screenWidth / 2)
            var appbarColor = Color(25,56,106)
            Spacer(modifier= Modifier.padding(15.dp))
            Box(
                modifier = Modifier
                    .size(80.dp, 45.dp)
                    .background(appbarColor)
                    .padding(4.dp)
                    .clickable {
                        onSelection(hexCodeRegion)
                        System.out.println("HEX SELECTED ARE: " + hexCodeRegion)
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.Center, // Align text to the bottom
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Choose",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

        }

    }
}

fun detectRegion(x: Float, y: Float, bitmap: Bitmap): String {
    //return "#000000"
    if (x < 0 || y < 0 || x >= bitmap.width || y >= bitmap.height) {
        return "Out of bounds"
    }

    // Get the pixel value at the specified coordinates
    val pixel = bitmap.getPixel(x.toInt(), y.toInt())

    // Extract RGB components from the pixel value

    val r = getRedFromPixel(pixel)
    val g = getGreenFromPixel(pixel)
    val b = getBlueFromPixel(pixel)

    // Return the pixel value in hexadecimal format
    return String.format("#%02X%02X%02X", r, g, b)/**/
}

private fun getRedFromPixel(pixel: Int): Int {
    // Right shift the pixel value by 16 bits to isolate the red component
    // Bitwise AND with 0xFF to clear all other bits except the first 8 bits (red component)
    return (pixel shr 16) and 0xFF
}
private fun getGreenFromPixel(pixel: Int): Int {
    // Right shift the pixel value by 8 bits to isolate the green component
    // Bitwise AND with 0xFF to clear all other bits except the middle 8 bits (green component)
    return (pixel shr 8) and 0xFF
}
private fun getBlueFromPixel(pixel: Int): Int {
    // Bitwise AND with 0xFF to clear all bits except the last 8 bits (blue component)
    return pixel and 0xFF
}


fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    var inputStream: InputStream? = null
    try {
        inputStream = context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return null
}


fun dpToPx(context: Context, dp: Float): Int {
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
    return px.toInt()
}

/*
private fun resizeBitmapWithAspectRatio(bitmap: Bitmap,newWidth: Int,newHeight: Int): Bitmap {

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
        scaledWidth = newWidth.toFloat()
        scaledHeight = (newWidth.toFloat())*(1/ratio)
    }

    return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt() , scaledHeight.toInt() , true)
}
*/

/*
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlphaTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp)),
                controller = controller
            )
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(10.dp),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    val color: Color = colorEnvelope.color
                    val hex: String = colorEnvelope.hexCode
                    selectedColor = mutableStateOf(color)
                    hexCode = hex
                }
            )

            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp),
                controller = controller,
                tileOddColor = Color.White,
                tileEvenColor = Color.Black
            )

            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .height(35.dp),
                controller = controller,
            )
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val halfScreenWidth = (screenWidth / 2)
            var appbarColor = Color(25,56,106)
            Spacer(modifier= Modifier.padding(15.dp))
            Box(
                modifier = Modifier
                    .size(80.dp, 45.dp)
                    .background(appbarColor)
                    .padding(4.dp)
                    .clickable {
                        onColorSelected(selectedColor.value, hexCode)
                        System.out.println("VALUES SELECTED ARE: " + selectedColor)
                        System.out.println("HEX SELECTED ARE: " + hexCode)
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.Center, // Align text to the bottom
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Select",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
        */