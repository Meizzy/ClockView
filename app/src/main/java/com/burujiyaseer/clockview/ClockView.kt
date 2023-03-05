package com.burujiyaseer.clockview

import android.content.Context
import android.graphics.*
import android.icu.util.Calendar
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat.getFont
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class ClockView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var mHeight = 0
    private var mWidth = 0
    private var mRadius = 0
    private var mCircleRadius = 0
    private var mAngle = 0.0
    private var mCentreX = 0
    private var mCentreY = 0
    private var mPadding = 0
    private var mIsInit = false
    private lateinit var mPaint: Paint
    private lateinit var mPath: Path
    private lateinit var mRect: Rect
    private var mNumbers: List<Int> = ArrayList()
    private var mMinimum = 0
    private var mHour = 0
    private var mMinute = 0
    private var mSecond = 0
    private var mHourHandSize = 0
    private var mHandSize = 0
    private var mFontSize = 0f


    private fun init() {
        mHeight = height
        mWidth = width
        mPadding = 50

        mCentreX = mWidth / 2
        mCentreY = mHeight / 2

        mMinimum = min(mHeight, mWidth)

        mCircleRadius = (mMinimum / 2) - 32
        mRadius = mMinimum / 2 - mPadding - 32 * 2

        mAngle = Math.PI / 30 - Math.PI / 2

        mPaint = Paint()
        mPath = Path()
        mRect = Rect()

        mHourHandSize = mRadius / 2
        mHandSize = mRadius - mRadius / 4
        mNumbers = (1..12).toList()

        mIsInit = true

        mFontSize = 64f
    }

    override fun onDraw(canvas: Canvas) {
        if (!mIsInit) {
            init()
        }
        canvas.also {
            drawCircle(it)
            drawHands(it)
            drawNumerals(it)
            drawLargePoints(it)
            drawLittlePoints(it)
//            drawShadow(it)
            postInvalidateDelayed(1000)

        }
        super.onDraw(canvas)
    }

    private fun setPaintAttributes(strokeWidth: Int) {
        mPaint.reset()
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = strokeWidth.toFloat()
        mPaint.isAntiAlias = true
    }

    private fun drawCircle(canvas: Canvas) {
        mPaint.reset()
        setPaintAttributes(32)

        canvas.drawCircle(mCentreX.toFloat(), mCentreY.toFloat(), mCircleRadius.toFloat(), mPaint)
    }

    private fun drawHands(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        mHour = calendar.get(Calendar.HOUR_OF_DAY)

        //convert to 12hour format from 24 hour format
        mHour = if (mHour > 12) mHour - 12 else mHour

        mMinute = calendar.get(Calendar.MINUTE)
        mSecond = calendar.get(Calendar.SECOND)

        drawHourHand(canvas, ((mHour + mMinute / 60.0) * 5f))
        drawMinuteHand(canvas, mMinute)
        drawSecondsHand(canvas, mSecond)
    }

    private fun drawHourHand(canvas: Canvas, location: Double) {

        setPaintAttributes(16)
        mAngle = Math.PI * location / 30 - Math.PI / 2
        canvas.drawLine(
            mCentreX.toFloat(),
            mCentreY.toFloat(),
            (mCentreX + cos(mAngle) * mHourHandSize).toFloat(),
            (mCentreY + sin(mAngle) * mHourHandSize).toFloat(),
            mPaint
        )
    }

    private fun drawMinuteHand(canvas: Canvas, location: Int) {

        setPaintAttributes(8)
        mAngle = Math.PI * location / 30 - Math.PI / 2
        canvas.drawLine(
            mCentreX.toFloat(),
            mCentreY.toFloat(),
            (mCentreX + cos(mAngle) * mHandSize).toFloat(),
            (mCentreY + sin(mAngle) * mHourHandSize).toFloat(),
            mPaint
        )
    }

    private fun drawSecondsHand(canvas: Canvas, location: Int) {
        setPaintAttributes(4)

        mAngle = Math.PI * location / 30 - Math.PI / 2
        canvas.drawLine(
            mCentreX.toFloat(),
            mCentreY.toFloat(),
            (mCentreX + cos(mAngle) * mHandSize).toFloat(),
            (mCentreY + sin(mAngle) * mHourHandSize).toFloat(),
            mPaint
        )
    }

    private fun drawNumerals(canvas: Canvas) {

        mPaint.textSize = mFontSize
        mPaint.typeface = getFont(context, R.font.custom_font)

        for (number in mNumbers) {
            val num = number.toString()
            mPaint.getTextBounds(num, 0, num.length, mRect)
            val angle = Math.PI / 6 * (number - 3)
            val x = (mCentreX + cos(angle) * mRadius - mRect.width() / 2)
            val y = (mCentreY + sin(angle) * mRadius + mRect.height() / 2)
            canvas.drawText(num, x.toFloat(), y.toFloat(), mPaint)

        }
    }

    private fun drawLittlePoints(canvas: Canvas) {
        for (number in 0..60) {
            setPaintAttributes(4)
            val angle = Math.PI / 30 * number
            val x = (mCentreX + cos(angle) * (mRadius + mPadding)) //- mRect.width() / 2).toInt()
            val y = (mCentreY + sin(angle) * (mRadius + mPadding)) //+ mRect.height() / 2).toInt()

            canvas.drawPoint(x.toFloat(), y.toFloat(), mPaint)
        }
    }

    private fun drawLargePoints(canvas: Canvas) {
        for (number in 0..30) {
            setPaintAttributes(10)
            val angle = Math.PI / 6 * number
            val x = (mCentreX + cos(angle) * (mRadius + mPadding)) //- mRect.width() / 2).toInt()
            val y = (mCentreY + sin(angle) * (mRadius + mPadding)) //+ mRect.height() / 2).toInt()

            canvas.drawPoint(x.toFloat(), y.toFloat(), mPaint)
        }
    }

}
