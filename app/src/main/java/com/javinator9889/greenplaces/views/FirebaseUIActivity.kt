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
 * Created by Javinator9889 on 22/05/21 - Green Places.
 */
package com.javinator9889.greenplaces.views

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.datamodels.LoginModel


class FirebaseUIActivity : AppCompatActivity() {
    private val model = LoginModel(this)

    init {
        LoginModel.theme = R.style.Theme_GreenPlaces
        LoginModel.privacyUrl = "https://javinator9889.com"
        LoginModel.tosUrl = "https://javinator9889.com"
        LoginModel.logo = R.mipmap.ic_launcher_round

        lifecycleScope.launchWhenCreated {
            LoginModel.onLoginIntent = Intent(this@FirebaseUIActivity, MainActivity::class.java)
            FirebaseAuth.getInstance().currentUser?.let {
                Toast.makeText(
                    this@FirebaseUIActivity,
                    getString(R.string.welcomeback, it.displayName),
                    Toast.LENGTH_LONG
                ).show()
                startActivity(LoginModel.onLoginIntent)
                finish()
            } ?: model.registerActivity.launch(LoginModel.intent)
        }
    }
}