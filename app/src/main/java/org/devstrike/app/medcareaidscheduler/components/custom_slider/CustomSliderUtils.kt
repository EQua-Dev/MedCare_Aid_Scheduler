/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components.custom_slider

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import kotlin.math.roundToInt


fun customSliderMeasurePolicy(
    itemCount: Int,
    gap: Int,
    value: Float,
    startValue: Float
) = MeasurePolicy { measurables, constraints ->
    // Measure the thumb component and calculate its radius.
    val thumbPlaceable = measurables.first {
        it.layoutId == CustomSliderComponents.THUMB
    }.measure(constraints)
    val thumbRadius = (thumbPlaceable.width / 2).toFloat()

    val indicatorPlaceables = measurables.filter {
        it.layoutId == CustomSliderComponents.INDICATOR
    }.map { measurable ->
        measurable.measure(constraints)
    }
    val indicatorHeight = indicatorPlaceables.maxByOrNull { it.height }?.height ?: 0

    val sliderPlaceable = measurables.first {
        it.layoutId == CustomSliderComponents.SLIDER
    }.measure(constraints)
    val sliderHeight = sliderPlaceable.height

    val labelPlaceable = measurables.find {
        it.layoutId == CustomSliderComponents.LABEL
    }?.measure(constraints)
    val labelHeight = labelPlaceable?.height ?: 0

    // Calculate the total width and height of the custom slider layout
    val width = sliderPlaceable.width
    val height = labelHeight + sliderHeight + indicatorHeight

    // Calculate the available width for the track (excluding thumb radius on both sides).
    val trackWidth = width - (2 * thumbRadius)

    // Calculate the width of each section in the track.
    val sectionWidth = trackWidth / itemCount
    // Calculate the horizontal spacing between indicators.
    val indicatorSpacing = sectionWidth * gap

    // To calculate offset of the label, first we will calculate the progress of the slider
    // by subtracting startValue from the current value.
    // After that we will multiply this progress by the sectionWidth.
    // Add thumb radius to this resulting value.
    val labelOffset = (sectionWidth * (value - startValue)) + thumbRadius

    layout(width = width, height = height) {
        var indicatorOffsetX = thumbRadius
        // Place label at top.
        // We have to subtract the half width of the label from the labelOffset,
        // to place our label at the center.
        labelPlaceable?.placeRelative(
            x = (labelOffset - (labelPlaceable.width / 2)).roundToInt(),
            y = 0
        )

        // Place slider placeable below the label.
        sliderPlaceable.placeRelative(x = 0, y = labelHeight)

        // Place indicators below the slider.
        indicatorPlaceables.forEach { placeable ->
            // We have to subtract the half width of the each indicator from the indicatorOffset,
            // to place our indicators at the center.
            placeable.placeRelative(
                x = (indicatorOffsetX - (placeable.width / 2)).roundToInt(),
                y = labelHeight + sliderHeight
            )
            indicatorOffsetX += indicatorSpacing
        }
    }
}

@Composable
fun Label(
    modifier: Modifier = Modifier,
    value: Float,
    label: @Composable (labelValue: Int) -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        label(value.roundToInt())
    }
}

@Composable
fun Indicator(
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float>,
    gap: Int,
    indicator: @Composable (indicatorValue: Int) -> Unit
) {
    // Iterate over the value range and display indicators at regular intervals.
    for (i in valueRange.start.roundToInt()..valueRange.endInclusive.roundToInt() step gap) {
        Box(
            modifier = modifier
        ) {
            indicator(i)
        }
    }
}