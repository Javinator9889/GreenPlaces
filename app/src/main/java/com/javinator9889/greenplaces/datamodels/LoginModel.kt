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
 * Created by Javinator9889 on 28/05/21 - Green Places.
 */
package com.javinator9889.greenplaces.datamodels

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginModel(private val owner: ComponentActivity) {
    lateinit var user: FirebaseUser

    companion object Properties {
        @StyleRes
        var theme: Int = R.style.FirebaseUI

        @DrawableRes
        var logo: Int? = null
        var alwaysShowSignInMethod = false
        var anonymousUsersAutoUpgrade = false
        var emailLinking: String? = null
        var tosUrl: String? = null
        var privacyUrl: String? = null
        var onLoginIntent: Intent? = null

        private val providers: List<AuthUI.IdpConfig> = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.GitHubBuilder().build()
        )

        val intent: Intent
            get() = with(AuthUI.getInstance().createSignInIntentBuilder()) {
                setTheme(theme)
                setAlwaysShowSignInMethodScreen(alwaysShowSignInMethod)
                if (anonymousUsersAutoUpgrade)
                    enableAnonymousUsersAutoUpgrade()
                emailLinking?.let { setEmailLink(it) }
                tosUrl?.let { tos ->
                    privacyUrl?.let { privacy ->
                        setTosAndPrivacyPolicyUrls(tos, privacy)
                    }
                }
                logo?.let { setLogo(it) }
                setAvailableProviders(providers)
                build()
            }
    }

    val registerActivity
        get() =
            owner.registerForActivityResult(StartActivityForResult()) {
                val res = IdpResponse.fromResultIntent(it.data)
                if (it.resultCode == Activity.RESULT_OK) {
                    user = FirebaseAuth.getInstance().currentUser
                        ?: throw IllegalStateException("User was not authenticated!")
                    onLoginIntent?.let { intent -> owner.startActivity(intent); owner.finish() }
                        ?: throw IllegalStateException("\"onLoginIntent\" must be set")
                } else {
                    throw IllegalStateException(
                        res?.error?.localizedMessage ?: "Unknown login error"
                    )
                }
            }
}