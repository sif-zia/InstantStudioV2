package com.example.myapplication.widget.FGandBGWidgets

import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun ColorPicker(onColorSelected: (Color, String) -> Unit) {
    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf(controller.selectedColor) }
    var hexCode by remember { mutableStateOf("") }
    System.out.println("COLOR PICKER WAS CALLED -----")
    LaunchedEffect(controller.selectedColor) {
        selectedColor = controller.selectedColor
    }

//    val transparentBlack = Color(0xBB000000)
    val transparentBlack = Color(0xBB0C203F)

    androidx.compose.material.Surface(color = transparentBlack) {

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
            Spacer(modifier=Modifier.padding(15.dp))
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

    }
}