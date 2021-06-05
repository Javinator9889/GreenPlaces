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
 * Created by Javinator9889 on 5/06/21 - Green Places.
 */
package com.javinator9889.greenplaces.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager


class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cls = with(PreferenceManager.getDefaultSharedPreferences(this)) {
            if (getBoolean(INTRO_DONE_KEY, false)) FirebaseUIActivity::class.java
            else IntroActivity::class.java
        }
        startActivity(Intent(this@LauncherActivity, cls))
        finish()
    }
}