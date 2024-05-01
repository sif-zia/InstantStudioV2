package com.example.myapplication.widget.CropSelectionWidgets

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.moyuru.cropify.AspectRatio
import com.example.myapplication.R

@Composable
fun AspectRatioPicker(
  selectedAspectRatio: AspectRatio?,
  modifier: Modifier = Modifier,
  onPicked: (AspectRatio?) -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    var isFlexible by remember { mutableStateOf(true) }
    val aspectRatioList = listOf(
      4 to 3,
      16 to 9,
      1 to 1,
      9 to 16,
      3 to 4,
    )
    aspectRatioList.forEach { (x, y) ->
      val aspectRatio = AspectRatio(x, y)
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
          .clickable { onPicked(aspectRatio) }
          .padding(8.dp)
      ) {
        Crossfade(
          targetState = if (selectedAspectRatio == aspectRatio)
            Color(25,56,106)
          else
            MaterialTheme.colorScheme.surface
        ) { color ->
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)
          ) {
            Spacer(
              modifier = Modifier
                .aspectRatio(x / y.toFloat())
                .border(2.dp, color)
            )
          }
          if(selectedAspectRatio == aspectRatio){
            isFlexible = false
          }
        }
        Text(text = "$x:$y", color= Color.White)
      }
    }
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier
        .clickable {
          onPicked(null)
          isFlexible = true
        }
        .padding(8.dp)
    ) {
      val ccolor =  if (isFlexible) Color(25,56,106) else Color.White
      Icon(painter = painterResource(id = R.drawable.outline_lens_blur_24), contentDescription = null, modifier = Modifier.size(40.dp), tint = ccolor)
      Text(text = stringResource(id = R.string.flexible), color = Color.White)
    }
  }
}
