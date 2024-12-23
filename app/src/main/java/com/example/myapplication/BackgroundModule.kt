package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.myapplication.widget.CommonAppBar
import com.example.myapplication.widget.FGandBGWidgets.ColorPicker
import com.example.myapplication.widget.FGandBGWidgets.RegionSelctor
import com.example.myapplication.widget.FGandBGWidgets.buildFormBody
import com.example.myapplication.widget.FGandBGWidgets.buildRequest
import com.example.myapplication.widget.FGandBGWidgets.compressImageToByteArray
import com.example.myapplication.widget.BGWidgets.convertHexToRGB
import com.example.myapplication.widget.FGandBGWidgets.decodeBase64Image
import com.example.myapplication.widget.FGandBGWidgets.returnClientBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

var loading = mutableStateOf(false)

class BackgroundModule : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var imageUri: Uri? = intent?.getParcelableExtra("imageUri")
        var tempImageUri:Uri? = intent?.getParcelableExtra("imageUri")
        var tempImageUriRegion:Uri? = intent?.getParcelableExtra("imageUri")
        setContent {
            var applyButtonChecker by remember {  mutableStateOf(false) }
            val painter = painterResource(id = R.drawable.b1)
            var fgImage by remember { mutableStateOf<ImageBitmap?>(null) }

            val context = LocalContext.current
            val bgc = painterResource(R.drawable.baseline_account_box_24)
            val adv = painterResource(R.drawable.baseline_color_lens_24)
            val pen = painterResource(R.drawable.color_dropper)
            var isBoxVisible by remember { mutableStateOf(true) }
            var isBoxVisible2 by remember { mutableStateOf(true) }
            var isBoxVisible3 by remember { mutableStateOf(true) }
            var isBoxVisible4 by remember { mutableStateOf(true) }
            var isBoxVisible5 by remember { mutableStateOf(true) }
            var isBoxVisible6 by remember { mutableStateOf(true) }
            var isBoxVisible7 by remember { mutableStateOf(true) }
            var isBoxVisible8 by remember { mutableStateOf(true) }
            var isBoxVisible9 by remember { mutableStateOf(true) }

            val gradientcolors = listOf(
                Color.Transparent,  Color.Transparent, Color.Black.copy(alpha = 0.3f)
            )
            var bgColor = Color(12,32,63)
            var appbarColor = Color(25,56,106)


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
            )

            var decodedImage: Bitmap? = null
            var isColorPickerVisible by remember { mutableStateOf(false) }
            var isConfirmationVisible by remember { mutableStateOf(false) }
            var isRegionSelectorVisible by remember { mutableStateOf(false) }
            var selectedColor by remember { mutableStateOf(Color.Transparent) }
            var hexCode by remember { mutableStateOf("") }
            var hexCodeRegion by remember { mutableStateOf("") }
            var temphexVal by remember { mutableStateOf("#FFFFFF") }
            var temphexVal2 by remember { mutableStateOf("#FFFFFF") }
            var applyChanges by remember { mutableStateOf(false) }
            if(loading.value){
                Dialog(
                    onDismissRequest = {  },
                    DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                ) {
                    Box(
                        contentAlignment= Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                    ) {
                        CircularProgressIndicator(
                            color = Color.LightGray,
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier.size(50.dp),
                        )
                    }
                }
            }

            CommonAppBar(title = "Background Color", modifier = Modifier.background(color = Color.DarkGray))

            Box(modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(gradientcolors))) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp), // Add vertical scroll
                    verticalArrangement = Arrangement.Top, // Align the content at the top
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 84.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f)
                        )
                    }
                }
                if (isColorPickerVisible) {
                    ColorPicker { color, hex ->
                        selectedColor = color
                        hexCode = hex
                        isColorPickerVisible = false
                        temphexVal ="#"+ hex.substring(2)
                    }
                }
                else{
                    isBoxVisible=true
                    isBoxVisible2=true
                    isBoxVisible7=true
                }

                if (isConfirmationVisible) {
                    tempImageUri?.let {
                        ImageConfirmationSurface (imageUri= it){ boolFlag ->
                            applyChanges=boolFlag
                            isConfirmationVisible=false
                        }
                    }
                }
                else{
                    isBoxVisible3=true
                    isBoxVisible4=true
                    isBoxVisible8=true
                }

                if (isRegionSelectorVisible) {
                    tempImageUriRegion?.let {
                        RegionSelctor (imageUri= it){ hexCodeRegionSelected ->
                            hexCodeRegion=hexCodeRegionSelected
                            temphexVal2 = hexCodeRegion
                            hexCodeRegion ="FF" + hexCodeRegion.substring(1).replace("#", "")
                            System.out.println("USER CONFIRMED SELECTED REGION HEXCODE: " + hexCodeRegion)
                            isRegionSelectorVisible=false
                        }
                    }
                }
                else{
                    isBoxVisible5=true
                    isBoxVisible6=true
                    isBoxVisible9=true
                }

            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val halfScreenWidth = (screenWidth / 2)
                System.out.println("HALFSCREENWIDTH= "+halfScreenWidth)
                Spacer(modifier = Modifier
                    .weight(0.2f)
                    .width(halfScreenWidth * 4))
                LazyRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(18.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {

                    item {
                        if (isBoxVisible2 && isBoxVisible4 && isBoxVisible6) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .width(100.dp)
                                    .background(appbarColor)
                                    .clickable
                                    {
                                        if (hexCode.length == 8) {
                                            isBoxVisible2 = false
                                            isBoxVisible = false
                                            val byteArray = compressImageToByteArray(context)
                                            var rgbArray = convertHexToRGB(hexCode)
                                            var rgbArray_to_change = convertHexToRGB(hexCodeRegion)
                                            var encoded =
                                                Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            val client = byteArray?.let { returnClientBuilder(it) }
                                            val formBody = buildFormBody(encoded, rgbArray, "True",rgbArray_to_change)
                                            val request = buildRequest(formBody)
                                            System.out.println(" " + formBody + "\n" + request + "\nABOUT TO CALL")
                                            if (client != null) {
                                                client
                                                    .newCall(request)
                                                    .enqueue(object : Callback {
                                                        override fun onFailure(
                                                            call: Call,
                                                            e: IOException
                                                        ) {
                                                            e.printStackTrace()
                                                        }

                                                        override fun onResponse(
                                                            call: Call,
                                                            response: Response
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                System.out.println("SUCCESFULLL!!!!!!!!!!")
                                                                var receivedResponse =
                                                                    response.body?.string() ?: ""
                                                                Log.d(
                                                                    "Received Response",
                                                                    "receivedResponse: $receivedResponse"
                                                                )
                                                                decodedImage =
                                                                    decodeBase64Image(receivedResponse)

                                                                val handler =
                                                                    Handler(Looper.getMainLooper())
                                                                handler.post {
                                                                    decodedImage?.let {
                                                                        tempImageUri =
                                                                            getImageUri(context, it)


                                                                    } ?: run {
                                                                        Log.e(
                                                                            "Bitmap Error",
                                                                            "Failed to decode the image."
                                                                        )
                                                                    }
                                                                    loading.value = false
                                                                    isBoxVisible3 = false
                                                                    isBoxVisible4 = false
                                                                    isBoxVisible5 = false
                                                                    isBoxVisible6 = false
                                                                    isBoxVisible7 = false
                                                                    isBoxVisible8 = false
                                                                    isBoxVisible9 = false
                                                                    isConfirmationVisible = true
                                                                }
                                                            }
                                                            System.out.println("WAS IT SUCCESFULLL????????")
                                                        }
                                                    })
                                                loading.value = true
                                            }
                                        } else {
                                            var str_msg=""
//                                            if(hexCode.length!=8 && hexCodeRegion.length!=8){
//                                                str_msg="Select Color & Region First!"
//                                            } else
                                            if(hexCode.length!=8 ){
                                                str_msg="Select Color Also!"
                                            }
//                                            else if(hexCodeRegion.length!=8 ){
//                                                str_msg="Select Region Also!"
//                                            }

                                            Toast
                                                .makeText(
                                                    context,
                                                    str_msg,
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    }

                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Bottom, // Align text to the bottom
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
                                        text = "Apply Color",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // ADD SPACER HERE
                        if (isBoxVisible && isBoxVisible3 && isBoxVisible5 ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .width(100.dp)
                                    .background(appbarColor)
                                    .clickable {
                                        isBoxVisible2 = false
                                        isBoxVisible = false
                                        isBoxVisible5 = false
                                        isBoxVisible6 = false
                                        isBoxVisible7 = false
                                        isBoxVisible8 = false
                                        isBoxVisible9 = false
                                        isColorPickerVisible = true
                                    }
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = adv,
                                        colorFilter = ColorFilter.tint(Color(android.graphics.Color.parseColor(temphexVal))),
                                        contentDescription = "Your Icon Description",
                                        modifier = Modifier
                                            .size(28.dp)
                                    )
                                    Text(
                                        text = "Color Picker",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // ADD SPACER HERE
                        if (isBoxVisible7 && isBoxVisible8 && isBoxVisible9) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .width(100.dp)
                                    .background(appbarColor)
                                    .clickable {
                                        isBoxVisible = false
                                        isBoxVisible2 = false
                                        isBoxVisible3 = false
                                        isBoxVisible4 = false
                                        isBoxVisible5 = false
                                        isBoxVisible6 = false
                                        isBoxVisible7 = false
                                        isBoxVisible8 = false
                                        isBoxVisible9 = false
                                        isRegionSelectorVisible = true
                                    }
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = pen,
                                        colorFilter = ColorFilter.tint(Color(android.graphics.Color.parseColor(temphexVal2))),
                                        contentDescription = "Your Icon Description",
                                        modifier = Modifier
                                            .size(28.dp)
                                    )
                                    Text(
                                        text = "Select Region",
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
    }




    @Composable
    fun ImageConfirmationSurface(imageUri:Uri,onConfirmation: (Boolean) -> Unit ) {

        var bgColor = Color(12,32,63)

        val transparentGrey = Color(0xFF2C2E2D)
        androidx.compose.material.Surface(color = transparentGrey) {
            CommonAppBar(title = "Confirmation Screen", modifier = Modifier.background(color = Color.DarkGray))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(horizontal = 16.dp), // Add vertical scroll
                verticalArrangement = Arrangement.Top, // Align the content at the top
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 84.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                )
            }

            val cancel_image = painterResource(R.drawable.cancel_button)
            val done_image = painterResource(R.drawable.baseline_check_24)

            Box(modifier = Modifier.fillMaxSize()) {
                // Other content goes here

                // Place the LazyRow just above the bottom of the screen

            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val halfScreenWidth = (screenWidth / 2)
                System.out.println("HALFSCREENWIDTH= "+halfScreenWidth)
                Spacer(modifier = Modifier.weight(0.2f).width(halfScreenWidth*4))
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
                                    .clickable  {
                                        onConfirmation(false)
                                    }

                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.editcancel),
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
                        // ADD SPACER HERE

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(appbarColor)
                                    .clickable  {
                                        val resultIntent = Intent().apply {
                                            putExtra("bgImageUri", imageUri)
                                        }
                                        setResult(ComponentActivity.RESULT_OK, resultIntent)
                                        finish()
                                    }
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Bottom, // Align text to the bottom
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.editcheck),
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










        }   }
    }


}