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
package com.javinator9889.greenplaces.utils.extensions

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.core.util.containsKey
import androidx.core.util.forEach
import androidx.core.util.size

//public operator fun SparseIntArray.contains(key: Int): Boolean = indexOfKey(key) >= 0
//public operator fun <E> SparseArray<E>.contains(key: Int): Boolean = containsKey(key)
/** Returns true if the collection contains [key]. */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") /* contains() added in R */
public infix fun <T> SparseArray<T>.contains(key: Int) = containsKey(key)
public infix fun <T> SparseArray<T>.notContains(key: Int) = !containsKey(key)
//public operator fun <T> SparseArray<T>.contains(key: Int): Boolean = indexOfKey(key) >= 0

public fun <T> SparseArray<T>.asList(): List<T> {
    val arr = ArrayList<T>(size)
    forEach { _, item -> arr.add(item) }
    return arr
}
public operator fun <T> SparseArray<T>.minusAssign(elements: Iterable<Int>) {
    elements.forEach { delete(it) }
}