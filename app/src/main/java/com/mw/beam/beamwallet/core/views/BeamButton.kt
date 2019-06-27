/*
 * // Copyright 2018 Beam Development
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License");
 * // you may not use this file except in compliance with the License.
 * // You may obtain a copy of the License at
 * //
 * //    http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS,
 * // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * // See the License for the specific language governing permissions and
 * // limitations under the License.
 */

package com.mw.beam.beamwallet.core.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.mw.beam.beamwallet.R
import kotlinx.android.synthetic.main.common_button.view.*

/**
 * Created by vain onnellinen on 10/22/18.
 */
class BeamButton : LinearLayout {
    var iconResId: Int = Integer.MIN_VALUE
        set(value) {
            field = value
            if (field != Integer.MIN_VALUE) {
                icon.setImageResource(field)
            }
        }
    var textResId: Int = Integer.MIN_VALUE
        set(value) {
            field = value
            if (field != Integer.MIN_VALUE) {
                text.text = if (isLowerCase) context.getString(field).toLowerCase() else context.getString(field)
            }
        }
    var textColorResId: Int = Integer.MIN_VALUE
        set(value) {
            field = value
            if (field != Integer.MIN_VALUE) {
                text.setTextColor(ContextCompat.getColor(context, field))
            }
        }

    private var isLowerCase = false

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        inflate(context, R.layout.common_button, this)

        if (this.background == null) {
            this.background = ContextCompat.getDrawable(context, R.drawable.common_button)
        }
        this.orientation = HORIZONTAL
        this.gravity = Gravity.CENTER

        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.BeamButton,
                    0, 0
            )

            iconResId = a.getResourceId(R.styleable.BeamButton_button_icon, Integer.MIN_VALUE)
            textResId = a.getResourceId(R.styleable.BeamButton_button_text, Integer.MIN_VALUE)
            textColorResId = a.getResourceId(R.styleable.BeamButton_button_text_color, Integer.MIN_VALUE)

            if (!a.getBoolean(R.styleable.BeamButton_text_padding_enabled, true)) {
                text.setPadding(text.paddingLeft, 0, text.paddingRight,0)
            }

            isLowerCase = a.getBoolean(R.styleable.BeamButton_lower_case, false)
            if (isLowerCase) {
                text.text = text.text.toString().toLowerCase()
            }
        }
    }
}
