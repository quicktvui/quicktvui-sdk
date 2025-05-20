package com.quicktvui.support.device.info.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.device.info.model.beans.CpuBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class SocUtils {

    private static final String TAG = "SocUtils";
    public static String gpuVendor = "";
    public static String gpuRenderer = "";
    public static String gpuVersion = "SocUtils";

    /**
     * 读取 SOC 型号信息
     *
     * @return
     */
    public static String getSocInfo() {
        String socStr = "";
        socStr = CommandUtils.execute("getprop ro.board.platform");
        if (TextUtils.isEmpty(socStr)) {
            socStr = CommandUtils.execute("getprop ro.hardware");
            if (TextUtils.isEmpty(socStr)) {
                socStr = CommandUtils.execute("getprop ro.boot.hardware");
            }
        }
        return socStr;
    }

    /**
     * 读取制造商信息
     *
     * @return
     */
    public static String getManufacturerInfo() {
        String socStr = "";
        socStr = CommandUtils.execute("getprop ro.product.system.manufacturer");
        if (TextUtils.isEmpty(socStr) || (socStr != null && socStr.equals("unknown"))) {
            socStr = CommandUtils.execute("getprop ro.product.odm.manufacturer");
            if (TextUtils.isEmpty(socStr) || (socStr != null && socStr.equals("unknown"))) {
                socStr = CommandUtils.execute("getprop ro.product.product.manufacturer");
            }
        }
        return socStr;
    }

    public static void setCpuInfo(List<Pair<String, String>> list) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line;
            CpuBean bean = new CpuBean();
            HashSet<String> parts = new HashSet<>();
            HashSet<String> implementer = new HashSet<>();
            while ((line = bufferedReader.readLine()) != null) {
                String result = line.toLowerCase();
                LogUtils.d("CPU: " + result);
                String[] split = result.split(":\\s+", 2);
                if (split[0].startsWith("cpu part")) {
                    parts.add(split[1]);
                } else if (split[0].startsWith("hardware")) {
                    bean.setHardware(split[1]);
                } else if (split[0].startsWith("features")) {
                    bean.setFeatures(split[1]);
                } else if (split[0].startsWith("cpu implementer")) {
                    implementer.add(split[1]);
                }
            }
            bean.setParts(parts.toArray(new String[0]));
            bean.setImplementers(implementer.toArray(new String[0]));
            list.add(new Pair<>("Parts", parts.toString()));
            list.add(new Pair<>("Implementer", implementer.toString()));
            list.add(new Pair<>("Hardware", bean.getHardware()));
            list.add(new Pair<>("Features", bean.getFeatures()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCoreInfo() {
        String core = FileUtils.readFile("/sys/devices/system/cpu/present");
        if (TextUtils.isEmpty(core)) {
            core = CommandUtils.execute("cat /sys/devices/system/cpu/present");
        }
        if (TextUtils.isEmpty(core)) {
            core = Constants.UNKNOWN;
        }
        return core;
    }

    public static void getGPUInfo(Context context, List<Pair<String, String>> list) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        list.add(new Pair<>("GlEsVersion", info.getGlEsVersion()));
        list.add(new Pair<>("GlEsVersion", info.reqGlEsVersion + ""));
        list.add(new Pair<>("GlEsVersion", info.reqInputFeatures + ""));
        list.add(new Pair<>("GlEsVersion", info.reqKeyboardType + ""));
        list.add(new Pair<>("GlEsVersion", info.reqNavigation + ""));
        list.add(new Pair<>("GlEsVersion", info.reqTouchScreen + ""));
        list.add(new Pair<>("GlEsVersion", info.describeContents() + ""));
    }

    public static void getGPUInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        /*list.add(new Pair<>("GlEsVersion", info.getGlEsVersion()));
        list.add(new Pair<>("GlEsVersion", info.reqGlEsVersion + ""));
        list.add(new Pair<>("GlEsVersion", info.reqInputFeatures + ""));
        list.add(new Pair<>("GlEsVersion", info.reqKeyboardType + ""));
        list.add(new Pair<>("GlEsVersion", info.reqNavigation + ""));
        list.add(new Pair<>("GlEsVersion", info.reqTouchScreen + ""));
        list.add(new Pair<>("GlEsVersion", info.describeContents() + ""));*/
    }


    public static String getGPUInfoFromSystem() {
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.hardware.egl");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String gpuInfo = reader.readLine();
            reader.close();

            return gpuInfo.isEmpty() ? "无法获取 GPU 信息" : "GPU 信息: " + gpuInfo;
        } catch (Exception e) {
            return "获取 GPU 信息失败：" + e.getMessage();
        }
    }

    public static String getGPUInfoFromSystem2() {
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/gpuinfo");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            reader.close();

            return result.toString().isEmpty() ? "无法获取 GPU 信息" : result.toString();
        } catch (Exception e) {
            return "获取 GPU 信息失败：" + e.getMessage();
        }
    }

    public static void getGPUInfoWithEGL() {
        String gpuVendor = EGL14.eglQueryString(EGL14.eglGetCurrentDisplay(), EGL14.EGL_VENDOR);
        String gpuRenderer = EGL14.eglQueryString(EGL14.eglGetCurrentDisplay(), EGL14.EGL_RENDER_BUFFER);
        String gpuVersion = EGL14.eglQueryString(EGL14.eglGetCurrentDisplay(), EGL14.EGL_VERSION);

        Log.d("GPU Info", "厂商: " + gpuVendor);
        Log.d("GPU Info", "渲染器: " + gpuRenderer);
        Log.d("GPU Info", "EGL 版本: " + gpuVersion);
    }

    public static void getGPUInfoWithGLES() {
        String gpuVendor = GLES20.glGetString(GLES20.GL_VENDOR);
        String gpuRenderer = GLES20.glGetString(GLES20.GL_RENDERER);
        String glVersion = GLES20.glGetString(GLES20.GL_VERSION);

        Log.d("GPU Info", "GPU 厂商: " + gpuVendor);
        Log.d("GPU Info", "GPU 渲染器: " + gpuRenderer);
        Log.d("GPU Info", "OpenGL 版本: " + glVersion);
    }

    public static String getGPUInfoFromSysDevices() {
        String[] possiblePaths = {
                "/sys/devices/platform/gpu/gpu_model",
                "/sys/devices/platform/mali.0/gpuinfo",
                "/sys/devices/soc0/gpuinfo"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String gpuInfo = reader.readLine();
                    return gpuInfo != null ? "GPU 信息: " + gpuInfo : "未找到 GPU 信息";
                } catch (Exception e) {
                    return "读取 GPU 信息失败：" + e.getMessage();
                }
            }
        }

        return "未找到可用的 GPU 信息路径";
    }

    public static String getGPUInfoFromSys() {
        String path = "/sys/class/graphics/fb0/name";
        File file = new File(path);
        if (!file.exists()) {
            return "GPU 信息文件不存在";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String gpuInfo = reader.readLine();
            return gpuInfo != null && !gpuInfo.isEmpty() ? "GPU 信息: " + gpuInfo : "未找到 GPU 信息";
        } catch (Exception e) {
            return "读取 GPU 信息失败：" + e.getMessage();
        }
    }

    /*public static void getGPUInfoWithVulkan() {
        VulkanInstance instance = new VulkanInstance();
        VulkanPhysicalDevice[] devices = instance.enumeratePhysicalDevices();

        for (VulkanPhysicalDevice device : devices) {
            Log.d("GPU Info", "厂商ID: " + device.vendorID);
            Log.d("GPU Info", "设备名称: " + device.deviceName);
            Log.d("GPU Info", "API 版本: " + device.apiVersion);
        }
    }*/

    public static void getGpuInfoWithGLSurfaceView(EsMap map, EsPromise promise) {
        FrameLayout rootLayout = new FrameLayout(EsProxy.get().getTopActivity());
        GLSurfaceView glSurfaceView = new GLSurfaceView(EsProxy.get().getTopActivity());
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                gpuVendor = gl.glGetString(GL10.GL_VENDOR);         // GPU厂商
                gpuRenderer = gl.glGetString(GL10.GL_RENDERER);     // GPU渲染器
                gpuVersion = gl.glGetString(GL10.GL_VERSION);       // OpenGL 版本

                Log.d("GPU Info", "厂商: " + gpuVendor);
                Log.d("GPU Info", "渲染器: " + gpuRenderer);
                Log.d("GPU Info", "OpenGL 版本: " + gpuVersion);
                map.pushString("gpuVendor", !TextUtils.isEmpty(gpuVendor) ? gpuVendor : "");
                map.pushString("gpuRenderer", !TextUtils.isEmpty(gpuRenderer) ? gpuRenderer : "");
                map.pushString("gpuVersion", !TextUtils.isEmpty(gpuVersion) ? gpuVersion : "");
                try {
                    promise.resolve(map);
                } catch (Throwable e) {
                    promise.reject("程序异常");
                    e.printStackTrace();
                }
                try {
                    // 在 UI 线程中移除 GLSurfaceView
                    new Handler(Looper.getMainLooper()).post(() -> {
                        rootLayout.removeView(glSurfaceView);
                        Log.d("GLInfoHelper", "GLSurfaceView 已销毁");
                    });
                } catch (Throwable e) {
                    Log.e(TAG, "onSurfaceCreated:失败 ---------->" + e.getMessage());
                }
            }

            @Override
            public void onDrawFrame(GL10 gl) {
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
            }
        });
        rootLayout.addView(glSurfaceView);
        Objects.requireNonNull(EsProxy.get().getTopActivity()).addContentView(rootLayout, new FrameLayout.LayoutParams(1, 1));
    }

}
