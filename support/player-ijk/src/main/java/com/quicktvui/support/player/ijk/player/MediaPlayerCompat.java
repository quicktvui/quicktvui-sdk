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

import com.quicktvui.support.ijk.base.IMediaPlayer;
import com.quicktvui.support.ijk.base.IjkMediaPlayer;
import com.quicktvui.support.ijk.base.MediaPlayerProxy;
import com.quicktvui.support.ijk.base.TextureMediaPlayer;

public class MediaPlayerCompat {
    public static String getName(IMediaPlayer mp) {
        if (mp == null) {
            return "null";
        } else if (mp instanceof TextureMediaPlayer) {
            StringBuilder sb = new StringBuilder("TextureMediaPlayer <");
            IMediaPlayer internalMediaPlayer = ((TextureMediaPlayer) mp).getInternalMediaPlayer();
            if (internalMediaPlayer == null) {
                sb.append("null>");
            } else {
                sb.append(internalMediaPlayer.getClass().getSimpleName());
                sb.append(">");
            }
            return sb.toString();
        } else {
            return mp.getClass().getSimpleName();
        }
    }

    public static IjkMediaPlayer getIjkMediaPlayer(IMediaPlayer mp) {
        IjkMediaPlayer ijkMediaPlayer = null;
        if (mp == null) {
            return null;
        } if (mp instanceof IjkMediaPlayer) {
            ijkMediaPlayer = (IjkMediaPlayer) mp;
        } else if (mp instanceof MediaPlayerProxy && ((MediaPlayerProxy) mp).getInternalMediaPlayer() instanceof IjkMediaPlayer) {
            ijkMediaPlayer = (IjkMediaPlayer) ((MediaPlayerProxy) mp).getInternalMediaPlayer();
        }
        return ijkMediaPlayer;
    }

    public static void selectTrack(IMediaPlayer mp, int stream) {
        mp.selectTrack(stream);
    }

    public static void deselectTrack(IMediaPlayer mp, int stream) {
        mp.deselectTrack(stream);
    }

    public static int getSelectedTrack(IMediaPlayer mp, int trackType) {
        return mp.getSelectedTrack(trackType);
    }
}
