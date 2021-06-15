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

import okhttp3.internal.format
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

fun File.md5(): String {
    val digest = MessageDigest.getInstance("MD5")
    this.forEachBlock(8192) { buffer, bytesRead ->
        digest.update(buffer, 0, bytesRead)
    }
    val md5sum = digest.digest().run { BigInteger(1, this) }.run { toString(16) }
    return format("%32s", md5sum).replace(' ', '0')
}
