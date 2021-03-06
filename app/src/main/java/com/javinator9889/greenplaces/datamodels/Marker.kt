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
 * Created by Javinator9889 on 15/6/21 - Green Places.
 */
package com.javinator9889.greenplaces.datamodels

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize


@Parcelize
data class Marker(
    val position: LatLng,
    @DrawableRes val icon: Int,
    val title: String,
    val description: String?,
    val md5sum: String
) : Parcelable
