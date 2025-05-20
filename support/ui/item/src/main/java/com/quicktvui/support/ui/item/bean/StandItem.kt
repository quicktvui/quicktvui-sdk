package com.quicktvui.support.ui.item.bean

import com.quicktvui.support.ui.item.presenter.SimpleItemPresenter

interface StandItem : SimpleItemPresenter.IModel{

        /**
         * 获得普通标题，目前在底部
         */
        fun obtainNormalTitle() : String?

        /**
         * 获得浮动在窗口上的文本
         */
        fun obtainFloatText() : String?

        /**
         * 获得角标背景颜色
         */
        fun obtainFlagBgColor() : Long {
            return 0xFF6C3EFF
        }

        /**
         * 获得角标显示文本
         */
        fun obtainFlagText() : String?{
            return ""
        }

        /**
         * 获得是否显示右上角角标
         */
        fun obtainFlagVisible() : Boolean{
            return obtainFlagText()?.isNotEmpty() ?: false
        }

        /**
         * 获得是否显示浮动文本
         */
        fun obtainFloatTextVisible() : Boolean{
            return obtainFloatText()?.isNotEmpty() ?: false
        }



}