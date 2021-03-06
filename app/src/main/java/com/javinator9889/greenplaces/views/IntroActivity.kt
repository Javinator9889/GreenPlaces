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

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.util.SparseArray
import androidx.core.content.edit
import androidx.core.util.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroPageTransformerType
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.datamodels.builders.lottieProperties
import com.javinator9889.greenplaces.views.fragments.LottieIntroFragment

internal const val INTRO_DONE_KEY = "app:intro-done"

class IntroActivity : AppIntro2() {
    private val fragments = SparseArray<Fragment>(5)

    init {
        lifecycleScope.launchWhenCreated {
            fragments.append(0, LottieIntroFragment.introBuilder {
                title = getString(R.string.talk)
                description = getString(R.string.talk_explanation)
                imageRes = R.raw.talk
                lottieProperties = lottieProperties {
                    animate = true
                    loop = true
                }
                backgroundColor = Color.WHITE
                titleColor = Color.BLACK
                descriptionColor = Color.DKGRAY
                titleTypeface = R.font.product_sans
                descriptionTypeface = R.font.product_sans
            })
            fragments.append(1, LottieIntroFragment.introBuilder {
                title = getString(R.string.walk)
                description = getString(R.string.walk_explanation)
                imageRes = R.raw.walk
                lottieProperties = lottieProperties {
                    animate = true
                    loop = true
                }
                backgroundColor = Color.WHITE
                titleColor = Color.BLACK
                descriptionColor = Color.DKGRAY
                titleTypeface = R.font.product_sans
                descriptionTypeface = R.font.product_sans
            })
            fragments.append(2, LottieIntroFragment.introBuilder {
                title = getString(R.string.location_access)
                description = getString(R.string.location_explanation)
                imageRes = R.raw.location_pin
                lottieProperties = lottieProperties {
                    animate = true
                    loop = true
                }
                backgroundColor = Color.WHITE
                titleColor = Color.BLACK
                descriptionColor = Color.DKGRAY
                titleTypeface = R.font.product_sans
                descriptionTypeface = R.font.product_sans
            })
            fragments.forEach { _, fragment -> addSlide(fragment) }
            setTransformer(AppIntroPageTransformerType.Parallax())
            isColorTransitionsEnabled = true
            isIndicatorEnabled = true
            isWizardMode = true
            showStatusBar(false)
            setProgressIndicator()
            askForPermissions(
                permissions = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                slideNumber = 3,
                required = true
            )
        }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        with(PreferenceManager.getDefaultSharedPreferences(this)) {
            edit { putBoolean(INTRO_DONE_KEY, true) }
        }
        startActivity(Intent(this, FirebaseUIActivity::class.java))
        finish()
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        fragments.forEach { i, fragment ->
            if (fragment is LottieIntroFragment) {
                if (i == position) fragment.setSelected()
                else fragment.setPaused()
            }
        }
    }
}