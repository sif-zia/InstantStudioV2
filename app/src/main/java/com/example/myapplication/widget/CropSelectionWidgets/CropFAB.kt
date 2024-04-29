package com.example.myapplication.widget.CropSelectionWidgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R


@Composable
fun CropFAB(modifier: Modifier = Modifier, onClick: () -> Unit) {
  FloatingActionButton(
    onClick = onClick,
    modifier = modifier
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_crop),
      contentDescription = stringResource(id = R.string.crop_alt)
    )
  }
}
