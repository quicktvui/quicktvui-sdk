package com.quicktvui.rastermill;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FrameSequence {
    private final long mNativeFrameSequence;
    private final int mWidth;
    private final int mHeight;
    private final boolean mOpaque;
    private final int mFrameCount;
    private final int mDefaultLoopCount;

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public boolean isOpaque() {
        return this.mOpaque;
    }

    public int getFrameCount() {
        return this.mFrameCount;
    }

    public int getDefaultLoopCount() {
        return this.mDefaultLoopCount;
    }

    private static native FrameSequence nativeDecodeByteArray(byte[] var0, int var1, int var2);

    private static native FrameSequence nativeDecodeStream(InputStream var0, byte[] var1);

    private static native FrameSequence nativeDecodeByteBuffer(ByteBuffer var0, int var1, int var2);

    private static native void nativeDestroyFrameSequence(long var0);

    private static native long nativeCreateState(long var0);

    private static native void nativeDestroyState(long var0);

    private static native long nativeGetFrame(long var0, int var2, Bitmap var3, int var4);

    private FrameSequence(long nativeFrameSequence, int width, int height, boolean opaque, int frameCount, int defaultLoopCount) {
        this.mNativeFrameSequence = nativeFrameSequence;
        this.mWidth = width;
        this.mHeight = height;
        this.mOpaque = opaque;
        this.mFrameCount = frameCount;
        this.mDefaultLoopCount = defaultLoopCount;
    }

    public static FrameSequence decodeByteArray(byte[] data) {
        return decodeByteArray(data, 0, data.length);
    }

    public static FrameSequence decodeByteArray(byte[] data, int offset, int length) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else if (offset >= 0 && length >= 0 && offset + length <= data.length) {
            return nativeDecodeByteArray(data, offset, length);
        } else {
            throw new IllegalArgumentException("invalid offset/length parameters");
        }
    }

    public static FrameSequence decodeByteBuffer(ByteBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException();
        } else if (!buffer.isDirect()) {
            if (buffer.hasArray()) {
                byte[] byteArray = buffer.array();
                return decodeByteArray(byteArray, buffer.position(), buffer.remaining());
            } else {
                throw new IllegalArgumentException("Cannot have non-direct ByteBuffer with no byte array");
            }
        } else {
            return nativeDecodeByteBuffer(buffer, buffer.position(), buffer.remaining());
        }
    }

    public static FrameSequence decodeStream(InputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException();
        } else {
            byte[] tempStorage = new byte[16384];
            return nativeDecodeStream(stream, tempStorage);
        }
    }

    State createState() {
        if (this.mNativeFrameSequence == 0L) {
            throw new IllegalStateException("attempted to use incorrectly built FrameSequence");
        } else {
            long nativeState = nativeCreateState(this.mNativeFrameSequence);
            return nativeState == 0L ? null : new State(nativeState);
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mNativeFrameSequence != 0L) {
                nativeDestroyFrameSequence(this.mNativeFrameSequence);
            }
        } finally {
            super.finalize();
        }

    }

    static {
        System.loadLibrary("framesequence");
    }

    static class State {
        private long mNativeState;

        public State(long nativeState) {
            this.mNativeState = nativeState;
        }

        public void destroy() {
            if (this.mNativeState != 0L) {
                FrameSequence.nativeDestroyState(this.mNativeState);
                this.mNativeState = 0L;
            }

        }

        public long getFrame(int frameNr, Bitmap output, int previousFrameNr) {
            if (output != null && output.getConfig() == Config.ARGB_8888) {
                if (this.mNativeState == 0L) {
                    throw new IllegalStateException("attempted to draw destroyed FrameSequenceState");
                } else {
                    return FrameSequence.nativeGetFrame(this.mNativeState, frameNr, output, previousFrameNr);
                }
            } else {
                throw new IllegalArgumentException("Bitmap passed must be non-null and ARGB_8888");
            }
        }
    }
}
