package com.quicktvui.support.ui.item.presenter;

import android.content.Context;

import android.support.annotation.Nullable;

import com.quicktvui.support.ui.item.widget.TitleWidget;
import com.quicktvui.support.ui.item.widget.Widgets;
import com.quicktvui.support.ui.render.TextNode;

public class ItemPresenters {


    public static TitleItemPresenter twoLineTitleItem(Context context){

      return new TitleItemPresenter.Builder().setTitleWidgetBuilder(
                new TitleWidget.Builder(context).setTitlesGenerator(new TitleWidget.TitlesGenerator() {
                    @Override
                    public TextNode generateTitle(Context context) {
                        final TextNode tx =  Widgets.defaultTitle(context);
                        tx.setZOrder(SimpleItemPresenter.Z_ORDER_MISC);
                        return tx;
                    }

                    @Nullable
                    @Override
                    public TextNode generateSubTitle(Context context) {
                        final TextNode tx =  Widgets.defaultSubTitle(context);
                        tx.setZOrder(SimpleItemPresenter.Z_ORDER_MISC);
                        return tx;
                    }
                })
        ).build();
    }

    public static SimpleItemPresenter standardItem(Context context){
        return new SimpleItemPresenter.Builder().setPlugin(new StandardItemPlugin()).enableBorder(false).build();
    }






}
