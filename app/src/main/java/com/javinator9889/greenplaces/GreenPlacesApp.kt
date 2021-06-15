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
 * Created by Javinator9889 on 14/06/21 - Green Places.
 */
package com.javinator9889.greenplaces

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.javinator9889.greenplaces.utils.trees.CrashTree
import timber.log.Timber
import java.lang.ref.WeakReference

class GreenPlacesApp : Application() {
    companion object {
        private const val ERROR_MSG = "Application has not been started yet!"
        private lateinit var privInstance: WeakReference<GreenPlacesApp>
        var instance: GreenPlacesApp
            get() {
                if (!::privInstance.isInitialized)
                    throw IllegalStateException(ERROR_MSG)
                return privInstance.get() ?: throw IllegalStateException(ERROR_MSG)
            }
            set(value) { privInstance = WeakReference(value) }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        with(FirebaseAppCheck.getInstance()) {
            installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())
        }
        plantTree()
        instance = this
    }

    private fun plantTree() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(CrashTree)
    }
}