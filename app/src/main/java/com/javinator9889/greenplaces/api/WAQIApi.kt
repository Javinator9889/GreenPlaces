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
package com.javinator9889.greenplaces.api

import android.os.Parcelable
import com.beust.klaxon.Klaxon
import com.google.android.gms.maps.model.LatLngBounds
import com.javinator9889.greenplaces.BuildConfig
import com.javinator9889.greenplaces.api.converters.KlaxonDate
import com.javinator9889.greenplaces.api.converters.KlaxonInt
import com.javinator9889.greenplaces.api.converters.dateConverter
import com.javinator9889.greenplaces.api.converters.intConverter
import com.javinator9889.greenplaces.datamodels.Bounds
import kotlinx.parcelize.Parcelize
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException


@Parcelize
data class WAQIApi(val status: String, val data: List<StationData>) : Parcelable {
    companion object {
        private val client = OkHttpClient()

        fun fromBounds(bounds: LatLngBounds): WAQIApi = fromBounds(Bounds.fromBounds(bounds))

        fun fromBounds(bounds: Bounds): WAQIApi {
            val req = with(Request.Builder()) {
                url("https://api.waqi.info/map/bounds/?token=${BuildConfig.WQAPI_API_KEY}&latlng=${bounds}")
                build()
            }

            client.newCall(req).execute().use { res ->
                if (!res.isSuccessful) throw IOException("Unexpected code $res")

                return with(Klaxon()) {
                    fieldConverter(KlaxonDate::class, dateConverter)
                    fieldConverter(KlaxonInt::class, intConverter)
                    parse<WAQIApi>(res.body!!.charStream())
                        ?: throw IllegalStateException("Body is empty!")
                }
            }
        }
    }
}
