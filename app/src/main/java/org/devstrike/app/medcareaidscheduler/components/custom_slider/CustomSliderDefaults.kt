/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components.custom_slider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.devstrike.app.medcareaidscheduler.ui.theme.PrimaryColor
import org.devstrike.app.medcareaidscheduler.ui.theme.TrackColor

object CustomSliderDefaults {

    /**
     * Object to hold defaults used by [CustomSlider]
     */

        /**
         * Composable function that represents the thumb of the slider.
         *
         * @param thumbValue The value to display on the thumb.
         * @param modifier The modifier for styling the thumb.
         * @param color The color of the thumb.
         * @param size The size of the thumb.
         * @param shape The shape of the thumb.
         */
        @Composable
        fun Thumb(
            thumbValue: String,
            modifier: Modifier = Modifier,
            color: Color = PrimaryColor,
            size: Dp = ThumbSize,
            shape: Shape = CircleShape,
            content: @Composable () -> Unit = {
                Text(
                    text = thumbValue,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        ) {
            Box(
                modifier = modifier
                    .thumb(size = size, shape = shape)
                    .background(color)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }

        /**
         * Composable function that represents the track of the slider.
         *
         * @param sliderPositions The positions of the slider.
         * @param modifier The modifier for styling the track.
         * @param trackColor The color of the track.
         * @param progressColor The color of the progress.
         * @param height The height of the track.
         * @param shape The shape of the track.
         */
        @Composable
        fun Track(
            sliderPositions: SliderPositions,
            modifier: Modifier = Modifier,
            trackColor: Color = TrackColor,
            progressColor: Color = PrimaryColor,
            height: Dp = TrackHeight,
            shape: Shape = CircleShape
        ) {
            Box(
                modifier = modifier
                    .track(height = height, shape = shape)
                    .background(trackColor)
            ) {
                Box(
                    modifier = Modifier
                        .progress(
                            sliderPositions = sliderPositions,
                            height = height,
                            shape = shape
                        )
                        .background(progressColor)
                )
            }
        }

        /**
         * Composable function that represents the indicator of the slider.
         *
         * @param indicatorValue The value to display as the indicator.
         * @param modifier The modifier for styling the indicator.
         * @param style The style of the indicator text.
         */
        @Composable
        fun Indicator(
            indicatorValue: String,
            modifier: Modifier = Modifier,
            style: TextStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal)
        ) {
            Box(modifier = modifier) {
                Text(
                    text = indicatorValue,
                    style = style,
                    textAlign = TextAlign.Center
                )
            }
        }

        /**
         * Composable function that represents the label of the slider.
         *
         * @param labelValue The value to display as the label.
         * @param modifier The modifier for styling the label.
         * @param style The style of the label text.
         */
        @Composable
        fun Label(
            labelValue: String,
            modifier: Modifier = Modifier,
            style: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
        ) {
            Box(modifier = modifier) {
                Text(
                    text = labelValue,
                    style = style,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    fun Modifier.track(
        height: Dp = TrackHeight,
        shape: Shape = CircleShape
    ) = fillMaxWidth()
        .heightIn(min = height)
        .clip(shape)

    fun Modifier.progress(
        sliderPositions: SliderPositions,
        height: Dp = TrackHeight,
        shape: Shape = CircleShape
    ) =
        fillMaxWidth(fraction = sliderPositions.activeRange.endInclusive - sliderPositions.activeRange.start)
            .heightIn(min = height)
            .clip(shape)

    fun Modifier.thumb(size: Dp = ThumbSize, shape: Shape = CircleShape) =
        defaultMinSize(minWidth = size, minHeight = size).clip(shape)

    enum class CustomSliderComponents {
        SLIDER, LABEL, INDICATOR, THUMB
    }


const val Gap = 1
val ValueRange = 0f..10f
private val TrackHeight = 8.dp
private val ThumbSize = 30.dp