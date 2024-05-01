package com.example.myapplication.widget.CropSelectionWidgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
  Box(
    modifier = Modifier
      .size(60.dp)
      .background(Color(25,56,106))
      .clickable { onClick() }
  ) {
    Column(
      verticalArrangement = Arrangement.Bottom,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()
    ) {
      Image(
        painter = painterResource(id = R.drawable.baseline_crop_free_24),
        contentDescription = "Preview",
        modifier = Modifier
          .size(28.dp)

      )
      Text(
        text = "Preview",
        color = Color.White,
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(5.dp)
      )
    }
  }
}
