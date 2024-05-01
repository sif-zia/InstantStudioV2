package com.example.myapplication.widget.CropSelectionWidgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.moyuru.cropify.CropifyOption
import com.example.myapplication.R
import com.example.myapplication.appbarColor

@Composable
fun CropifyOptionSelector(
  option: CropifyOption,
  onOptionChanged: (CropifyOption) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
  ) {
    HeadlineS(text = stringResource(id = R.string.frame))

    TitleM(text = stringResource(id = R.string.frame_color))
    ColorPicker(selectedColor = option.frameColor, onPicked = {onOptionChanged(option.copy(frameColor = it , gridColor = it)) })

    TitleM(text = stringResource(id = R.string.frame_alpha))
    Slider(
      value = option.frameAlpha,
      onValueChange = {onOptionChanged(option.copy(frameAlpha = it, gridAlpha = it)) },
      steps = 10, // Optional: Define the number of steps in the range
      colors = SliderDefaults.colors(
        thumbColor = appbarColor, // Set the color of the thumb
        activeTrackColor = appbarColor, // Set the color of the active track
        inactiveTrackColor = Color.White // Set the color of the inactive track
      ))

    TitleM(text = stringResource(id = R.string.frame_width))
    Slider(
      value = option.frameWidth.value,
      onValueChange = { onOptionChanged(option.copy(frameWidth = it.dp, gridWidth = it.dp))},
      valueRange = 1f..12f,
      steps = 10, // Optional: Define the number of steps in the range
      colors = SliderDefaults.colors(
        thumbColor = appbarColor, // Set the color of the thumb
        activeTrackColor = appbarColor, // Set the color of the active track
        inactiveTrackColor = Color.White // Set the color of the inactive track
      )
    )
    TitleM(text = stringResource(id = R.string.frame_aspect_ratio))
    AspectRatioPicker(selectedAspectRatio = option.frameAspectRatio) {
      onOptionChanged(option.copy(frameAspectRatio = it))
    }

//    Space(dp = 16.dp)
//
//    HeadlineS(text = stringResource(id = R.string.grid))
//    TitleM(text = stringResource(id = R.string.grid_color))
//    ColorPicker(selectedColor = option.gridColor) { onOptionChanged(option.copy(gridColor = it)) }
//    TitleM(text = stringResource(id = R.string.grid_alpha))
//    Slider(value = option.gridAlpha, onValueChange = { onOptionChanged(option.copy(gridAlpha = it)) })
//    TitleM(text = stringResource(id = R.string.grid_width))
//    Slider(
//      value = option.gridWidth.value,
//      onValueChange = { onOptionChanged(option.copy(gridWidth = it.dp)) },
//      valueRange = 1f..12f
//    )

    Space(dp = 16.dp)

    HeadlineS(text = stringResource(id = R.string.mask))
    TitleM(text = stringResource(id = R.string.mask_color))
    ColorPicker(selectedColor = option.maskColor) { onOptionChanged(option.copy(maskColor = it)) }
    TitleM(text = stringResource(id = R.string.mask_alpha))
    Slider(
      value = option.maskAlpha,
      onValueChange = { onOptionChanged(option.copy(maskAlpha = it))} ,
        steps = 10, // Optional: Define the number of steps in the range
        colors = SliderDefaults.colors(
          thumbColor = appbarColor, // Set the color of the thumb
          activeTrackColor = appbarColor, // Set the color of the active track
          inactiveTrackColor = Color.White // Set the color of the inactive track
        )
    )

//    Space(dp = 16.dp)
//
//    HeadlineS(text = stringResource(id = R.string.background))
//    TitleM(text = stringResource(id = R.string.background_color))
//    ColorPicker(selectedColor = option.backgroundColor) { onOptionChanged(option.copy(backgroundColor = it)) }
  }
}
