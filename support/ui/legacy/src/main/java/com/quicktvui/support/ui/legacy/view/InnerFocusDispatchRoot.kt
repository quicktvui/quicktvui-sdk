package com.quicktvui.support.ui.legacy.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.FocusFinder
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import com.quicktvui.support.ui.legacy.FConfig
import com.quicktvui.support.ui.legacy.widget.TVFrameLayout
import java.util.ArrayList


open class InnerFocusDispatchRoot : TVFrameLayout {


    companion object {

        val TAG = "FocusSystem&"

    }

    private var mFocused : View? = null

    private var mSpecialFocusSearchRequest : View? = null


    private val mTempFocusList  = ArrayList<View>()



    constructor(context: Context) : super(
            context
    )

    constructor(context: Context,attrs: AttributeSet) : super(
            context,
            attrs
    )

    constructor(context: Context,attrs: AttributeSet, defStyleAttr : Int) : super(
            context,
            attrs,
            defStyleAttr
    )


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return super.dispatchKeyEvent(event)
    }


    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    )

    override fun requestChildFocus(child: View?, focused: View?) {
        if(FConfig.DEBUG){
            Log.d(TAG, "FocusChange : From: $mFocused To: $focused")
        }
        mFocused = focused
        super.requestChildFocus(child, focused)
    }


    /**TODO
     *1，线性布局内部处理焦点寻找逻辑
     *2，nextXXid 指定为自己时的处理
     *3，添加可以设置焦点在被动选择时，默认选择第几个child的接口
     */



    override fun addFocusables(views: ArrayList<View>?, direction: Int) {
        if(FConfig.DEBUG) {
            Log.d(TAG, "+addFocusables views : $mTempFocusList")
        }
        views?.addAll(mTempFocusList)

    }

    private fun markSpecifiedFocusSearch(specialTarget : View){
//        Log.d(TAG, "+mark SpecifiedFocusSearch  target : "+specialTarget)
        mSpecialFocusSearchRequest = specialTarget
    }

    private fun consumeSpecifiedFocusSearchRequest(){
        if(mSpecialFocusSearchRequest != null){
//            Log.d(TAG, "-consume SpecifiedFocusSearchRequest")
            mSpecialFocusSearchRequest = null
        }
    }

    private fun isSpecifiedFocusSearch() : Boolean{
        return mSpecialFocusSearchRequest != null
    }

    private fun excuteFindNextFocus(group : ViewGroup, focused:View? , direction: Int) : View?{
        //注意： 此方法会调用this.addFocusables()
        //由于 focused可能不是group的子孙view,可能导致崩溃，所以这里try catch
        var result : View?
        try {
            result =  FocusFinder.getInstance().findNextFocus(group,focused,direction)
        }catch (t : Throwable){
            result = null
        }
        return result
    }

    open fun blockFocus(){
        Log.e(TAG,"blockFocus called")
        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
    }

    open fun unBlockFocus(){
        Log.e(TAG,"releaseBlockFocus called")
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
    }

    fun blockFocusForTime(duration : Long){
        blockFocus()
        postDelayed({
            unBlockFocus()
        },duration)
    }



    private fun findFocusFromGroup(group: ViewGroup?,direction: Int,focused: View?,clearSearchList : Boolean) : View?{
        if(group == null)
            return null
        if(clearSearchList){
            mTempFocusList.clear()
        }
        group.addFocusables(mTempFocusList,direction)

        return excuteFindNextFocus(group,focused,direction)
    }


    override fun focusSearch(focused: View?, direction: Int): View? {
        if(FConfig.DEBUG) {
            Log.d(TAG, "-----------------begin : focusSearch with focused : $focused direction : $direction -------------")
        }
        //return super.focusSearch(focused, direction)

        //寻找结果
        var found : View? = null
        //用户自定义的目标view
        var userSpecifiedTarget: View? = null
        //用户自定义的目标viewID
        var userSpecifiedTargetId = -1
        //当前焦点的parent
        var focusedParent : ViewGroup? = null

        try {

            //1, find user specified
            focused?.let {
                //发现用户指定targetView，则直接调用此view的addFocusables方法
                userSpecifiedTargetId = findUserSpecifiedNextFocusViewIdTraverse(it, direction)
                userSpecifiedTarget = findViewById(userSpecifiedTargetId)
                if(FConfig.DEBUG) {
                    Log.d(TAG, "1 : find specifiedTargetViewID is $userSpecifiedTargetId specifiedView is $userSpecifiedTarget")
                }
                if(it.parent is ViewGroup){
                    focusedParent = it.parent as ViewGroup
                }
            }

            //2 , find focus
            if (userSpecifiedTarget != null) {
                userSpecifiedTarget!!.let {
                    markSpecifiedFocusSearch(it)
                    // it.addFocusables(views,direction,focusableMode)

                    //1,优先在当前parent中寻找，以保证优先搜索比较近的view
                    if(found == null) {
                        if(focusedParent != userSpecifiedTarget && focusedParent != userSpecifiedTarget!!.parent) { //确保俩个parent不是同一个

                            focusedParent?.addFocusables(mTempFocusList, direction)
                                //zhaopeng 20190327 由于这里focused不是focusedParent的子view时，会发生崩溃，所以这里try catch
                            found = excuteFindNextFocus(focusedParent!!, focused, direction)

                            if(FConfig.DEBUG) {
                                Log.d(TAG, "2-1: find from focusedParent found :  $found")
                            }
                        }
                    }
                    //2， 从IFocusGroup中寻找

                    if(it is ITVFocusGroup){
                        val specifiedFocused = it.getNextSpecifiedFocus(focused,direction)

                        if(specifiedFocused != null ){
//                            val parent = focused!!.parent
                            //如果设置了nextFocusXX id为自身时，不处理此逻辑。
//                            if(!(parent is ViewGroup && parent.id == userSpecifiedTargetId)) {
                                found = specifiedFocused
                                if(FConfig.DEBUG) {
                                    Log.d(TAG, "2-2: find from  ITVFocusGroup next is :  " + found)
                                }
//                            }

                        }else{
                            if(FConfig.DEBUG) {
                                Log.d(TAG, "2-2: find from  ITVFocusGroup specifiedFocused == null  ")
                            }
                        }
                    }
                    //3， userSpecifiedTarget中寻找
                    if(found == null) {
                        mTempFocusList.clear()
                        it.addFocusables(mTempFocusList, direction)
                        found = excuteFindNextFocus(this, focused, direction)
                        if(FConfig.DEBUG) {
                            Log.d(TAG, "2-3 :  find from  userSpecifiedTarget  $found")
                        }
                    }



                    //4 这里处理用户是否拦截了焦点
                    if( (found == null)  ){
                        focused?.let {
//                            if(parent is ViewGroup && parent.id == userSpecifiedTargetId){
                            if(sameDescend(it,userSpecifiedTargetId)){
                                // 这种情况下，用户将nextXXID 设置成本身，所以将focused返回
                                if(FConfig.DEBUG) {
                                    Log.d(TAG, "2-4 : find from : sameDescend return focused")
                                }
                               found =  it
                            }
                        }

                    }

                    //5 这里只有一种情况，用户设置的焦点不太符合物理逻辑，所以不再考虑focused位置的情况下再次搜索
                    if(found == null){
                            mTempFocusList.clear()
                            userSpecifiedTarget!!.addFocusables(mTempFocusList, direction)
                            found = excuteFindNextFocus(this,null,direction)
                            if(FConfig.DEBUG){
                                Log.d(TAG, "2-5 : find without focused found is "+found)
                            }
                    }


                }


            } else {
                if(FConfig.DEBUG) {
                    Log.d(TAG, "2 : ** userSpecifiedTarget is NULL find from Root")
                }
                //2 为空时用户没有设定，从root中寻找
                super.addFocusables(mTempFocusList, direction)

                //3 find nextFocus from root
                // 注意： 此方法会调用this.addFocusables()
                found = excuteFindNextFocus(this, focused, direction)
                if(FConfig.DEBUG) {
                    Log.d(TAG, "3 :  FocusFinder search from Root result is  $found")
                }
            }




        }finally {
            mTempFocusList.clear()
        }


        consumeSpecifiedFocusSearchRequest()
        if(FConfig.DEBUG) {
            Log.d(TAG, "-----------------end : focusSearch searched : $found-----------------")
        }

        return found
    }



    override fun focusSearch(direction: Int): View {
        if(FConfig.DEBUG) {
            Log.d(TAG, "focusSearch  without focused " + direction + "direction")
        }
        return super.focusSearch(direction)
    }


    /***
     * 寻找当前view指定的下一焦点id
     * @param
     */
    private fun findSpecifiedNextFocusId(sourceView: View?,direction: Int) : Int{
        var specifiedTargetViewID = -1
        sourceView?.let {
            when(direction){
                View.FOCUS_UP->{
                    specifiedTargetViewID = sourceView.nextFocusUpId
                }

                View.FOCUS_DOWN->{
                    specifiedTargetViewID = sourceView.nextFocusDownId
                }

                View.FOCUS_LEFT->{
                    specifiedTargetViewID = sourceView.nextFocusLeftId
                }

                View.FOCUS_RIGHT->{
                    specifiedTargetViewID = sourceView.nextFocusRightId
                }
            }
        }
        return specifiedTargetViewID
    }

    private fun sameDescend(focused : View?,userSpecifiedId : Int) : Boolean{
        var target : View? = focused
        while (target != this && target is View){

            if(userSpecifiedId ==  target.id){
                return true
            }

            target = if(target.parent is View){
                target.parent as View
            }else{
                null
            }
        }
        return false
    }


    private fun findUserSpecifiedNextFocusViewIdTraverse(focused : View, direction: Int) : Int{

        var specifiedTargetViewID = -1
        /**
         * 寻找当前view以及其父view指定的下一焦点id
         *
         */
        var target : View? = focused
        while (target != this && target is View){

            specifiedTargetViewID = findSpecifiedNextFocusId(target,direction)

//            if(DEBUG){
//                Log.d(TAG, " findSpecifiedNextFocusId target is "+target+" find specifiedTargetViewID is "+specifiedTargetViewID)
//            }

            if(specifiedTargetViewID > 0){
                return specifiedTargetViewID
            }
            target = if(target.parent is View){
                target.parent as View
            }else{
                null
            }

        }

        return specifiedTargetViewID
    }

}