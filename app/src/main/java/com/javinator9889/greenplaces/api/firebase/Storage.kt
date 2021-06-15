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
package com.javinator9889.greenplaces.api.firebase

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.datamodels.Marker
import com.javinator9889.greenplaces.utils.extensions.await
import com.javinator9889.greenplaces.utils.extensions.md5
import timber.log.Timber
import java.io.File

object Storage {
    private val storage = Firebase.storage
    private val reference = storage.reference
    private val images = reference.child("images")

    suspend fun uploadImage(
        img: File,
        location: LatLng,
        title: String,
        description: String?,
        progressListener: ((UploadTask.TaskSnapshot) -> Unit)? = null
    ): String {
        val md5sum = img.md5()
        val ref = images.child(md5sum)
        val task = ref.putStream(img.inputStream(), storageMetadata {
            contentType = "image/jpg"
            setCustomMetadata("Md5Hash", md5sum)
        })
        progressListener?.let { task.addOnProgressListener(it) }
        task.await()
        val imageData =
            hashMapOf(
                "hash" to md5sum,
                "lat" to location.latitude,
                "lng" to location.longitude,
                "title" to title,
                "description" to description
            )
        with(Firebase.firestore.collection("images")) {
            add(imageData).await()
        }
        return md5sum
    }

    suspend fun downloadImage(hash: String): Pair<StorageReference, LatLng>? {
        val location = with(Firebase.firestore.collection("images").whereEqualTo("hash", hash)) {
            Timber.d("Got query $this")
            val doc = get().await().documents[0]
            if (!doc.exists())
                return null
            LatLng(doc.getDouble("lat")!!, doc.getDouble("lng")!!)
        }
        return images.child(hash) to location
    }

    suspend fun images(): List<Marker> {
        with(Firebase.firestore.collection("images")) {
            val docs = get().await()
            return docs.documents.map {
                Marker(
                    position = LatLng(it.getDouble("lat")!!, it.getDouble("lng")!!),
                    icon = R.drawable.plant,
                    title = it.getString("title")!!,
                    description = it.getString("description"),
                    md5sum = it.getString("hash")!!
                )
            }
        }
    }
}