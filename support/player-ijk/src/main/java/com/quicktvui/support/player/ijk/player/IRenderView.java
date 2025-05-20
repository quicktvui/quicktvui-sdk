/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quicktvui.support.player.ijk.player;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import com.quicktvui.support.ijk.base.IMediaPlayer;

public interface IRenderView {


//    fitParent:可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
//    fillParent:可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
//    wrapContent:将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
//    fitXY:不剪裁,非等比例拉伸画面填满整个View
//    16:9:不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
//    4:3:不剪裁,非等比例拉伸画面到4:3,并完全显示在View中


    //比例缩放
    int AR_ASPECT_FIT_PARENT = 0; // without clip全屏
    //充满父窗
    int AR_ASPECT_FILL_PARENT = 1; // may clip等比全屏
    //匹配内容
    int AR_ASPECT_WRAP_CONTENT = 2;//1:1
    //
    int AR_MATCH_PARENT = 3;
    //16:9比例缩放
    int AR_16_9_FIT_PARENT = 4;//16:9
    //4:3比例缩放
    int AR_4_3_FIT_PARENT = 5;//4:3

    View getView();

    boolean shouldWaitForResize();

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    void setVideoRotation(int degree);

    void setAspectRatio(int aspectRatio);

    void addRenderCallback( IRenderCallback callback);

    void removeRenderCallback( IRenderCallback callback);

    interface ISurfaceHolder {
        void bindToMediaPlayer(IMediaPlayer mp);

        
        IRenderView getRenderView();

        SurfaceHolder getSurfaceHolder();

        Surface openSurface();

        SurfaceTexture getSurfaceTexture();
    }

    interface IRenderCallback {
        /**
         * @param holder
         * @param width  could be 0
         * @param height could be 0
         */
        void onSurfaceCreated( ISurfaceHolder holder, int width, int height);

        /**
         * @param holder
         * @param format could be 0
         * @param width
         * @param height
         */
        void onSurfaceChanged( ISurfaceHolder holder, int format, int width, int height);

        void onSurfaceDestroyed( ISurfaceHolder holder);
    }
}
