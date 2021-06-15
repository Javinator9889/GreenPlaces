/*
 * Copyright © 2021 - present | Green Places by Javinator9889
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 *
 * Created by Javinator9889 on 5/06/21 - Green Places.
 */
package com.javinator9889.greenplaces.datamodels.builders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LottieProperties(
    var startFrame: Int,
    var endFrame: Int,
    var progress: Float,
    var scale: Float,
    var speed: Float,
    var animate: Boolean,
    var loop: Boolean
) : Parcelable

fun lottieProperties(block: LottiePropertiesBuilder.() -> Unit) =
    LottiePropertiesBuilder().apply(block).build()

class LottiePropertiesBuilder {
    var startFrame = 0
    var endFrame = Integer.MAX_VALUE
    var progress = 0F
    var scale = 1F
    var speed = 1F
    var animate = true
    var loop = false

    fun build() = LottieProperties(startFrame, endFrame, progress, scale, speed, animate, loop)
}