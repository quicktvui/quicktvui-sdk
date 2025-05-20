package com.quicktvui.support.ui.item.widget;

import android.graphics.Canvas;


public interface IActorTagWidget extends IWidget {

    String NAME = "ACTORTAG";

    void setActorTag(String actorTag);

    void setActorTagSize(int unit,float size);

    void setVisibility(int visible);

    void setVisible(boolean isShow);

    void draw(Canvas canvas);
}
