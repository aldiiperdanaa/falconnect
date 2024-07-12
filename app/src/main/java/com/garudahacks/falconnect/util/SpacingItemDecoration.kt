package com.garudahacks.falconnect.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val spacing: Int,
    private val edgeSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position == 0) {
            outRect.left = edgeSpacing
        }

        if (position == itemCount - 1) {
            outRect.right = edgeSpacing
        } else {
            outRect.right = spacing
        }
    }
}
