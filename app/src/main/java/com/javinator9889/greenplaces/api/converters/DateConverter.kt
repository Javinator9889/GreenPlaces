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
package com.javinator9889.greenplaces.api.converters

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Target(AnnotationTarget.FIELD)
annotation class KlaxonDate

val dateConverter = object : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == LocalDateTime::class.java
    override fun fromJson(jv: JsonValue): Any =
        if (jv.string != null)
            LocalDateTime.parse(jv.string, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        else
            throw KlaxonException("Could not parse date ${jv.string}")

    override fun toJson(value: Any): String = """ { "time": $value } """
}