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
package com.javinator9889.greenplaces.views

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.javinator9889.greenplaces.databinding.UploadImgBinding
import java.io.File


class ImageUploadActivity : AppCompatActivity() {
    companion object {
        internal const val IMAGE_EXTRA = "bundle:extras:image-src"
    }

    private lateinit var binding: UploadImgBinding
//    private lateinit var binding:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UploadImgBinding.inflate(layoutInflater).also { setContentView(it.root) }
        supportActionBar?.hide()

        val data = intent.extras ?: savedInstanceState
        if (data != null) {
            val file = File(
                data.getString(IMAGE_EXTRA) ?: throw IllegalStateException("No image was given")
            )
            binding.imageView.setImageBitmap(BitmapFactory.decodeStream(file.inputStream()))
        }
        binding.submit.setOnClickListener { finish() }
    }
}
