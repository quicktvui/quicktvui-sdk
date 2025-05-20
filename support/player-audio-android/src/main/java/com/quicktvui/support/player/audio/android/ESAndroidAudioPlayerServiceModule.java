package com.quicktvui.support.player.audio.android;


import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.player.audio.android.IAudioPlayerService;
import com.quicktvui.support.player.audio.android.IPlayerCallback;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.support.player.manager.base.PlayerBaseView;

/**
 *
 */
@ESKitAutoRegister
public class ESAndroidAudioPlayerServiceModule implements IEsModule, IEsInfo {

    private boolean serviceConnected;
    private IAudioPlayerService audioPlayerService;

    public static final String EVENT_PROP_VALUE = "value";

    public enum Events {
        EVENT_ON_PLAYER_SERVICE_BIND("onESAudioPlayerServiceBind"),
        EVENT_ON_PLAYER_INIT("onESAudioPlayerInit"),
        EVENT_ON_PLAYER_STATUS_CHANGED("onESAudioPlayerStatusChanged"),
        EVENT_ON_PLAYER_ERROR("onESAudioPlayerError"),
        EVENT_ON_PLAYER_INFO("onESAudioPlayerInfo"),
        EVENT_ON_PLAYER_RATE_CHANGED("onESAudioPlayRateChanged");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private Context context;

    @Override
    public void init(Context context) {
        this.context = context;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceConnected = false;
            if (L.DEBUG) {
                L.logD("--------onServiceDisconnected---->>>>");
            }
            EsMap esMap = new EsMap();
            esMap.pushBoolean(EVENT_PROP_VALUE, false);
            EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                    Events.EVENT_ON_PLAYER_SERVICE_BIND.toString(), esMap);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceConnected = true;
            audioPlayerService = IAudioPlayerService.Stub.asInterface(service);

            if (L.DEBUG) {
                L.logD("--------onServiceConnected---->>>>" + audioPlayerService);
            }

            try {
                audioPlayerService.registerPlayerCallback(playerCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //
            EsMap esMap = new EsMap();
            esMap.pushBoolean(EVENT_PROP_VALUE, true);
            EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                    Events.EVENT_ON_PLAYER_SERVICE_BIND.toString(), esMap);
        }
    };

    private IPlayerCallback playerCallback = new IPlayerCallback.Stub() {
        @Override
        public void onPlayerStatusChanged(int playerStatus) throws RemoteException {
            if (L.DEBUG) {
                L.logD("--------onPlayerStatusChanged---->>>>" + playerStatus);
            }
            EsMap esMap = new EsMap();
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, playerStatus);
            EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }

