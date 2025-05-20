package com.quicktvui.support.device.info.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EmulatorUtils {

    /**
     * 获取 su 版本
     *
     * @return
     */
    public static String getSuVersion() {
        return CommandUtils.execute("su -v");
    }

    /**
     * 读取声卡型号
     *
     * @return
     */
    public static String getSound() {
        String sound = CommandUtils.execute("cat /proc/asound/card0/id");
        if (TextUtils.isEmpty(sound)) {
            sound = FileUtils.readFile("cat /proc/asound/card0/id");
        }
        return sound;
    }

    /**
     * 桌面应用
     *
     * @param context
     * @return
     */
    public static String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res == null) {
            return "$unknown";
        }
        if (res.activityInfo == null) {
            return "$unknown";
        }
        return res.activityInfo.packageName;
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static void setLauncherInfo(Context context, List<Pair<String, String>> list, String launcher) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(launcher, 0);
            list.add(new Pair<>("LauncherName", applicationInfo.loadLabel(packageManager).toString()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list.add(new Pair<>("LauncherMinSdkVersion", applicationInfo.minSdkVersion + ""));
            }
            list.add(new Pair<>("LauncherTargetSdkVersion", applicationInfo.targetSdkVersion + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(launcher, 0);
            list.add(new Pair<>("LauncherVersionName", packageInfo.versionName));
            list.add(new Pair<>("LauncherVersionCode", packageInfo.versionCode + ""));
            list.add(new Pair<>("LauncherFirstInstallTime", TimeUtils.formatDate(packageInfo.firstInstallTime)));
            list.add(new Pair<>("LauncherLastUpdateTime", TimeUtils.formatDate(packageInfo.lastUpdateTime)));
            Signature signature = packageManager.getPackageInfo(launcher, PackageManager.GET_SIGNATURES).signatures[0];
            list.add(new Pair<>("LauncherSign", HashUtils.md5Encode(signature.toByteArray())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject brandJson = null;

    /**
     * @return 品牌信息列表
     */
    public static String getBrandInfo() {

        if (brandJson != null && brandJson.length() > 0) {
            return brandJson.toString();
        }

        if (FileUtils.exists("/system/app")) {
            File file = new File("/system/app");
            String[] list = file.list();
            if (list != null && list.length > 0) {
                JSONArray hw = new JSONArray();
                JSONArray oppo = new JSONArray();
                JSONArray vivo = new JSONArray();
                JSONArray xm = new JSONArray();
                JSONArray op = new JSONArray();
                JSONArray smartisan = new JSONArray();
                JSONArray samsung = new JSONArray();
                JSONArray lenovo = new JSONArray();
                JSONArray zte = new JSONArray();
                JSONArray mz = new JSONArray();

                // 读取值
                for (String s : list) {
                    s = s.toLowerCase();
                    if (((s.startsWith("hw") || s.contains("huawei")) && !s.equals("hw"))) {
                        hw.put(s);
                    } else if (s.contains("miui") || s.contains("xiaomi")) {
                        xm.put(s);
                    } else if (s.contains("oppo")) {
                        oppo.put(s);
                    } else if (s.contains("vivo")) {
                        vivo.put(s);
                    } else if (s.contains("samsung")) {
                        samsung.put(s);
                    } else if (s.startsWith("op")) {
                        op.put(s);
                    } else if (s.contains("smartisan")) {
                        smartisan.put(s);
                    } else if (s.contains("lenovo")) {
                        lenovo.put(s);
                    } else if (s.startsWith("zte")) {
                        zte.put(s);
                    } else if (s.startsWith("mz")) {
                        mz.put(s);
                    }
                }

                if (FileUtils.exists("/system/emui")) {
                    hw.put("/system/emui");
                }

                JSONObject jsonObject = new JSONObject();
                if (hw.length() > 0) {
                    try {
                        jsonObject.put("huawei", hw);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (xm.length() > 0) {
                    try {
                        jsonObject.put("xiaomi", xm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (oppo.length() > 0) {
                    try {
                        jsonObject.put("oppo", oppo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (vivo.length() > 0) {
                    try {
                        jsonObject.put("vivo", vivo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (samsung.length() > 0) {
                    try {
                        jsonObject.put("samsung", samsung);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (op.length() > 0) {
                    try {
                        jsonObject.put("oneplus", op);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (smartisan.length() > 0) {
                    try {
                        jsonObject.put("smartisan", smartisan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (lenovo.length() > 0) {
                    try {
                        jsonObject.put("lenovo", lenovo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (zte.length() > 0) {
                    try {
                        jsonObject.put("zte", zte);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mz.length() > 0) {
                    try {
                        jsonObject.put("meizu", mz);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                brandJson = jsonObject;
                return brandJson.toString();
            }
        }
        return null;
    }

    public static String getBatteryInfo(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryStatus != null) {
            StringBuffer sb = new StringBuffer();
            // 电量
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            sb.append("Level: ");
            sb.append(level);
            // unknown=1, charging=2, discharging=3, not charging=4, full=5
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            sb.append("    Status: ");
            sb.append(status);
            return sb.toString();
        }
        return null;
    }

    /**
     * qemu 检测
     *
     * @return
     */
    public static boolean qemuCheck() {
        if (checkQEmuDriverFile("/proc/tty/drivers") || checkQEmuDriverFile("/proc/cpuinfo")) {
            return true;
        }
        return "1".equals(CommandUtils.getProperty("ro.kernel.qemu"));
    }

    /**
     * qemu特有的驱动列表
     */
    private static final String[] KNOWN_QEMU_DRIVERS = {
            "goldfish"
    };

    /**
     * 驱动程序的列表
     *
     * @return true为模拟器
     */
    private static boolean checkQEmuDriverFile(String name) {
        File driver = new File(name);
        if (driver.exists() && driver.canRead()) {
            byte[] data = new byte[1024];
            try {
                InputStream inStream = new FileInputStream(driver);
                inStream.read(data);
                inStream.close();
            } catch (Exception e) {
            }
            String driverData = new String(data);
            for (String known_qemu_driver : KNOWN_QEMU_DRIVERS) {
                if (driverData.contains(known_qemu_driver)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设备通道文件，只兼容了qemu模拟器
     */
    private static final String[] KNOWN_PIPES = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };

    /**
     * 检测“/dev/socket/qemud”，“/dev/qemu_pipe”这两个通道设备文件特征
     *
     * @return true为模拟器
     */
    public static boolean checkPipes() {
        for (String pipes : KNOWN_PIPES) {
            File qemu = new File(pipes);
            if (qemu.exists()) {
                return true;
            }
        }
        return false;
    }

    public static String getModelName() {
        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            String line;
            fileReader = new FileReader("/proc/cpuinfo");
            reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("model name")) {
                    return line.substring(line.indexOf(":") + 1).trim();
                }
            }
        } catch (Exception e) {
            Log.e("error", "getModelName: "+e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("error", "getModelName:IOException reader "+e.getMessage());
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    Log.e("error", "getModelName:IOException fileReader "+e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 是否支持相机
     */
    public static boolean cameraCheck(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        }
        return false;
    }

    public static void getMapsInfo(List<Pair<String, String>> list) {
        Map<String, Integer> map = new HashMap<>();
        // librs_jni.so osVer=4.3
        String[] array = {"libRSDriver.so", "libRSCpuRef.so"};

        String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(mapsFilename);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                for (String item : array) {
                    if (line.contains(item)) {
                        if (map.containsKey(item)) {
                            int temp = map.get(item) + 1;
                            map.put(item, temp);
                        } else {
                            map.put(item, 1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(new Pair<>(entry.getKey(), String.valueOf(entry.getValue())));
        }
    }

    public static native int specialFilesEmulatorCheck();

    public static native int bluetoothCheck();

    public static native int getArch();

    public static native String getMapsArch();

    public static native int thermalCheck();

}
