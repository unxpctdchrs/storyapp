package com.noir.storyapp.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.noir.storyapp.R

class CustomEmailET @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatEditText(context, attrs), View.OnTouchListener {
    private var emailIcon: Drawable? = null
    private val warningIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_warning) as Drawable

    init {
        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_mail_unfocused) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()){
                    emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_mail) as Drawable
                    if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                        setError("Format email salah", warningIcon)
                    } else {
                        error = null
                    }
                } else {
                    emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_mail_unfocused) as Drawable
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Email"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        compoundDrawablePadding = 50
        setPadding(50, 0, 0, 0)
        setHintTextColor(ContextCompat.getColor(context, R.color.purple))
        setTextColor(ContextCompat.getColor(context, R.color.orange))
        setBackgroundColor(ContextCompat.getColor(context, R.color.pastelOrange))

        setCornerRadius(30f)
        showDrawables()
    }

    private fun showDrawables() {
        setButtonDrawables(startOfTheText = emailIcon)
    }

    private fun setCornerRadius(cornerRadius: Float) {
        val roundRectShape = RoundRectShape(
            floatArrayOf(
                cornerRadius, cornerRadius, // Top-left corner radius
                cornerRadius, cornerRadius, // Top-right corner radius
                cornerRadius, cornerRadius, // Bottom-right corner radius
                cornerRadius, cornerRadius  // Bottom-left corner radius
            ),
            null,
            null
        )

        val shapeDrawable = ShapeDrawable(roundRectShape)
        shapeDrawable.paint.color = ContextCompat.getColor(context, R.color.pastelOrange)
        background = shapeDrawable
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText,
        )
    }
}