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
package com.javinator9889.greenplaces.views.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable
import com.github.appintro.SlideBackgroundColorHolder
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.databinding.IntroFragmentBinding
import com.javinator9889.greenplaces.datamodels.builders.LottieProperties
import kotlin.properties.Delegates

class LottieIntroFragment : Fragment(), SlideBackgroundColorHolder {
    companion object {
        private const val ARG_TITLE = "args:title"
        private const val ARG_DESCRIPTION = "args:description"
        private const val ARG_IMAGE_RES = "args:image-res"
        private const val ARG_BACKGROUND_COLOR = "args:background-color"
        private const val ARG_IMAGE_PROPERTIES = "args:image-res:properties"
        private const val ARG_TITLE_COLOR = "args:title:color"
        private const val ARG_DESCRIPTION_COLOR = "args:description:color"
        private const val ARG_TITLE_TYPEFACE = "args:title:typeface"
        private const val ARG_DESCRIPTION_TYPEFACE = "args:description:typeface"

        inline fun introBuilder(block: IntroBuilder.() -> Unit) =
            IntroBuilder().apply(block).build()
    }

    @LayoutRes
    private val layoutResId: Int = R.layout.intro_fragment
    private lateinit var title: String
    private lateinit var description: String
    private var imageRes by Delegates.notNull<@RawRes @DrawableRes Int>()
    private lateinit var binding: IntroFragmentBinding
    override var defaultBackgroundColor by Delegates.notNull<@ColorInt Int>()
    private var lottieProperties: LottieProperties? = null
    private var titleColor by Delegates.notNull<@ColorInt Int>()
    private var descriptionColor by Delegates.notNull<@ColorInt Int>()
    private var titleTypeface by Delegates.notNull<@FontRes Int>()
    private var descriptionTypeface by Delegates.notNull<@FontRes Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments ?: savedInstanceState
        args?.let {
            title = it.getString(ARG_TITLE, "")
            description = it.getString(ARG_DESCRIPTION, "")
            imageRes = it.getInt(ARG_IMAGE_RES, 0)
            defaultBackgroundColor = it.getInt(ARG_BACKGROUND_COLOR, 0x000000)
            lottieProperties = it.getParcelable(ARG_IMAGE_PROPERTIES)
            titleColor = it.getInt(ARG_TITLE_COLOR, 0xFFFFFF)
            descriptionColor = it.getInt(ARG_DESCRIPTION_COLOR, 0xFFFFFF)
            titleTypeface = it.getInt(ARG_TITLE_TYPEFACE, 0)
            descriptionTypeface = it.getInt(ARG_DESCRIPTION_TYPEFACE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = IntroFragmentBinding.bind(view)
        with(binding.title) {
            text = title
            setTextColor(titleColor)
            if (titleTypeface != 0) typeface = ResourcesCompat.getFont(view.context, titleTypeface)
        }
        with(binding.description) {
            text = description
            setTextColor(descriptionColor)
            if (descriptionTypeface != 0)
                typeface = ResourcesCompat.getFont(view.context, descriptionTypeface)
        }
        binding.image.setAnimation(imageRes)
        lottieProperties?.let {
            with(binding.image) {
                scale = it.scale
                progress = it.progress
                speed = it.speed
                setMinAndMaxFrame(it.startFrame, it.endFrame)
                if (it.loop)
                    repeatCount = LottieDrawable.INFINITE
                if (it.animate)
                    playAnimation()
            }
        }
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        binding.main.setBackgroundColor(backgroundColor)
    }

    fun setSelected() {
        if (::binding.isInitialized) binding.image.resumeAnimation()
    }

    fun setPaused() {
        if (::binding.isInitialized) binding.image.pauseAnimation()
    }

    data class IntroBuilder(
        var title: String? = null,
        var description: String? = null,
        @RawRes @DrawableRes var imageRes: Int? = null,
        @ColorInt var backgroundColor: Int = 0x000000,
        @ColorInt var titleColor: Int = 0xFFFFFF,
        @ColorInt var descriptionColor: Int = 0xFFFFFF,
        @FontRes var titleTypeface: Int = 0,
        @FontRes var descriptionTypeface: Int = 0,
        var lottieProperties: LottieProperties? = null
    ) {
        fun build(): Fragment {
            require(title != null)
            require(description != null)
            require(imageRes != null)

            val args = Bundle(3).apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESCRIPTION, description)
                putInt(ARG_IMAGE_RES, imageRes!!)
                putInt(ARG_BACKGROUND_COLOR, backgroundColor)
                putParcelable(ARG_IMAGE_PROPERTIES, lottieProperties)
                putInt(ARG_TITLE_COLOR, titleColor)
                putInt(ARG_DESCRIPTION_COLOR, descriptionColor)
                putInt(ARG_TITLE_TYPEFACE, titleTypeface)
                putInt(ARG_DESCRIPTION_TYPEFACE, descriptionTypeface)
            }
            return LottieIntroFragment().apply { arguments = args }
        }
    }
}