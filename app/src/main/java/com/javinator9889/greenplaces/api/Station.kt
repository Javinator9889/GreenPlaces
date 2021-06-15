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
 * Created by Javinator9889 on 14/06/21 - Green Places.
 */
package com.javinator9889.greenplaces.api

import android.os.Parcelable
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.google.android.gms.maps.model.LatLng
import com.javinator9889.greenplaces.api.converters.KlaxonDate
import com.javinator9889.greenplaces.api.converters.KlaxonInt
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Parcelize
data class StationData(
    val lat: Double,
    val lon: Double,
    val uid: Int,
    @KlaxonInt
    val aqi: Int,
    val station: Station
) : Parcelable


@Parcelize
data class Station(val name: String, @KlaxonDate val time: LocalDateTime) : Parcelable