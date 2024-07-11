package com.garudahacks.falconnect.view.main

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.garudahacks.falconnect.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationBar: BottomNavigationView {
	constructor(context: Context): super(context)
	constructor(context: Context, attrs: AttributeSet): super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

	private var activeBackground: Drawable = ContextCompat.getDrawable(context, R.drawable.bottom_nav_active) as Drawable
	private var inActiveBackground: Drawable = ContextCompat.getDrawable(context, R.drawable.bottom_nav_inactive) as Drawable

	init {
		setupItemBackground()
	}

	private fun setupItemBackground() {
		for (i in 0 until menu.size()) {
			val item = menu.getItem(i)
			val itemView = findViewById<BottomNavigationItemView>(item.itemId)
			itemView?.background = if (item.isChecked) activeBackground else inActiveBackground
		}
	}

	override fun setItemActiveIndicatorColor(csl: ColorStateList?) {
		super.setItemActiveIndicatorColor(csl)
		setupItemBackground()
	}
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		setupItemBackground()
	}
}