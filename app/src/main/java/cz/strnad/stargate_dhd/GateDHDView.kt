package cz.strnad.stargate_dhd

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import androidx.core.view.ViewCompat

class GateDHDView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        const val dialChar = "!"
    }

    private var centerSize: Int = 0
    private var textSize: Float = 0f
    private var innerCircleRadius: Int = 0
    private var innerCircleWidth: Int = 0
    private var innerCircleHeight: Int = 0
    private var outerCircleRadius: Int = 0
    private var outerCircleWidth: Int = 0
    private var outerCircleHeight: Int = 0
    private val glyphsInner = "ABCDEFGHIJKLMNOPQRS"
    private val glyphsOuter = "TUVWXYZabcdefghijkl"

    init {
        context.obtainStyledAttributes(attrs, R.styleable.GateDHDView).use {
            centerSize =
                it.getDimensionPixelSize(R.styleable.GateDHDView_centerSize, 0)
            innerCircleRadius =
                it.getDimensionPixelOffset(R.styleable.GateDHDView_innerCircleRadius, 0)
            innerCircleWidth =
                it.getDimensionPixelSize(R.styleable.GateDHDView_innerCircleWidth, 0)
            innerCircleHeight =
                it.getDimensionPixelSize(R.styleable.GateDHDView_innerCircleHeight, 0)
            outerCircleRadius =
                it.getDimensionPixelOffset(R.styleable.GateDHDView_outerCircleRadius, 0)
            outerCircleWidth =
                it.getDimensionPixelSize(R.styleable.GateDHDView_outerCircleWidth, 0)
            outerCircleHeight =
                it.getDimensionPixelSize(R.styleable.GateDHDView_outerCircleHeight, 0)
            textSize = it.getDimension(R.styleable.GateDHDView_textSize, 0f)
        }

        val rotationStep = 360f / 19f
        val centerButton = createCenter()
        val round1Buttons = glyphsInner.mapIndexed { _, char -> createRound1(char) }
        val round2Buttons = glyphsOuter.mapIndexed { _, char -> createRound2(char) }

        with(ConstraintSet()) {
            clone(this@GateDHDView)

            centerHorizontally(centerButton.id, ConstraintSet.PARENT_ID)
            centerVertically(centerButton.id, ConstraintSet.PARENT_ID)

            round1Buttons.forEachIndexed { index, view ->
                constrainCircle(
                    view.id,
                    centerButton.id,
                    innerCircleRadius,
                    180f + index * rotationStep
                )
                setRotation(view.id, index * rotationStep)
            }

            round2Buttons.forEachIndexed { index, view ->
                constrainCircle(
                    view.id,
                    centerButton.id,
                    outerCircleRadius,
                    180f + index * rotationStep
                )
                setRotation(view.id, index * rotationStep)
            }

            applyTo(this@GateDHDView)
        }
    }

    private fun createCenter(): View = createButton(R.drawable.dhd_center).apply {
        text = dialChar
        layoutParams.apply {
            width = centerSize
            height = centerSize
        }
    }

    private fun createRound1(c: Char): View = createButton(R.drawable.dhd_round_1).apply {
        text = c.toString()
        layoutParams.apply {
            width = innerCircleWidth
            height = innerCircleHeight
        }
    }

    private fun createRound2(c: Char): View = createButton(R.drawable.dhd_round_2).apply {
        text = c.toString()
        layoutParams.apply {
            width = outerCircleWidth
            height = outerCircleHeight
        }
    }

    private fun createButton(
        @DrawableRes backgroundResId: Int
    ) = AppCompatButton(context).apply {
        id = ViewCompat.generateViewId()
        textSize = this@GateDHDView.textSize
        isAllCaps = false
        if (!isInEditMode) {
            typeface = ResourcesCompat.getFont(context, R.font.stargate)
        }
        setBackgroundResource(backgroundResId)
        addView(this)
    }

}
