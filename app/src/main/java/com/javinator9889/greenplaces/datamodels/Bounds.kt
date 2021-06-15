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
 * Created by Javinator9889 on 14/6/21 - Green Places.
 */
package com.javinator9889.greenplaces.datamodels

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bounds(val northeast: LatLng, val southwest: LatLng) : Parcelable {
    companion object {
        fun fromBounds(bounds: LatLngBounds): Bounds = Bounds(bounds.northeast, bounds.southwest)
    }

    override fun toString(): String =
        "${northeast.latitude},${northeast.longitude},${southwest.latitude},${southwest.longitude}"
}
