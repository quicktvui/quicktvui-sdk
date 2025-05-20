package com.quicktvui.support.player.ijk.component;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.support.player.ijk.player.IjkVideoPlayer;
import com.quicktvui.support.player.manager.base.PlayerBaseView;
import com.quicktvui.support.player.manager.component.PlayerBaseComponent;
import com.quicktvui.support.player.manager.player.IPlayer;
import com.quicktvui.support.player.manager.player.PlayerStatus;
import com.quicktvui.support.player.manager.player.PlayerStatusEnum;
import com.quicktvui.support.player.manager.player.PlayerType;

/**
 * <br>
 *
 * <br>
 */
@ESKitAutoRegister
public class ADPlayerComponent extends PlayerBaseComponent {

    @Override
    protected IPlayer initPlayer(Context context, PlayerBaseView playerRootView) {
        return new IjkVideoPlayer();
    }

    @Override
    public void dispatchFunction(PlayerBaseView view, String functionName, EsArray params, EsPromise promise) {
        if (IEsInfo.ES_OP_GET_ES_INFO.equals(functionName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        } else if (OP_INIT.equals(functionName) || OP_PLAY.equals(functionName) || OP_AD_CAN_EXIT_TIME.equals(functionName)
                || OP_CLICK_PLAYER_VIEW.equals(functionName) || OP_SET_POINT_AD_PROGRESS.equals(functionName)
                || OP_SET_RELEASE_POINT_AD.equals(functionName)) {
            PlayerStatus playerStatusPreparing = new PlayerStatus(PlayerType.AD);
            playerStatusPreparing.status = PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR;
            view.onPlayerStatusChanged(playerStatusPreparing);
        } else
            super.dispatchFunction(view, functionName, params, promise);
    }

}
