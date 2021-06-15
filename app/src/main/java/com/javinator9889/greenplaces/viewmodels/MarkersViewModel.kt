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
package com.javinator9889.greenplaces.viewmodels

import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLngBounds
import com.javinator9889.greenplaces.api.firebase.Storage
import com.javinator9889.greenplaces.datamodels.Bounds
import com.javinator9889.greenplaces.datamodels.Marker
import com.javinator9889.greenplaces.utils.coroutines.Condition
import com.javinator9889.greenplaces.utils.coroutines.send
import com.javinator9889.greenplaces.utils.coroutines.waitAndRun
import kotlinx.coroutines.launch
import timber.log.Timber


class MarkersViewModel : ViewModel() {
    private val condition = Condition<LatLngBounds>()
    private val data = ArrayMap<String, Marker>()
    val markers: MutableLiveData<Marker> by lazy { MutableLiveData() }
    var alive: Boolean = true
        get() = synchronized(this) {
            return@synchronized field
        }
        set(value) = synchronized(this) {
            field = value
        }

    init {
        viewModelScope.launch { loadData() }
    }

    fun toggleRun(bounds: LatLngBounds) = condition send bounds

    fun toggleRun(bounds: Bounds) = toggleRun(LatLngBounds(bounds.southwest, bounds.northeast))

    private suspend fun loadData() {
        while (alive) {
            condition waitAndRun { bounds ->
                Timber.d("Running marker task")
                val remoteMarkers = Storage.images()
                val newMarkers = HashSet<Marker>()
                remoteMarkers.forEach { marker ->
                    if (marker.md5sum !in data && marker.position in bounds) {
                        data[marker.md5sum] = marker
                        newMarkers.add(marker)
                    }
                }
                newMarkers.forEach { markers.postValue(it) }
            }
        }
    }
}