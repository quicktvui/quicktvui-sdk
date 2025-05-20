package com.quicktvui.support.ui.item.view.shimmer

import android.view.View


interface FocusShimmer {

    fun setVisible(visible: Boolean)

    fun onFocus(options: Options?)

//    fun unBoundGlobalFocusListener()

    var parentView : View

    abstract class Options

    class Builder {
        fun asColor(): ColorFocusShimmer.Builder {
            return ColorFocusShimmer.Builder()
        }
    }

    companion object OptionsFactory {
        operator fun get(scaleX: Float, scaleY: Float): Options {
            return AbsFocusShimmer.Options[scaleX, scaleY]
        }

        // 指令下达
        operator fun get(roundRadius: Float): Options {
            return AbsFocusShimmer.Options[roundRadius]
        }
    }
}