        @Override
        public void onPlayerError(int what, int extra) throws RemoteException {
            if (L.DEBUG) {
                L.logD("--------onPlayerError---->>>>" + "what:" + what + "--->>extra:" + extra);
            }
            EsMap eventMap = new EsMap();
            eventMap.pushInt(PlayerBaseView.EVENT_PROP_ERROR_CODE, what);
            eventMap.pushString(PlayerBaseView.EVENT_PROP_ERROR_MESSAGE, extra + "");
            EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                    ESAndroidAudioPlayerModule.Events.EVENT_ON_PLAYER_ERROR.toString(), eventMap);
        }

        @Override
        public void onPlayerInfo(int code, String message) throws RemoteException {
            if (L.DEBUG) {
                L.logD("--------onPlayerInfo---->>>>" + "code:" + code + "--->>message:" + message);
            }
            EsMap infoMap = new EsMap();
            infoMap.pushInt(PlayerBaseView.EVENT_PROP_INFO_CODE, code);
            infoMap.pushString(PlayerBaseView.EVENT_PROP_INFO_MESSAGE, message);
            EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                    Events.EVENT_ON_PLAYER_INFO.toString(), infoMap);
        }
    };

    public void bindService() {
        try {
            Intent intent = new Intent(context, ESAudioPlayerService.class);
            context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void unbindService() {
        try {
            if (serviceConnection != null) {
                context.unbindService(serviceConnection);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stopService() {
        try {
            Intent intent = new Intent(context, ESAudioPlayerService.class);
            context.stopService(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void initAudioPlayer(boolean looping) {
        if (L.DEBUG) {
            L.logD("--------initAudioPlayer--start-->>>>");
        }
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------initAudioPlayer---->>>>");
                }
                audioPlayerService.initAudioPlayer(looping);

                EsMap esMap = new EsMap();
                esMap.pushBoolean(EVENT_PROP_VALUE, true);
                EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                        Events.EVENT_ON_PLAYER_INIT.toString(), esMap);
            } else {
                if (L.DEBUG) {
                    L.logD("-----initAudioPlayer------->>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
                EsMap esMap = new EsMap();
                esMap.pushBoolean(EVENT_PROP_VALUE, false);
                EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                        Events.EVENT_ON_PLAYER_INIT.toString(), esMap);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            EsMap esMap = new EsMap();
            esMap.pushBoolean(EVENT_PROP_VALUE, false);
            EsProxy.get().sendNativeEventTraceable(ESAndroidAudioPlayerServiceModule.this,
                    Events.EVENT_ON_PLAYER_INIT.toString(), esMap);
        }
        if (L.DEBUG) {
            L.logD("--------initMediaPlayer----end----->>>>");
        }
    }

    public void play(String url) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------play---->>>>" + url);
                }
                audioPlayerService.play(url);
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------start---->>>>");
                }
                audioPlayerService.start();
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------pause---->>>>");
                }
                audioPlayerService.pause();
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------resume---->>>>");
                }
                audioPlayerService.resume();
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void seekTo(int msec) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------seekTo---->>>>" + msec);
                }
                audioPlayerService.seekTo(msec);
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------stop---->>>>");
                }
                audioPlayerService.stop();
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------reset---->>>>");
                }
                audioPlayerService.reset();
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------release---->>>>");
                }
                audioPlayerService.release();
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void isPlaying(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------isPlaying---->>>>"
                    );
                }
                esPromise.resolve(audioPlayerService.isPlaying());
            } else {
                esPromise.resolve(-1);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(-1);
        }
    }

    public void isPaused(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------isPaused---->>>>"
                    );
                }
                esPromise.resolve(audioPlayerService.isPaused());
            } else {
                esPromise.resolve(false);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(false);
        }
    }

    public void getDuration(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------getDuration---->>>>"
                    );
                }
                esPromise.resolve(audioPlayerService.getDuration());
            } else {
                esPromise.resolve(-1);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(-1);
        }
    }

    public void getCurrentPosition(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------getCurrentPosition---->>>>"
                    );
                }
                esPromise.resolve(audioPlayerService.getCurrentPosition());
            } else {
                esPromise.resolve(-1);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(-1);
        }
    }

    public void getBufferPercentage(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------getBufferPercentage---->>>>"
                    );
                }
                esPromise.resolve(audioPlayerService.getBufferPercentage());
            } else {
                esPromise.resolve(-1);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(-1);
        }
    }

    public void setPlayRate(float speed) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------setPlayRate---->>>>"
                            + "speed:" + speed + "---"
                    );
                }
                audioPlayerService.setPlayRate(speed);
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getCurrentPlayRate(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------getCurrentPlayRate---->>>>"
                    );
                }
                esPromise.resolve(audioPlayerService.getCurrentPlayRate());
            } else {
                esPromise.resolve(-1);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(-1);
        }
    }

    public void setVolume(float volume) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------setVolume---->>>>"
                            + "volume:" + volume + "---"
                    );
                }
                audioPlayerService.setVolume(volume);
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setLeftRightVolume(float leftVolume, float rightVolume) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------setLeftRightVolume---->>>>"
                            + "leftVolume:" + leftVolume + "---"
                            + "rightVolume:" + rightVolume + "---"
                    );
                }
                audioPlayerService.setLeftRightVolume(leftVolume, rightVolume);
            } else {
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getLeftVolume(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------getLeftVolume---->>>>");
                }
                esPromise.resolve(audioPlayerService.getLeftVolume());
            } else {
                esPromise.resolve(-1);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(-1);
        }
    }

    public void getRightVolume(EsPromise esPromise) {
        try {
            if (serviceConnected && audioPlayerService != null) {
                if (L.DEBUG) {
                    L.logD("--------getRightVolume---->>>>");
                }
                esPromise.resolve(audioPlayerService.getRightVolume());
            } else {
                esPromise.resolve(-1);
                if (L.DEBUG) {
                    L.logD("------------>>>>" +
                            "serviceConnected:" + serviceConnected + "--->>" +
                            "audioPlayerService:" + audioPlayerService + "--->>"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.resolve(-1);
        }
    }


    @Override
    public void destroy() {
        try {
            if (audioPlayerService != null) {
                audioPlayerService.unregisterPlayerCallback(playerCallback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            /*map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, eskit.sdk.support.player.manager.BuildConfig.ES_KIT_BUILD_TAG_COUNT);
            map.pushString(IEsInfo.ES_PROP_INFO_PACKAGE_NAME, BuildConfig.LIBRARY_PACKAGE_NAME);
            map.pushString(IEsInfo.ES_PROP_INFO_CHANNEL, BuildConfig.ES_KIT_BUILD_TAG_CHANNEL);
            map.pushString(IEsInfo.ES_PROP_INFO_BRANCH, BuildConfig.ES_KIT_BUILD_TAG);
            map.pushString(IEsInfo.ES_PROP_INFO_COMMIT_ID, BuildConfig.ES_KIT_BUILD_TAG_ID);
            map.pushString(IEsInfo.ES_PROP_INFO_RELEASE_TIME, BuildConfig.ES_KIT_BUILD_TAG_TIME);*/
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }
}
