package com.quicktvui.support.player.manager.base;

import com.quicktvui.support.player.manager.player.IPlayer;

public interface IPlayerRootView {

    void quickUpdateVideoLayout(int w, int h);

    IPlayer getPlayer();
}
