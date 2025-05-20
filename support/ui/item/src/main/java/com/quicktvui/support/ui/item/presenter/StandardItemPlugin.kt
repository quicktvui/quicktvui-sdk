package com.quicktvui.support.ui.item.presenter

import android.util.Log
import com.quicktvui.support.ui.item.host.ItemHostView
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder
import com.quicktvui.support.ui.item.widget.MultiLineTitleWidget
import com.quicktvui.support.ui.leanback.Presenter

open class StandardItemPlugin : SimpleItemPresenter.PluginImpl() {

    companion object {
        const val FLAG_WIDGET_NAME = "flagW"
        const val TAG = "StandardItemPlugin"
    }

    override fun onRegisterWidgetBuilder(widgetsHolder: LazyWidgetsHolder?) {
        super.onRegisterWidgetBuilder(widgetsHolder)
        widgetsHolder?.let{
            val b = MultiLineTitleWidget.Builder(widgetsHolder.context)
                    .setName(MultiLineTitle.NAME).setZOrder(SimpleItemPresenter.Z_ORDER_SHIMMER + 1)
            it.registerLazyWidget(MultiLineTitle.NAME,b)
        }

    }


    open fun getMultiLineWidget(viewHolder: Presenter.ViewHolder?) : MultiLineTitle?{
        val widget = viewHolder?.getFacet(MultiLineTitle::class.java)
        if(widget is MultiLineTitle)
            return widget
        return null
    }

    interface MultiLineTitle{

        fun callFocusChange(focus : Boolean)

        fun setText(title : String?)

        fun enableMultiLine(enable : Boolean)

        companion object {
            const val NAME = "MultiLineTitle"
        }
    }

    interface IModel : TitleItemPresenter.IModel


    override fun onHostViewFocusChanged(hostView: ItemHostView?, hasFocus: Boolean, widgetsHolder: LazyWidgetsHolder?) {
        super.onHostViewFocusChanged(hostView, hasFocus, widgetsHolder)
        val multiLine = widgetsHolder?.getFacet(MultiLineTitle::class.java)
        if(multiLine is MultiLineTitle){
            multiLine.callFocusChange(hasFocus)
        }
    }


    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
//        val multiLine = viewHolder?.getFacet(MultiLineTitle.Widget::class.java)
//        if(multiLine is MultiLineTitle){
//            multiLine.setVisible(false)
//        }
        super.onBindViewHolder(viewHolder, item)

        Log.d(TAG,"onBindViewHolder item:$item")
        if (item is TitleItemPresenter.IModel) {

            val multiLine: MultiLineTitle? = (viewHolder as LazyWidgetsHolder).getWidget<MultiLineTitleWidget>(
                MultiLineTitle.NAME
            )
            Log.d(TAG,"onBindViewHolder multiLine:$multiLine")
            multiLine?.let {
                viewHolder.setFacet(MultiLineTitle::class.java,it)
//                it.setVisible(true)
                it.setText(item.title)
            }
        }
    }


    override fun onExecuteTask(lsHolder: LazyWidgetsHolder, item: Any?, taskID: Int) {
        super.onExecuteTask(lsHolder, item, taskID)
        if(taskID == SimpleItemPresenter.TASK_MISC) {

        }
    }

}