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
package com.javinator9889.greenplaces.utils.coroutines

import kotlinx.coroutines.channels.Channel


enum class ConditionTypes {
    UNLOCK, LOCK
}

class Condition<T> {
    private val channel = Channel<T?>(0)

    suspend fun wait() = channel.receive()
    fun notify(type: T) = channel.trySend(type)
    suspend fun unlock() = channel.trySend(null)
}

internal infix fun <T> Condition<T>.send(type: T) = notify(type)
internal suspend infix fun <T> Condition<T>.waitFor(expr: (T) -> Boolean) {
    var done = false
    var v: T?
    while (!done) {
        v = wait()
        if (v != null)
            done = expr(v)
    }
}
internal suspend infix fun <T, R> Condition<T>.waitAndRun(fn: suspend (T) -> R): R? {
    val v = wait()
    if (v != null)
        return fn(v)
    return null
}
