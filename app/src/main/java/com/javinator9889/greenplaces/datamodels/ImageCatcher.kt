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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImageCatcher(ts: String, storage: File?, private val context: Context) {
    val file = File.createTempFile("JPEG_${ts}_", ".jpg", storage)
    val currentPhotoPath: String = file.absolutePath

    fun photoURI(provider: String) = FileProvider.getUriForFile(context, provider, file)

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun createImageFile(context: Context): ImageCatcher {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return ImageCatcher(timeStamp, storageDir, context)
        }
    }
}