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
import android.graphics.Color
import android.util.Log
import android.util.SparseArray
import androidx.core.content.edit
import androidx.core.util.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.datamodels.builders.lottieProperties
import com.javinator9889.greenplaces.views.fragments.LottieIntroFragment
import java.lang.ref.WeakReference

internal const val INTRO_DONE_KEY = "app:intro-done"

class IntroActivity : AppIntro2() {
    private val fragments = SparseArray<WeakReference<Fragment>>(5)

    init {
        fragments.append(0, WeakReference(LottieIntroFragment.introBuilder {
            title = "hola"
            description = "prueba"
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
        }))
        fragments.append(
            1, WeakReference(
                AppIntroFragment.newInstance(
                    title = "Green Places",
                    description = "A description at the bottom",
                    imageDrawable = R.mipmap.ic_launcher,
                    titleColor = Color.BLACK,
                    descriptionColor = Color.DKGRAY,
                    backgroundColor = Color.WHITE,
                    titleTypefaceFontRes = R.font.open_sans_light,
                    descriptionTypefaceFontRes = R.font.open_sans_light
                )
            )
        )
        fragments.append(
            2, WeakReference(
                AppIntroFragment.newInstance(
                    title = "Green Places 2",
                    description = "A new description at the bottom",
                    imageDrawable = R.mipmap.ic_launcher,
                    titleColor = Color.BLACK,
                    descriptionColor = Color.DKGRAY,
                    backgroundColor = Color.LTGRAY,
                    titleTypefaceFontRes = R.font.open_sans_light,
                    descriptionTypefaceFontRes = R.font.open_sans_light
                )
            )
        )
        lifecycleScope.launchWhenCreated {
            fragments.forEach { _, fragment -> fragment.get()?.let { addSlide(it) } }
            setTransformer(AppIntroPageTransformerType.Parallax())
            isColorTransitionsEnabled = true
            isIndicatorEnabled = true
            isWizardMode = true
            showStatusBar(false)
            setProgressIndicator()
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
            fragment.get()?.let {
                if (it is LottieIntroFragment) {
                    if (i == position) it.setSelected()
                    else it.setPaused()
                }
            }
        }
    }
}