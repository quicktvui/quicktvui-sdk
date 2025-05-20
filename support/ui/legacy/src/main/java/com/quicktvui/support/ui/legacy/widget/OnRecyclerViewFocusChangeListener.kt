package com.quicktvui.support.ui.legacy.widget

import android.view.View
import com.quicktvui.support.ui.v7.widget.RecyclerView

interface OnRecyclerViewFocusChangeListener {

    fun onRequestChildFocus(parent: RecyclerView, child: View, childPosition : Int, focused: View?)

    fun onRecyclerViewFocusChanged(recyclerView: RecyclerView, hasFocus : Boolean, focused: View?){

    }


}
