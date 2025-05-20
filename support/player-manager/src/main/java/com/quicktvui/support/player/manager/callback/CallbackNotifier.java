package com.quicktvui.support.player.manager.callback;

import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.player.PlayerError;
import com.quicktvui.support.player.manager.player.PlayerInfo;
import com.quicktvui.support.player.manager.player.PlayerStatus;
import com.quicktvui.support.player.manager.volume.IPlayerVolume;

import java.util.List;

import com.quicktvui.support.player.manager.aspect.AspectRatio;
import com.quicktvui.support.player.manager.decode.Decode;
import com.quicktvui.support.player.manager.player.IPlayerCallback;

public class CallbackNotifier {

    /**
     * 清晰度变化
     *
     * @param definitionList
     */
    public static void notifyDefinitionListChanged(List<IPlayerCallback> listenerList,
                                                   List<Definition> definitionList) {
        if (listenerList != null && definitionList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onAllDefinitionChanged(definitionList);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清晰度变化
     *
     * @param definition
     */
    public static void notifyDefinitionChanged(List<IPlayerCallback> listenerList,
                                               Definition definition) {
        if (listenerList != null && definition != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onDefinitionChanged(definition);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param listenerList
     * @param rateList
     */
    public static void notifyPlayRateListChanged(List<IPlayerCallback> listenerList,
                                                 List<Float> rateList) {
        if (listenerList != null && rateList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onAllPlayRateChanged(rateList);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 播放速率变化
     *
     * @param playRate
     */
    public static void notifyPlayRateChanged(List<IPlayerCallback> listenerList,
                                             float playRate) {
        if (listenerList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayRateChanged(playRate);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyPlayerErrorChanged(List<IPlayerCallback> listenerList,
                                                PlayerError playerError) {
        if (listenerList != null && playerError != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayerError(playerError);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    public static void notifyPlayerInfoChanged(List<IPlayerCallback> listenerList,
                                               PlayerInfo playerInfo) {
        if (listenerList != null && playerInfo != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayerInfo(playerInfo);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyPlayerStatusChanged(List<IPlayerCallback> listenerList,
                                                 PlayerStatus playerStatus) {
        if (listenerList != null && playerStatus != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayerStatusChanged(playerStatus);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    public static void notifyAspectRatioChanged(List<IPlayerCallback> listenerList,
                                                AspectRatio aspectRatio) {
        if (listenerList != null && aspectRatio != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onAspectRatioChanged(aspectRatio);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyAspectRatioListChanged(List<IPlayerCallback> listenerList,
                                                    List<AspectRatio> aspectRatioList) {
        if (listenerList != null && aspectRatioList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onAllAspectRatioChanged(aspectRatioList);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyDecodeListChanged(List<IPlayerCallback> listenerList,
                                               List<Decode> decodeList) {
        if (listenerList != null && decodeList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onAllDecodeChanged(decodeList);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyDecodeChanged(List<IPlayerCallback> listenerList,
                                           Decode decode) {
        if (listenerList != null && decode != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onDecodeChanged(decode);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyPlayerAllPlayRateChanged(List<IPlayerCallback> listenerList,
                                                      List<Float> rateList) {
        if (listenerList != null && rateList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onAllPlayRateChanged(rateList);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyPlayerPlayRateChanged(List<IPlayerCallback> listenerList,
                                                   float playRate) {
        if (listenerList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayRateChanged(playRate);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyPlayerVolumeChanged(List<IPlayerCallback> listenerList,
                                                 IPlayerVolume playerVolume) {
        if (listenerList != null && playerVolume != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayerVolumeChanged(playerVolume.getLeftVolume(), playerVolume.getRightVolume());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
