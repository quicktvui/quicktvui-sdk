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

import android.content.Context;
import android.view.View;

import java.lang.ref.WeakReference;

import com.quicktvui.support.player.ijk.R;
import com.quicktvui.support.player.manager.log.PLog;


public final class MeasureHelper {

    private static String TAG = "VIDEO_VIEW_MEASURE";

    private WeakReference<View> mWeakView;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mVideoRotationDegree;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private int mCurrentAspectRatio = IRenderView.AR_ASPECT_FIT_PARENT;

    public MeasureHelper(View view) {
        mWeakView = new WeakReference<View>(view);
    }

    public View getView() {
        if (mWeakView == null)
            return null;
        return mWeakView.get();
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    /**
     * Must be called by View.onMeasure(int, int)
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.e(TAG, "===========================start======================>>>>>" + mCurrentAspectRatio);
        }

        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#-----1---doMeasure--->>>>>" +
                    "widthMeasureSpec:" + View.MeasureSpec.toString(widthMeasureSpec) + "---->>>" +
                    "heightMeasureSpec:" + View.MeasureSpec.toString(heightMeasureSpec));
        }

        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            int tempSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempSpec;
        }

        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#---2-----doMeasure--->>>>>" +
                    "mVideoWidth:" + mVideoWidth + "---->>>" +
                    "mVideoHeight:" + mVideoHeight);
        }

        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#-----3---doMeasure--->>>>>" +
                    "width:" + width + "---->>>" +
                    "height:" + height);
        }

        if (mCurrentAspectRatio == IRenderView.AR_MATCH_PARENT) {
            width = widthMeasureSpec;
            height = heightMeasureSpec;

            if (PLog.isLoggable(PLog.DEBUG)) {
                PLog.d(TAG, "#----4----doMeasure---AR_MATCH_PARENT--->>>>>" +
                        "width:" + View.MeasureSpec.toString(widthMeasureSpec) + "---->>>" +
                        "height:" + View.MeasureSpec.toString(heightMeasureSpec));
            }

        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

            if (PLog.isLoggable(PLog.DEBUG)) {
                PLog.d(TAG, "#----5----doMeasure------>>>>>" +
                        "widthSpecMode:" + widthSpecMode + "---->>>" +
                        "widthSpecSize:" + widthSpecSize + "---->>>" +
                        "heightSpecMode:" + heightSpecMode + "---->>>" +
                        "heightSpecSize:" + heightSpecSize);
            }

            if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----6----doMeasure--AT_MOST---start---->>>>>");
                }
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----7----doMeasure--specAspectRatio--->>>>>" + specAspectRatio);
                }
                float displayAspectRatio;
                switch (mCurrentAspectRatio) {
                    case IRenderView.AR_16_9_FIT_PARENT:
                        displayAspectRatio = 16.0f / 9.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        }
                        if (PLog.isLoggable(PLog.DEBUG)) {
                            PLog.d(TAG, "#--------doMeasure---AR_16_9_FIT_PARENT---displayAspectRatio--->>>>>" + displayAspectRatio);
                        }
                        break;
                    case IRenderView.AR_4_3_FIT_PARENT:
                        displayAspectRatio = 4.0f / 3.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        }
                        if (PLog.isLoggable(PLog.DEBUG)) {
                            PLog.d(TAG, "#--------doMeasure---AR_4_3_FIT_PARENT---displayAspectRatio--->>>>>" + displayAspectRatio);
                        }
                        break;
                    case IRenderView.AR_ASPECT_FIT_PARENT:
                    case IRenderView.AR_ASPECT_FILL_PARENT:
                    case IRenderView.AR_ASPECT_WRAP_CONTENT:
                    default:
                        displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
                        // zhousuqiang 修复中信盒子 720x576分辨率视频成方形的问题
                        if (displayAspectRatio > 1.2f && displayAspectRatio < 1.34f) {
                            displayAspectRatio = 4.0f / 3.0f;
                        }
                        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
                            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
                        }
                        if (PLog.isLoggable(PLog.DEBUG)) {
                            PLog.d(TAG, "#--------doMeasure---" +
                                    "FIT_PARENT-" +
                                    "FILL_PARENT-" +
                                    "WRAP_CONTENT-" +
                                    "--displayAspectRatio--->>>>>" + displayAspectRatio);
                        }
                        break;
                }
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#-----8---doMeasure---shouldBeWider--->>>>>" + shouldBeWider);
                }

                switch (mCurrentAspectRatio) {
                    case IRenderView.AR_ASPECT_FIT_PARENT:
                    case IRenderView.AR_16_9_FIT_PARENT:
                    case IRenderView.AR_4_3_FIT_PARENT:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }
                        if (PLog.isLoggable(PLog.DEBUG)) {
                            PLog.d(TAG, "#--------doMeasure---FIT_PARENT--->>>>>"
                                    + "width:" + width + "--->>>"
                                    + "height:" + height + "--->>>"
                            );
                        }
                        break;
                    case IRenderView.AR_ASPECT_FILL_PARENT:
                        if (shouldBeWider) {
                            // not high enough, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        } else {
                            // not wide enough, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        }
                        if (PLog.isLoggable(PLog.DEBUG)) {
                            PLog.d(TAG, "#--------doMeasure---FILL_PARENT--->>>>>"
                                    + "width:" + width + "--->>>"
                                    + "height:" + height + "--->>>"
                            );
                        }
                        break;
                    case IRenderView.AR_ASPECT_WRAP_CONTENT:
                    default:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        if (PLog.isLoggable(PLog.DEBUG)) {
                            PLog.d(TAG, "#--------doMeasure---WRAP_CONTENT--default--->>>>>"
                                    + "width:" + width + "--->>>"
                                    + "height:" + height + "--->>>"
                            );
                        }
                        break;
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----9----doMeasure--AT_MOST---end---->>>>>");
                }
            }
            //
            else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----10----doMeasure--widthSpecMode&heightSpecMode---EXACTLY---start---->>>>>");
                }
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                    if (PLog.isLoggable(PLog.DEBUG)) {
                        PLog.d(TAG, "#----11----doMeasure-----EXACTLY---->>>>>width:" + width);
                    }
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                    if (PLog.isLoggable(PLog.DEBUG)) {
                        PLog.d(TAG, "#----12----doMeasure-----EXACTLY---->>>>>height:" + height);
                    }
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----13----doMeasure-----EXACTLY---->>>>>" +
                            "width:" + width + "---->>>" +
                            "height:" + height + "---->>>"
                    );
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----14----doMeasure--widthSpecMode&heightSpecMode---EXACTLY---end---->>>>>");
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#---15-----doMeasure--widthSpecMode---EXACTLY---start---->>>>>");
                }

                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----16----doMeasure-----EXACTLY---->>>>>" +
                            "width:" + width + "---->>>" +
                            "height:" + height + "---->>>"
                    );
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----17----doMeasure--widthSpecMode---EXACTLY---end---->>>>>");
                }

            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#-----18---doMeasure--heightSpecMode---EXACTLY---start---->>>>>");
                }
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----19----doMeasure----heightSpecMode-EXACTLY---->>>>>" +
                            "width:" + width + "---->>>" +
                            "height:" + height + "---->>>"
                    );
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----20----doMeasure--heightSpecMode---EXACTLY---end---->>>>>");
                }
            } else {
                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----21----doMeasure--else----start--->>>>>");
                }
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----22----doMeasure----else---->>>>>" +
                            "width:" + width + "---->>>" +
                            "height:" + height + "---->>>"
                    );
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#----23----doMeasure--else----end--->>>>>");
                }
            }
        } else {
            if (PLog.isLoggable(PLog.DEBUG)) {
                PLog.d(TAG, "#----24----doMeasure-------no size yet, just adopt the given spec sizes--------->>>>>");
            }
            // no size yet, just adopt the given spec sizes
        }

        mMeasuredWidth = width;
        mMeasuredHeight = height;

        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#-----25---doMeasure----END---->>>>>" +
                    "mMeasuredWidth:" + mMeasuredWidth + "---->>>" +
                    "mMeasuredHeight:" + mMeasuredHeight + "---->>>"
            );
        }

        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.e(TAG, "===========================end======================>>>>>");
        }
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    public void setAspectRatio(int aspectRatio) {
        mCurrentAspectRatio = aspectRatio;
    }

    public static String getAspectRatioText(Context context, int aspectRatio) {
        String text;
        switch (aspectRatio) {
            case IRenderView.AR_ASPECT_FIT_PARENT:
                text = context.getString(R.string.VideoView_ar_aspect_fit_parent);
                break;
            case IRenderView.AR_ASPECT_FILL_PARENT:
                text = context.getString(R.string.VideoView_ar_aspect_fill_parent);
                break;
            case IRenderView.AR_ASPECT_WRAP_CONTENT:
                text = context.getString(R.string.VideoView_ar_aspect_wrap_content);
                break;
            case IRenderView.AR_MATCH_PARENT:
                text = context.getString(R.string.VideoView_ar_match_parent);
                break;
            case IRenderView.AR_16_9_FIT_PARENT:
                text = context.getString(R.string.VideoView_ar_16_9_fit_parent);
                break;
            case IRenderView.AR_4_3_FIT_PARENT:
                text = context.getString(R.string.VideoView_ar_4_3_fit_parent);
                break;
            default:
                text = context.getString(R.string.N_A);
                break;
        }
        return text;
    }
}
