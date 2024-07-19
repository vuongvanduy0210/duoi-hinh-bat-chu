package com.duyvv.dhbc.ui.play.adapter

import android.content.Context
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CustomLayoutManager(context: Context) : FlexboxLayoutManager(context) {

    override fun canScrollVertically(): Boolean {
        return false
    }
}