package cz.strnad.stargate_dhd

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat

class DHDView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val glyphs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkl"

    init {
        val centerButton = createCenter()
        val round1Buttons = glyphs.subSequence(0, glyphs.length / 2)
            .mapIndexed { i, char -> createRound1(char).apply { rotation = i * 19f } }
        val round2Buttons = glyphs.subSequence(glyphs.length / 2, glyphs.length)
            .mapIndexed { i, char -> createRound2(char).apply { rotation = i * 19f } }
        (listOf(centerButton) + round1Buttons + round2Buttons).forEach { addView(it) }

        with(ConstraintSet()) {
            clone(this@DHDView)

            centerHorizontally(centerButton.id, ConstraintSet.PARENT_ID)
            centerVertically(centerButton.id, ConstraintSet.PARENT_ID)

            val round1Radius = resources.getDimensionPixelOffset(R.dimen.dhd_round1_radius)
            round1Buttons.forEachIndexed { index, view ->
                constrainCircle(view.id, centerButton.id, round1Radius, 180f + index * 19f)
            }

            val round2Radius = resources.getDimensionPixelOffset(R.dimen.dhd_round2_radius)
            round2Buttons.forEachIndexed { index, view ->
                constrainCircle(view.id, centerButton.id, round2Radius, 180f + index * 19f)
            }

            applyTo(this@DHDView)
        }
    }

    private fun createCenter(): View = createButton(R.drawable.dhd_center)

    private fun createRound1(c: Char): View = createButton(R.drawable.dhd_round_1) {
        text = c.toString()
    }

    private fun createRound2(c: Char): View = createButton(R.drawable.dhd_round_2) {
        text = c.toString()
    }

    private fun createButton(
        @DrawableRes backgroundResId: Int,
        modify: AppCompatButton.() -> Unit = {}
    ) = AppCompatButton(context).apply {
        id = generateViewId()
        if (!isInEditMode) {
            typeface = ResourcesCompat.getFont(context, R.font.stargate)
        }
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 38f)
        setBackgroundResource(backgroundResId)
        modify()
    }

}
