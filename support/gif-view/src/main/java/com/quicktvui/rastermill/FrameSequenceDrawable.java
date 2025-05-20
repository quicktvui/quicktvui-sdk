package com.quicktvui.rastermill;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

public class FrameSequenceDrawable extends Drawable implements Animatable, Runnable {
    private static final long MIN_DELAY_MS = 20L;
    private static final long DEFAULT_DELAY_MS = 100L;
    private static final Object sLock = new Object();
    private static HandlerThread sDecodingThread;
    private static Handler sDecodingThreadHandler;
    private static BitmapProvider sAllocatingBitmapProvider = new BitmapProvider() {
        public Bitmap acquireBitmap(int minWidth, int minHeight) {
            return Bitmap.createBitmap(minWidth, minHeight, Config.ARGB_8888);
        }

        public void releaseBitmap(Bitmap bitmap) {
        }
    };
    public static final int LOOP_ONCE = 1;
    public static final int LOOP_INF = 2;
    public static final int LOOP_DEFAULT = 3;
    private final FrameSequence mFrameSequence;
    private final FrameSequence.State mFrameSequenceState;
    private final Paint mPaint;
    private BitmapShader mFrontBitmapShader;
    private BitmapShader mBackBitmapShader;
    private final Rect mSrcRect;
    private boolean mCircleMaskEnabled;
    private final Object mLock;
    private final BitmapProvider mBitmapProvider;
    private boolean mDestroyed;
    private Bitmap mFrontBitmap;
    private Bitmap mBackBitmap;
    private static final int STATE_SCHEDULED = 1;
    private static final int STATE_DECODING = 2;
    private static final int STATE_WAITING_TO_SWAP = 3;
    private static final int STATE_READY_TO_SWAP = 4;
    private int mState;
    private int mCurrentLoop;
    private int mLoopBehavior;
    private long mLastSwap;
    private long mNextSwap;
    private int mNextFrameToDecode;
    private OnFinishedListener mOnFinishedListener;
    private Runnable mDecodeRunnable;
    private Runnable mCallbackRunnable;

