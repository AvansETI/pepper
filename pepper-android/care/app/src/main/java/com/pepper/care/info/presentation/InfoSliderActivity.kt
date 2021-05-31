package com.pepper.care.info.presentation

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroPageTransformerType
import com.pepper.care.R

class InfoSliderActivity : AppIntro(), SliderCallback {

    override val layoutId = R.layout.activity_info_slider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSlides()
        initLayoutParams()
    }

    private fun initSlides() {
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.info_item_1))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.info_item_2))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.info_item_3))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.info_item_4))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.info_item_5))
    }

    private fun initLayoutParams() {
        isIndicatorEnabled = true
        isSystemBackButtonLocked = true

        setIndicatorColor(
            selectedIndicatorColor = ResourcesCompat.getColor(
                resources,
                R.color.colorAccent,
                theme
            ),
            unselectedIndicatorColor = ResourcesCompat.getColor(
                resources,
                R.color.transparentNotSelected,
                theme
            )
        )

        setTransformer(AppIntroPageTransformerType.Flow)
        setProgressIndicator()
        setImmersiveMode()
        setDoneText("Gereed")
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }

    override fun nextSlide() {
        this@InfoSliderActivity.goToNextSlide(false)
    }

    override fun cancelSlides() {
        finish()
    }
}