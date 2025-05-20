package com.quicktvui.support.player.ijk.utils;
import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/*
 * Description : 设备工具类，用于获取设备信息以及获取activity的宽高等
 * Attention: 对于getActivitySize方法，建议适用于activity中的onResume()
 */
public class DeviceUtil {
    /**
     * 获取设备的gpu信息
     */
    public static String getGPUInfo() {
        StringBuilder gpuName;
        try{
            EGL10 egl = (EGL10) EGLContext.getEGL();
            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            int[] version = new int[2];
            if (!egl.eglInitialize(display, version)) {
                return "";
            }
            int[] configAttrs = {
                    EGL10.EGL_RENDERABLE_TYPE, 0x0004,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_SURFACE_TYPE, 0x0004,
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[10];
            int[] numConfigs = new int[1];
            if (!egl.eglChooseConfig(display, configAttrs, configs, configs.length, numConfigs)) {
                return "";
            }
            EGLConfig config = configs[0];
            int[] contextAttrs = { 0x3098, 2, EGL10.EGL_NONE };
            EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, contextAttrs);
            if (egl.eglGetError() != EGL10.EGL_SUCCESS) {
                return "";
            }
            int[] surfaceAttrs = { EGL10.EGL_NONE };
            EGLSurface eglSurface = egl.eglCreatePbufferSurface(display, config, surfaceAttrs);
            if (egl.eglGetError() != EGL10.EGL_SUCCESS) {
                return "";
            }
            if (!egl.eglMakeCurrent(display, eglSurface, eglSurface, context)) {
                return "";
            }

            GL10 gl = (GL10) context.getGL();
            gpuName = new StringBuilder();
            gpuName = gpuName.append(gl.glGetString(GL10.GL_RENDERER));
            gpuName.append(",");
            gpuName.append(gl.glGetString(GL10.GL_VENDOR));
            gpuName.append(",");
            gpuName.append(gl.glGetString(GL10.GL_VERSION));

            egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            egl.eglDestroySurface(display, eglSurface);
            egl.eglDestroyContext(display, context);
            egl.eglTerminate(display);
            egl = null;
            display = null;
        } catch(Throwable ex) {
            ex.printStackTrace();
            gpuName = new StringBuilder();
        }

        return gpuName.toString();
    }

    /**
     * 获取当前activity的宽高，在onResume方法里调用
     * @return
     */
    public static Rect getActivityRect(Activity activity) {
        Rect rect = new Rect();
        if (activity != null) {
            View decorView = activity.getWindow().getDecorView();
            rect.top = 0;
            rect.bottom = decorView.getMeasuredHeight();
            rect.left = 0;
            rect.right = decorView.getMeasuredWidth();
        }
        return rect;
    }
}
