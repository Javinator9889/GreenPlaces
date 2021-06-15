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

import android.util.SparseArray
import androidx.core.util.set
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.heatmaps.WeightedLatLng
import com.javinator9889.greenplaces.api.WAQIApi
import com.javinator9889.greenplaces.datamodels.Bounds
import com.javinator9889.greenplaces.utils.coroutines.Condition
import com.javinator9889.greenplaces.utils.coroutines.send
import com.javinator9889.greenplaces.utils.coroutines.waitAndRun
import com.javinator9889.greenplaces.utils.extensions.asList
import com.javinator9889.greenplaces.utils.extensions.minusAssign
import com.javinator9889.greenplaces.utils.extensions.notContains
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HeatMapsModel : ViewModel() {
    private val condition = Condition<Bounds>()
    private val uids: MutableSet<Int> = HashSet()
    private val data = SparseArray<WeightedLatLng>()
    var alive: Boolean = true
        get() = synchronized(this) {
            return@synchronized field
        }
        set(value) = synchronized(this) {
            field = value
        }

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    val heatMaps: MutableLiveData<List<WeightedLatLng>> by lazy {
        MutableLiveData<List<WeightedLatLng>>()
    }

    fun toggleRun(bounds: LatLngBounds) = toggleRun(Bounds.fromBounds(bounds))

    fun toggleRun(bounds: Bounds) {
        condition send bounds
    }

    override fun onCleared() {
        alive = false
        viewModelScope.launch { condition.unlock() }
    }

    private suspend fun loadData() {
        while (alive) {
            condition waitAndRun {
                Timber.d("Running task!")
                val api = withContext(Dispatchers.IO) {
                    WAQIApi.fromBounds(it)
                }
                Timber.d("API response: $api")
                if (api.data.isNotEmpty()) {
                    val points = HashSet(api.data.map {
                        if (data notContains it.uid) {
                            data[it.uid] = WeightedLatLng(LatLng(it.lat, it.lon), it.aqi.toDouble())
                            uids += it.uid
                        }
                        it.uid
                    })
                    Timber.d("Obtained points: $points")
                    Timber.d("Registered UIDs: $uids")
                    val diff = uids subtract points
                    Timber.d("Difference in between sets: $diff")
                    uids -= diff
                    data -= diff
                    heatMaps.postValue(data.asList())
                }
            }
        }
    }
}