    private static void initializeDecodingThread() {
        synchronized(sLock) {
            if (sDecodingThread == null) {
                sDecodingThread = new HandlerThread("FrameSequence decoding thread", 10);
                sDecodingThread.start();
                sDecodingThreadHandler = new Handler(sDecodingThread.getLooper());
            }
        }
    }

    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.mOnFinishedListener = onFinishedListener;
    }

    public void setLoopBehavior(int loopBehavior) {
        this.mLoopBehavior = loopBehavior;
    }

    private static Bitmap acquireAndValidateBitmap(BitmapProvider bitmapProvider, int minWidth, int minHeight) {
        Bitmap bitmap = bitmapProvider.acquireBitmap(minWidth, minHeight);
        if (bitmap.getWidth() >= minWidth && bitmap.getHeight() >= minHeight && bitmap.getConfig() == Config.ARGB_8888) {
            return bitmap;
        } else {
            throw new IllegalArgumentException("Invalid bitmap provided");
        }
    }

    public FrameSequenceDrawable(FrameSequence frameSequence) {
        this(frameSequence, sAllocatingBitmapProvider);
    }

    public FrameSequenceDrawable(FrameSequence frameSequence, BitmapProvider bitmapProvider) {
        this.mLock = new Object();
        this.mDestroyed = false;
        this.mLoopBehavior = 3;
        this.mDecodeRunnable = new Runnable() {
            public void run() {
                int nextFrame;
                Bitmap bitmap;
                synchronized(FrameSequenceDrawable.this.mLock) {
                    if (FrameSequenceDrawable.this.mDestroyed) {
                        return;
                    }

                    nextFrame = FrameSequenceDrawable.this.mNextFrameToDecode;
                    if (nextFrame < 0) {
                        return;
                    }

                    bitmap = FrameSequenceDrawable.this.mBackBitmap;
                    FrameSequenceDrawable.this.mState = 2;
                }

                int lastFrame = nextFrame - 2;
                long invalidateTimeMs = FrameSequenceDrawable.this.mFrameSequenceState.getFrame(nextFrame, bitmap, lastFrame);
                if (invalidateTimeMs < 20L) {
                    invalidateTimeMs = 100L;
                }

                boolean schedule = false;
                Bitmap bitmapToRelease = null;
                synchronized(FrameSequenceDrawable.this.mLock) {
                    if (FrameSequenceDrawable.this.mDestroyed) {
                        bitmapToRelease = FrameSequenceDrawable.this.mBackBitmap;
                        FrameSequenceDrawable.this.mBackBitmap = null;
                    } else if (FrameSequenceDrawable.this.mNextFrameToDecode >= 0 && FrameSequenceDrawable.this.mState == 2) {
                        schedule = true;
                        FrameSequenceDrawable.this.mNextSwap = invalidateTimeMs + FrameSequenceDrawable.this.mLastSwap;
                        FrameSequenceDrawable.this.mState = 3;
                    }
                }

                if (schedule) {
                    FrameSequenceDrawable.this.scheduleSelf(FrameSequenceDrawable.this, FrameSequenceDrawable.this.mNextSwap);
                }

                if (bitmapToRelease != null) {
                    FrameSequenceDrawable.this.mBitmapProvider.releaseBitmap(bitmapToRelease);
                }

            }
        };
        this.mCallbackRunnable = new Runnable() {
            public void run() {
                if (FrameSequenceDrawable.this.mOnFinishedListener != null) {
                    FrameSequenceDrawable.this.mOnFinishedListener.onFinished(FrameSequenceDrawable.this);
                }

            }
        };
        if (frameSequence != null && bitmapProvider != null) {
            this.mFrameSequence = frameSequence;
            this.mFrameSequenceState = frameSequence.createState();
            int width = frameSequence.getWidth();
            int height = frameSequence.getHeight();
            this.mBitmapProvider = bitmapProvider;
            this.mFrontBitmap = acquireAndValidateBitmap(bitmapProvider, width, height);
            this.mBackBitmap = acquireAndValidateBitmap(bitmapProvider, width, height);
            this.mSrcRect = new Rect(0, 0, width, height);
            this.mPaint = new Paint();
            this.mPaint.setFilterBitmap(true);
            this.mFrontBitmapShader = new BitmapShader(this.mFrontBitmap, TileMode.CLAMP, TileMode.CLAMP);
            this.mBackBitmapShader = new BitmapShader(this.mBackBitmap, TileMode.CLAMP, TileMode.CLAMP);
            this.mLastSwap = 0L;
            this.mNextFrameToDecode = -1;
            this.mFrameSequenceState.getFrame(0, this.mFrontBitmap, -1);
            initializeDecodingThread();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public final void setCircleMaskEnabled(boolean circleMaskEnabled) {
        this.mCircleMaskEnabled = circleMaskEnabled;
        this.mPaint.setAntiAlias(circleMaskEnabled);
    }

    private void checkDestroyedLocked() {
        if (this.mDestroyed) {
            throw new IllegalStateException("Cannot perform operation on recycled drawable");
        }
    }

    public boolean isDestroyed() {
        synchronized(this.mLock) {
            return this.mDestroyed;
        }
    }

    public void destroy() {
        if (this.mBitmapProvider == null) {
            throw new IllegalStateException("BitmapProvider must be non-null");
        } else {
            Bitmap bitmapToReleaseB = null;
            Bitmap bitmapToReleaseA;
            synchronized(this.mLock) {
                this.checkDestroyedLocked();
                bitmapToReleaseA = this.mFrontBitmap;
                this.mFrontBitmap = null;
                if (this.mState != 2) {
                    bitmapToReleaseB = this.mBackBitmap;
                    this.mBackBitmap = null;
                }

                this.mDestroyed = true;
            }

            this.mBitmapProvider.releaseBitmap(bitmapToReleaseA);
            if (bitmapToReleaseB != null) {
                this.mBitmapProvider.releaseBitmap(bitmapToReleaseB);
            }

        }
    }

    protected void finalize() throws Throwable {
        try {
            this.mFrameSequenceState.destroy();
        } finally {
            super.finalize();
        }

    }

    public void draw(Canvas canvas) {
        synchronized(this.mLock) {
            this.checkDestroyedLocked();
            if (this.mState == 3 && this.mNextSwap - SystemClock.uptimeMillis() <= 0L) {
                this.mState = 4;
            }

            if (this.isRunning() && this.mState == 4) {
                Bitmap tmp = this.mBackBitmap;
                this.mBackBitmap = this.mFrontBitmap;
                this.mFrontBitmap = tmp;
                BitmapShader tmpShader = this.mBackBitmapShader;
                this.mBackBitmapShader = this.mFrontBitmapShader;
                this.mFrontBitmapShader = tmpShader;
                this.mLastSwap = SystemClock.uptimeMillis();
                boolean continueLooping = true;
                if (this.mNextFrameToDecode == this.mFrameSequence.getFrameCount() - 1) {
                    ++this.mCurrentLoop;
                    if (this.mLoopBehavior == 1 && this.mCurrentLoop == 1 || this.mLoopBehavior == 3 && this.mCurrentLoop == this.mFrameSequence.getDefaultLoopCount()) {
                        continueLooping = false;
                    }
                }

                if (continueLooping) {
                    this.scheduleDecodeLocked();
                } else {
                    this.scheduleSelf(this.mCallbackRunnable, 0L);
                }
            }
        }

        if (this.mCircleMaskEnabled) {
            Rect bounds = this.getBounds();
            this.mPaint.setShader(this.mFrontBitmapShader);
            float width = (float)bounds.width();
            float height = (float)bounds.height();
            float circleRadius = Math.min(width, height) / 2.0F;
            canvas.drawCircle(width / 2.0F, height / 2.0F, circleRadius, this.mPaint);
        } else {
            this.mPaint.setShader((Shader)null);
            canvas.drawBitmap(this.mFrontBitmap, this.mSrcRect, this.getBounds(), this.mPaint);
        }

    }

    private void scheduleDecodeLocked() {
        this.mState = 1;
        this.mNextFrameToDecode = (this.mNextFrameToDecode + 1) % this.mFrameSequence.getFrameCount();
        sDecodingThreadHandler.post(this.mDecodeRunnable);
    }

    public void run() {
        boolean invalidate = false;
        synchronized(this.mLock) {
            if (this.mNextFrameToDecode >= 0 && this.mState == 3) {
                this.mState = 4;
                invalidate = true;
            }
        }

        if (invalidate) {
            this.invalidateSelf();
        }

    }

    public void start() {
        if (!this.isRunning()) {
            synchronized(this.mLock) {
                this.checkDestroyedLocked();
                if (this.mState == 1) {
                    return;
                }

                this.mCurrentLoop = 0;
                this.scheduleDecodeLocked();
            }
        }

    }

    public void stop() {
        if (this.isRunning()) {
            this.unscheduleSelf(this);
        }

    }

    public boolean isRunning() {
        synchronized(this.mLock) {
            return this.mNextFrameToDecode > -1 && !this.mDestroyed;
        }
    }

    public void unscheduleSelf(Runnable what) {
        synchronized(this.mLock) {
            this.mNextFrameToDecode = -1;
            this.mState = 0;
        }

        super.unscheduleSelf(what);
    }

    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (!visible) {
            this.stop();
        } else if (restart || changed) {
            this.stop();
            this.start();
        }

        return changed;
    }

    public void setFilterBitmap(boolean filter) {
        this.mPaint.setFilterBitmap(filter);
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        return this.mFrameSequence.getWidth();
    }

    public int getIntrinsicHeight() {
        return this.mFrameSequence.getHeight();
    }

    public int getOpacity() {
        return this.mFrameSequence.isOpaque() ? -1 : -2;
    }

    public FrameSequence getFramSequence() {
        return this.mFrameSequence;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public interface BitmapProvider {
        Bitmap acquireBitmap(int var1, int var2);

        void releaseBitmap(Bitmap var1);
    }

    public interface OnFinishedListener {
        void onFinished(FrameSequenceDrawable var1);
    }
}
