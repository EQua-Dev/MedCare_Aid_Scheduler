/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components.custom_slider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderPositions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import org.devstrike.app.medcareaidscheduler.components.custom_slider.CustomSliderDefaults.Indicator
import org.devstrike.app.medcareaidscheduler.components.custom_slider.CustomSliderDefaults.Label
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = ValueRange,
    gap: Int = Gap,
    showIndicator: Boolean = false,
    showLabel: Boolean = false,
    enabled: Boolean = true,
    thumb: @Composable (thumbValue: Int) -> Unit = {
        CustomSliderDefaults.Thumb(it.toString())
    },
    track: @Composable (sliderPositions: SliderPositions) -> Unit = { sliderPositions ->
        CustomSliderDefaults.Track(sliderPositions = sliderPositions)
    },
    indicator: @Composable (indicatorValue: Int) -> Unit = { indicatorValue ->
        CustomSliderDefaults.Indicator(indicatorValue = indicatorValue.toString())
    },
    label: @Composable (labelValue: Int) -> Unit = { labelValue ->
        CustomSliderDefaults.Label(labelValue = labelValue.toString())
    }
) {
    // Implementation details

    val itemCount = (valueRange.endInclusive - valueRange.start).roundToInt()
    val steps = if (gap == 1) 0 else (itemCount / gap - 1)

    Box(modifier = modifier) {
        Layout(
            measurePolicy = customSliderMeasurePolicy(
                itemCount = itemCount,
                gap = gap,
                value = value,
                startValue = valueRange.start
            ),
            content = {
                if (showLabel)
                    Label(
                        modifier = Modifier.layoutId(CustomSliderComponents.LABEL),
                        value = value,
                        label = label
                    )

                Box(modifier = Modifier.layoutId(CustomSliderComponents.THUMB)) {
                    thumb(value.roundToInt())
                }

                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId(CustomSliderComponents.SLIDER),
                    value = value,
                    valueRange = valueRange,
                    steps = steps,
                    onValueChange = { onValueChange(it) },
                    thumb = {
                        thumb(value.roundToInt())
                    },
                    track = { track(it) },
                    enabled = enabled
                )

                if (showIndicator)
                    Indicator(
                        modifier = Modifier.layoutId(CustomSliderComponents.INDICATOR),
                        valueRange = valueRange,
                        gap = gap,
                        indicator = indicator
                    )
            })
    }
}