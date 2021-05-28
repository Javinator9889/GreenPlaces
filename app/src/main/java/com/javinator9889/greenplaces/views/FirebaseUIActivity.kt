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

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.javinator9889.greenplaces.R


class FirebaseUIActivity : AppCompatActivity() {
    init {
        lifecycleScope.launchWhenCreated {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.GitHubBuilder().build()
            )

            val activityRegister =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                    val response = IdpResponse.fromResultIntent(res.data)
                    if (res.resultCode == Activity.RESULT_OK) {
                        val user = FirebaseAuth.getInstance().currentUser
                        Toast.makeText(
                            this@FirebaseUIActivity, "User $user signed in " +
                                    "with response $response", Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            val intent = with(AuthUI.getInstance().createSignInIntentBuilder()) {
                setAvailableProviders(providers)
                setTheme(R.style.Theme_GreenPlaces)
                setTosAndPrivacyPolicyUrls(
                    "https://javinator9889.com",
                    "https://javinator9889.com"
                )
                build()
            }
            activityRegister.launch(intent)
        }
    }
}