package com.example.myapplication.widget.CropSelectionWidgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R


@Composable
fun CropFAB(modifier: Modifier = Modifier, isPreview: Boolean, onClick: () -> Unit) {
  if(!isPreview) {
    FloatingActionButton(
      onClick = onClick,
      backgroundColor = Color.LightGray.copy(0.5f)
    ) {
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
            painter = painterResource(id = R.drawable.ic_crop),
            contentDescription = stringResource(id = R.string.crop_alt),
            modifier = Modifier.size(28.dp)
          )
          Text(
            text = "Preview",
            color = Color.Black,
            fontSize = 10.sp,
//                            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(5.dp) // Add padding at the bottom
          )
        }
      }
    }
  }
}
