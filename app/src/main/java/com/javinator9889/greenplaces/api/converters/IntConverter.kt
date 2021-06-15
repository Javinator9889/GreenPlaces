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
package com.javinator9889.greenplaces.api.converters

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import timber.log.Timber


@Target(AnnotationTarget.FIELD)
annotation class KlaxonInt

val intConverter = object : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == String::class.java
    override fun fromJson(jv: JsonValue): Any =
        if (jv.string != null)
            runCatching { Integer.parseInt(jv.string!!) }.getOrDefault(0)
        else
            throw KlaxonException("Could not parse int ${jv.string}")

    override fun toJson(value: Any): String = """ { "aqi": $value } """
}