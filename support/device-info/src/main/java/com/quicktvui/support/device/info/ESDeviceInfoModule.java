package com.quicktvui.support.device.info;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.device.info.model.beans.StorageBean;
import com.quicktvui.support.device.info.model.info.StoreInfo;
import com.quicktvui.support.device.info.utils.CommandUtils;
import com.quicktvui.support.device.info.utils.DensityUtils;
import com.quicktvui.support.device.info.utils.DeviceUtils;
import com.quicktvui.support.device.info.utils.EmulatorUtils;
import com.quicktvui.support.device.info.utils.FileUtils;
import com.quicktvui.support.device.info.utils.GatewayUtils;
import com.quicktvui.support.device.info.utils.NetWorkUtils;
import com.quicktvui.support.device.info.utils.RootUtils;
import com.quicktvui.support.device.info.utils.SocUtils;
import com.sunrain.toolkit.utils.NetworkUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@ESKitAutoRegister
public class ESDeviceInfoModule implements IEsModule, IEsInfo {

    private Context mContext;
    private String currentIp = "";

    @Override
    public void init(Context context) {
        mContext = context;
//        setWebViewDataDirectorySuffix();
    }

    private static void setWebViewDataDirectorySuffix() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        WebView.setDataDirectorySuffix(Application.getProcessName());
    }

    //基础信息----------------
    public void getBaseInfo(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("brand", Build.BRAND != null ? Build.BRAND : "");//品牌
        map.pushString("model", Build.MODEL != null ? Build.MODEL : "");//型号
        String machine = "";
        machine = EmulatorUtils.getModelName();//cpu型号 对应cpuinfo中 model name
        SysInfo.detectInitCPUInfo();
        SysInfoSingleTon sysInfoST = SysInfoSingleTon.getInstance();
        if (sysInfoST.SoC_Model == -1) {
            sysInfoST.SoC_Model = SysInfo.getSoCModel();
        }
        List<String> cpuArchList = SysInfo.getCPUCoreArchitecture();
        if (cpuArchList.size() > 0) {
            if (cpuArchList.size() > 1) {
                map.pushString("cpu", cpuArchList.get(0) + "," + cpuArchList.get(1));//cpu型号
            } else {
                map.pushString("cpu", cpuArchList.get(0));//cpu型号
            }
        } else {
            map.pushString("cpu", machine);
        }
        if (TextUtils.isEmpty(machine)) {
            machine = CommandUtils.execute("uname -m");////cpu型号 对应adb model name
            Log.d("test", "getBaseInfo:2 ------------>" + machine);
        }
        //map.pushString("cpu", TextUtils.isEmpty(machine) ? Build.MODEL : machine);//cpu型号
        map.pushString("machine", machine);//当前cpu型号
        int cores = Objects.requireNonNull(new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER)).length;
        if (cores > 0) {
            map.pushInt("cores", cores);//内核数
        } else {
            map.pushString("cores", "");//内核数
        }
        List<StorageBean> list = StoreInfo.getStoreInfo(mContext);
        StorageBean storageBean = list.get(0);
        if (storageBean != null) {
            map.pushString("memory", storageBean.getTotalMemory());
            map.pushString("storage", storageBean.getRomSize());
            map.pushString("free", storageBean.getFreeStore());
            map.pushString("used", storageBean.getUsedStore());
        } else {
            map.pushString("memory", "");
            map.pushString("storage", "");
            map.pushString("free", "");
            map.pushString("used", "");
        }
        WifiInfo wifiInfo = GatewayUtils.getWifiInfo(mContext);
        if (wifiInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int frequency = wifiInfo.getFrequency();
                if (frequency > 0) {
                    map.pushBoolean("support5G", true);
                } else {
                    map.pushBoolean("support5G", false);
                }
            } else {
                map.pushBoolean("support5G", false);
            }
        } else {
            map.pushBoolean("support5G", false);
        }
        String width = String.valueOf(DensityUtils.getScreenWidth(mContext));
        String height = String.valueOf(DensityUtils.getScreenHeight(mContext));
        map.pushString("resolution", width + " X " + height);
        map.pushString("width", TextUtils.isEmpty(width) ? "" : width);
        map.pushString("height", TextUtils.isEmpty(height) ? "" : height);
        setVuePromise(map, promise);
    }

    //设备品牌
    public void getDeviceBrand(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("brand", Build.BRAND != null ? Build.BRAND : "");//品牌
        setVuePromise(map, promise);
    }

    //设备型号
    public void getDeviceModel(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("model", Build.MODEL != null ? Build.MODEL : "");//型号
        setVuePromise(map, promise);
    }

    //cpu核心型号
    public void getDeviceCpu(EsPromise promise) {
        EsMap map = new EsMap();
        String machine = "";
        machine = EmulatorUtils.getModelName();//cpu型号 对应cpuinfo中 model name
        SysInfo.detectInitCPUInfo();
        SysInfoSingleTon sysInfoST = SysInfoSingleTon.getInstance();
        if (sysInfoST.SoC_Model == -1) {
            sysInfoST.SoC_Model = SysInfo.getSoCModel();
        }
        List<String> cpuArchList = SysInfo.getCPUCoreArchitecture();
        if (cpuArchList.size() > 0) {
            if (cpuArchList.size() > 1) {
                map.pushString("cpu", cpuArchList.get(0) + "," + cpuArchList.get(1));//cpu型号
            } else {
                map.pushString("cpu", cpuArchList.get(0));//cpu型号
            }
        } else {
            map.pushString("cpu", machine);
        }
        setVuePromise(map, promise);
    }

    //cpu 核心数
    public void getDeviceCpuCores(EsPromise promise) {
        EsMap map = new EsMap();
        int cores = Objects.requireNonNull(new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER)).length;
        if (cores > 0) {
            map.pushInt("cores", cores);//内核数
        } else {
            map.pushString("cores", "");//内核数
        }
        setVuePromise(map, promise);
    }

    //运行内存
    public void getDeviceMemory(EsPromise promise) {
        List<StorageBean> list = StoreInfo.getStoreInfo(mContext);
        StorageBean bean = list.get(0);
        EsMap map = new EsMap();
        if (bean != null) {
            map.pushString("memory", bean.getTotalMemory());
        } else {
            map.pushString("memory", "");
        }
        setVuePromise(map, promise);
    }

    //存储空间
    public void getDeviceStorage(EsPromise promise) {
        List<StorageBean> list = StoreInfo.getStoreInfo(mContext);
        StorageBean bean = list.get(0);
        EsMap map = new EsMap();
        if (bean != null) {
            map.pushString("storage", bean.getRomSize());
            map.pushString("free", bean.getFreeStore());
            map.pushString("used", bean.getUsedStore());
        } else {
            map.pushString("storage", "");
            map.pushString("free", "");
            map.pushString("used", "");
        }
        setVuePromise(map, promise);
    }

    //分辨率
    public void getDeviceResolution(EsPromise promise) {
        EsMap map = new EsMap();
        String width = String.valueOf(DensityUtils.getScreenWidth(mContext));
        String height = String.valueOf(DensityUtils.getScreenHeight(mContext));
        map.pushString("resolution", width + " X " + height);
        map.pushString("width", TextUtils.isEmpty(width) ? "" : width);
        map.pushString("height", TextUtils.isEmpty(height) ? "" : height);
        setVuePromise(map, promise);
    }

    //5G
    public void getDevice5G(EsPromise promise) {
        WifiInfo wifiInfo = GatewayUtils.getWifiInfo(mContext);
        EsMap map = new EsMap();
        if (wifiInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int frequency = wifiInfo.getFrequency();
                if (frequency > 0) {
                    map.pushBoolean("support5G", true);
                } else {
                    map.pushBoolean("support5G", false);
                }
            } else {
                map.pushBoolean("support5G", false);
            }
        } else {
            map.pushBoolean("support5G", false);
        }
        setVuePromise(map, promise);
    }

    //网卡速率
    public void getDeviceLinkSpeed(EsPromise promise) {
        WifiInfo wifiInfo = GatewayUtils.getWifiInfo(mContext);
        EsMap map = new EsMap();
        if (wifiInfo != null) {
            int linkSpeed = wifiInfo.getLinkSpeed();
            map.pushString("linkSpeed", linkSpeed + " Mbps");
        } else {
            map.pushString("linkSpeed", 0 + " Mbps");
        }
        setVuePromise(map, promise);
    }

    //系统信息------------
    public void getSystemInfo(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("brand", !TextUtils.isEmpty(Build.BRAND) ? Build.BRAND : "");//品牌
        map.pushString("model", !TextUtils.isEmpty(Build.MODEL) ? Build.MODEL : "");//型号
        map.pushString("manufacture", TextUtils.isEmpty(Build.MANUFACTURER) || Build.MANUFACTURER.equals("unknown") ? SocUtils.getManufacturerInfo() : Build.MANUFACTURER);//制造商
        map.pushString("board", !TextUtils.isEmpty(Build.BOARD) ? Build.BOARD : "");//主板
        map.pushString("platform", SocUtils.getSocInfo());//平台
        map.pushString("hardware", !TextUtils.isEmpty(Build.HARDWARE) ? Build.HARDWARE : "");//硬件
        setVuePromise(map, promise);
    }

    //制造商
    public void getDeviceManufacture(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("manufacture", TextUtils.isEmpty(Build.MANUFACTURER) || Build.MANUFACTURER.equals("unknown") ? SocUtils.getManufacturerInfo() : Build.MANUFACTURER);//制造商
        setVuePromise(map, promise);
    }

    //主板
    public void getDeviceBoard(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("board", !TextUtils.isEmpty(Build.BOARD) ? Build.BOARD : "");//主板
        setVuePromise(map, promise);
    }

    //获取设备平台
    public void getDevicePlatform(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("platform", SocUtils.getSocInfo());
        setVuePromise(map, promise);
    }

    //获取设备硬件
    public void getDeviceHard(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("hardware", !TextUtils.isEmpty(Build.HARDWARE) ? Build.HARDWARE : "");//硬件
        setVuePromise(map, promise);
    }

    //获取cpu信息------------
    public void getCpuInfo(EsPromise promise) {
        EsMap map = new EsMap();
        String machine = "";
        machine = EmulatorUtils.getModelName();//cpu型号 对应adb model name
        SysInfo.detectInitCPUInfo();
        SysInfoSingleTon sysInfoST = SysInfoSingleTon.getInstance();
        if (sysInfoST.SoC_Model == -1) {
            sysInfoST.SoC_Model = SysInfo.getSoCModel();
        }
        List<String> cpuArchList = SysInfo.getCPUCoreArchitecture();
        if (cpuArchList.size() > 0) {
            map.pushString("cpu", cpuArchList.get(0));//cpu型号
        } else {
            map.pushString("cpu", machine);
        }
        if (TextUtils.isEmpty(machine)) {
            machine = CommandUtils.execute("uname -m");////cpu型号 对应adb model name
        }
        map.pushString("machine", machine);//当前cpu型号
        String cpuModel = System.getProperty("os.arch");//获取 CPU 型号
        map.pushString("cpucore", cpuModel);//当前设备内核
        int cores = Objects.requireNonNull(new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER)).length;
        map.pushInt("cores", cores);//cpu核心数 几核
        if (cores > 0) {
            ArrayList<Integer> min = new ArrayList<>();
            ArrayList<Integer> max = new ArrayList<>();
            for (int i = 0; i < cores; i++) {
                int minValue = Integer.parseInt(FileUtils.readFile(String.format("/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_min_freq", i)));
                if (minValue > 0) {
                    min.add(minValue);
                }
                int maxValue = Integer.parseInt(FileUtils.readFile(String.format("/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i)));
                if (maxValue > 0) {
                    max.add(maxValue);
                }
            }
            Collections.sort(min);
            Collections.sort(max);
            if (max.size() > 0 && min.size() > 0) {
                map.pushInt("minCpuSeed", min.get(0) / 1000);//cpu最小频率
                map.pushInt("maxCpuSeed", max.get(max.size() - 1) / 1000);//cpu最大频率
                map.pushString("clockSeed", min.get(0) / 1000 + " - " + max.get(max.size() - 1) / 1000 + " MHz");//区间
            } else {
                map.pushString("minCpuSeed", "");//cpu最小频率
                map.pushString("maxCpuSeed", "");//cpu最大频率
                map.pushString("clockSeed", "");//区间
            }
        }
        StringBuilder abiList = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = Build.SUPPORTED_ABIS;
            if (abis.length > 0) {
                String currentAbi = Build.CPU_ABI;
                if (!currentAbi.equals(abis[0])) {
                    map.pushString("abi", abis[0]);//当前设备内核
                } else {
                    map.pushString("abi", Build.CPU_ABI);//当前设备内核
                }
            } else {
                map.pushString("abi", Build.CPU_ABI);//当前设备内核
            }
            for (String abi : abis) {
                abiList.append(abi).append(",");
            }
            if (abiList.length() > 0) {
                abiList.deleteCharAt(abiList.length() - 1); // 删除最后一个逗号
            }
        } else {
            abiList.append(Build.CPU_ABI);
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                abiList.append(",");
                abiList.append(Build.CPU_ABI2);
            }
            map.pushString("abi", Build.CPU_ABI);//当前设备内核
        }
        map.pushString("abiList", !TextUtils.isEmpty(abiList.toString()) ? abiList.toString() : "");//设备支持的abi
        setVuePromise(map, promise);
    }

    //获取内核架构
    public void getDeviceArchitecture(EsPromise promise) {
        EsMap map = new EsMap();
        StringBuilder abiList = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = Build.SUPPORTED_ABIS;
            if (abis.length > 0) {
                String currentAbi = Build.CPU_ABI;
                if (!currentAbi.equals(abis[0])) {
                    map.pushString("abi", abis[0]);//当前设备内核
                } else {
                    map.pushString("abi", Build.CPU_ABI);//当前设备内核
                }
            } else {
                map.pushString("abi", Build.CPU_ABI);//当前设备内核
            }
            for (String abi : abis) {
                abiList.append(abi).append(",");
            }
            if (abiList.length() > 0) {
                abiList.deleteCharAt(abiList.length() - 1); // 删除最后一个逗号
            }
        } else {
            abiList.append(Build.CPU_ABI);
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                abiList.append(",");
                abiList.append(Build.CPU_ABI2);
            }
            map.pushString("abi", Build.CPU_ABI);//当前设备内核
        }
        setVuePromise(map, promise);
    }

    //获取设备支持的ABI类型
    public void getDeviceABI(EsPromise promise) {
        EsMap map = new EsMap();
        StringBuilder abiList = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = Build.SUPPORTED_ABIS;
            if (abis.length > 0) {
                String currentAbi = Build.CPU_ABI;
                if (!currentAbi.equals(abis[0])) {
                    map.pushString("abi", abis[0]);//当前设备内核
                } else {
                    map.pushString("abi", Build.CPU_ABI);//当前设备内核
                }
            } else {
                map.pushString("abi", Build.CPU_ABI);//当前设备内核
            }
            for (String abi : abis) {
                abiList.append(abi).append(",");
            }
            if (abiList.length() > 0) {
                abiList.deleteCharAt(abiList.length() - 1); // 删除最后一个逗号
            }
        } else {
            abiList.append(Build.CPU_ABI);
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                abiList.append(",");
                abiList.append(Build.CPU_ABI2);
            }
            map.pushString("abi", Build.CPU_ABI);//当前设备内核
        }
        map.pushString("abiList", !TextUtils.isEmpty(abiList.toString()) ? abiList.toString() : "");//设备支持的abi
        setVuePromise(map, promise);
    }

    //获取cpu频率
    public void getDeviceClockSpeed(EsPromise promise) {
        EsMap map = new EsMap();
        int cores = Objects.requireNonNull(new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER)).length;
        map.pushInt("cores", cores);//cpu核心数 几核
        if (cores > 0) {
            ArrayList<Integer> min = new ArrayList<>();
            ArrayList<Integer> max = new ArrayList<>();
            for (int i = 0; i < cores; i++) {
                int minValue = Integer.parseInt(FileUtils.readFile(String.format("/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_min_freq", i)));
                if (minValue > 0) {
                    min.add(minValue);
                }
                int maxValue = Integer.parseInt(FileUtils.readFile(String.format("/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i)));
                if (maxValue > 0) {
                    max.add(maxValue);
                }
            }
            Collections.sort(min);
            Collections.sort(max);
            if (max.size() > 0 && min.size() > 0) {
                map.pushInt("minCpuSeed", min.get(0) / 1000);//cpu最小频率
                map.pushInt("maxCpuSeed", max.get(max.size() - 1) / 1000);//cpu最大频率
                map.pushString("clockSeed", min.get(0) / 1000 + " - " + max.get(max.size() - 1) / 1000 + " MHz");//区间
            } else {
                map.pushString("minCpuSeed", "");//cpu最小频率
                map.pushString("maxCpuSeed", "");//cpu最大频率
                map.pushString("clockSeed", "");//区间
            }
        }
        setVuePromise(map, promise);
    }

    //获取Android信息------------
    public void getAndroidInfo(EsPromise promise) {
        EsMap map = new EsMap();
        String version = Build.VERSION.RELEASE;
        String majorVersion = version.split("-")[0]; // 取"-"前面的部分
        if (version.split("-").length > 1) {
            String majorVersionLast = version.split("-", 2)[1]; // 取"-"后面的部分
            if (!TextUtils.isEmpty(majorVersionLast)) {
                map.pushString("majorVersionLast", majorVersionLast);//安卓小版本
            } else {
                map.pushString("majorVersionLast", "");//安卓小版本
            }
        } else {
            map.pushString("majorVersionLast", "");//安卓小版本
        }
        map.pushString("androidVersion", majorVersion);//安卓版本
        map.pushInt("apiVersion", Build.VERSION.SDK_INT);//api版本
        map.pushString("androidId", DeviceUtils.getAndroidId(mContext));//安卓id
        map.pushString("buildId", Build.ID);//buildId
        map.pushString("isRoot", RootUtils.isRoot(mContext));//0为非root 1root
        setVuePromise(map, promise);
    }

    //发布版本（安卓版本）
    public void getDeviceAndroidVersion(EsPromise promise) {
        EsMap map = new EsMap();
        String version = Build.VERSION.RELEASE;
        String majorVersion = version.split("-")[0]; // 取"-"前面的部分
        if (version.split("-").length > 1) {
            String majorVersionLast = version.split("-", 2)[1]; // 取"-"后面的部分
            if (!TextUtils.isEmpty(majorVersionLast)) {
                map.pushString("majorVersionLast", majorVersionLast);//安卓小版本
            } else {
                map.pushString("majorVersionLast", "");//安卓小版本
            }
        } else {
            map.pushString("majorVersionLast", "");//安卓小版本
        }
        map.pushString("androidVersion", majorVersion);//安卓版本
        setVuePromise(map, promise);
    }

    //API版本
    public void getDeviceApiVersion(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushInt("apiVersion", Build.VERSION.SDK_INT);
        setVuePromise(map, promise);
    }

    //获取安卓id
    public void getDeviceAndroidId(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("androidId", DeviceUtils.getAndroidId(mContext));
        setVuePromise(map, promise);
    }

    //buildId
    public void getDeviceBuildId(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("buildId", Build.ID);
        setVuePromise(map, promise);
    }

    //是否root //todo 多种方式 都要试一下
    public void getDeviceRoot(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("isRoot", RootUtils.isRoot(mContext));
        //map.pushString("isRoot", Build.TAGS);
        setVuePromise(map, promise);
    }

    //获取内存信息------------
    public void getStoreInfo(EsPromise promise) {
        EsMap map = new EsMap();
        List<StorageBean> list = StoreInfo.getStoreInfo(mContext);
        StorageBean bean = list.get(0);
        if (bean != null) {
            map.pushString("memory", bean.getTotalMemory());//运行内存-->实际设备总内存
            map.pushString("storage", bean.getRomSize());//总存储空间
            map.pushString("free", bean.getFreeStore());//剩余存储空间
            map.pushString("used", bean.getUsedStore());//已使用空间
        } else {
            map.pushString("used", "");
            map.pushString("memory", "");
            map.pushString("storage", "");
            map.pushString("free", "");
        }
        setVuePromise(map, promise);
    }

    //储存-剩余空间
    public void getDeviceFreeStore(EsPromise promise) {
        EsMap map = new EsMap();
        List<StorageBean> list = StoreInfo.getStoreInfo(mContext);
        StorageBean bean = list.get(0);
        if (bean != null) {
            map.pushString("free", bean.getFreeStore());
        } else {
            map.pushString("free", "");
        }
        setVuePromise(map, promise);
    }

    //获取网络信息------------
    public void getNetInfo(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushBoolean("netAvailability", NetWorkUtils.isNetworkConnected(mContext));//true false
        map.pushString("networkType", NetWorkUtils.getNetWorkType(mContext));//网络类型 WIFI/NONE
//        map.pushString("ssid", GatewayUtils.getBssid(mContext));
        map.pushString("ssid", GatewayUtils.getSsid(mContext));//ssid无线网络名
        String ethMac = NetworkUtils.getEthMac();
        String wifiMac = NetworkUtils.getWifiMac();
        map.pushString("ethMac", ethMac);//有线mac地址
        if (!TextUtils.isEmpty(wifiMac) && !wifiMac.equals("02:00:00:00:00:00")) {
            map.pushString("wifiMac", wifiMac);//无限mac地址
        } else {
            map.pushString("wifiMac", "");//无限mac地址
        }

        String ipv4Value = "";
        String ipv6Value = "";
        Map<String, String> ips = GatewayUtils.getIp(mContext);
        if (ips.containsKey("wlan0")) {
            ipv4Value = ips.get("wlan0");
            ipv6Value = GatewayUtils.getHostIpv6(ips.get("wlan0_ipv6"));
        } else if (ips.containsKey("en0")) {
            ipv4Value = ips.get("en0");
            ipv6Value = GatewayUtils.getHostIpv6(ips.get("network_name"));
        } else if (ips.containsKey("vpn")) {
            ipv4Value = ips.get("vpn");
        }
        map.pushString("ipv4", ipv4Value);
        map.pushString("ipv6", ipv6Value);
        String dns1Value = "";
        String dns2Value = "";
        String dnsValue = NetWorkUtils.getDnsServer();
        //NetWorkUtils.getNetworkInterfaceInfo(mContext);
        if (!TextUtils.isEmpty(dnsValue) && !dnsValue.equals(",")) {
            String[] dnsList = dnsValue.split(",");
            if (dnsList.length == 1) {
                dns1Value = dnsList[0];
                map.pushString("dns1", dns1Value);
                map.pushString("dns2", "");
            } else if (dnsList.length > 1) {
                dns1Value = dnsList[0];
                map.pushString("dns1", dns1Value);
                dns2Value = dnsList[1];
                map.pushString("dns2", dns2Value);
            }
        } else {
            String dnsList = NetWorkUtils.getDnsByConnectivityManager(mContext);
            if (!TextUtils.isEmpty(dnsList)) {
                String[] dnsListArray = dnsList.split(",");
                if (dnsListArray.length == 1) {
                    dns1Value = dnsListArray[0];
                    map.pushString("dns1", dns1Value);
                    map.pushString("dns2", "");
                } else if (dnsListArray.length > 1) {
                    dns1Value = dnsListArray[0];
                    map.pushString("dns1", dns1Value);
                    dns2Value = dnsListArray[1];
                    map.pushString("dns2", dns2Value);
                }
            } else {
                map.pushString("dns1", "");
                map.pushString("dns2", "");
            }
        }
        String wifiVersion = "5GHz";
        int wifiType = NetWorkUtils.getWiFiFrequency(mContext);
        switch (wifiType) {
            case 1:
            case 2:
                wifiVersion = "2.4GHz";
                break;
            case 3:
                wifiVersion = "2.4GHz,5GHz";
                break;
            case 4:
                wifiVersion = "2.4GHz,5GHz,6GHz";
                break;
        }
        String wiredRate = "";
        String wirelessRate = "";
        wiredRate = NetWorkUtils.getWiredRate(mContext);//有线速率
        wirelessRate = NetWorkUtils.getWirelessRate(mContext);//无线速率
        map.pushBoolean("isSupport5G", wifiType > 2);//是否支持5g频段
        map.pushString("wifiBands", wifiVersion);//wifi频段 2.4GHz/5GHz
        map.pushString("wifiVersion", wifiVersion);//支持协议 todo
//        map.pushString("networkSpeed", networkSpeed);//兼容速率 网线返回有线速率 没网线返回无线速率
        map.pushString("wiredRate", wiredRate);//有线速率
        map.pushString("wirelessRate", wirelessRate);//无线速率
        map.pushString("supportProtocols", NetWorkUtils.getNetWorkType(mContext));
        setVuePromise(map, promise);
    }

    //获取网络协议 http https wifi SSL/TLS
    public void getNetworkProtocol(EsPromise promise) {
        EsMap map = new EsMap();
        StringBuilder netProtocols = new StringBuilder();
        String ssl = NetWorkUtils.getNetworkProtocol();
        if (TextUtils.isEmpty(ssl)) {
            NetWorkUtils.checkHttpsInterface(new NetWorkUtils.IPCallback() {
                @Override
                public void onResult(String value) {
                    if (!TextUtils.isEmpty(value)) {
                        netProtocols.append("HTTP");
                        netProtocols.append(",");
                        netProtocols.append(value);
                        if (!TextUtils.isEmpty(ssl)) {
                            netProtocols.append(",");
                            netProtocols.append(ssl);
                        }
                        map.pushString("networkProtocols", netProtocols.toString());
                        setVuePromise(map, promise);
                    } else {
                        netProtocols.append("HTTP");
                        if (!TextUtils.isEmpty(ssl)) {
                            netProtocols.append(",");
                            netProtocols.append(ssl);
                        }
                        map.pushString("networkProtocols", netProtocols.toString());
                        setVuePromise(map, promise);
                    }
                }

                @Override
                public void onError() {
                    netProtocols.append("HTTP");
                    if (!TextUtils.isEmpty(ssl)) {
                        netProtocols.append(",");
                        netProtocols.append(ssl);
                    }
                    map.pushString("networkProtocols", netProtocols.toString());
                    setVuePromise(map, promise);
                }
            });
        } else {
            netProtocols.append("HTTP");
            netProtocols.append(",");
            netProtocols.append("HTTPS");
            if (!TextUtils.isEmpty(ssl)) {
                netProtocols.append(",");
                netProtocols.append(ssl);
            }
            map.pushString("networkProtocols", netProtocols.toString());
            setVuePromise(map, promise);
        }
    }

    //获取外网ip
    @Deprecated
    public void getOutIp(EsPromise promise) {
        EsMap map = new EsMap();
        if (TextUtils.isEmpty(currentIp)) {
            NetWorkUtils.getExternalIPWithOkHttp(value -> {
                if (!TextUtils.isEmpty(value)) {
                    currentIp = value;
                    EsMap map1 = new EsMap();
                    map1.pushString("externalIp", value);
                    setVuePromise(map1, promise);
                } else {
                    EsMap map1 = new EsMap();
                    map1.pushString("externalIp", "");
                    setVuePromise(map1, promise);
                }
            });
        } else {
            map.pushString("externalIp", currentIp);
            setVuePromise(map, promise);
        }
    }

    //获取网络连接速率-----
    public void getNetworkSpeed(EsPromise promise) {
        //无线网卡 返回的速率
        WifiInfo wifiInfo = GatewayUtils.getWifiInfo(mContext);
        EsMap map = new EsMap();
        if (wifiInfo != null) {
            int linkSpeed = wifiInfo.getLinkSpeed();
            if (linkSpeed > 0) {
                map.pushString("networkSpeed", linkSpeed + " Mbps");
            } else {
                map.pushString("networkSpeed", "");
            }
        } else {
            map.pushString("networkSpeed", "");
        }
        setVuePromise(map, promise);
        /*EsMap map = new EsMap();
        NetWorkUtils.getWifiSpeed(result -> {
            if (!TextUtils.isEmpty(result)) {
                map.pushString("networkSpeed", result + " Mbps");
            } else {
                map.pushString("networkSpeed", "");
            }
            setVuePromise(map, promise);
        });*/
    }

    //网络状态wifi-------
    public void getNetworkState(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushBoolean("netAvailability", NetWorkUtils.isNetworkConnected(mContext));
        setVuePromise(map, promise);
    }

    //网络类型
    public void getNetworkType(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("networkType", NetWorkUtils.getNetWorkType(mContext));
        setVuePromise(map, promise);
    }

    //SSID
    public void getSSID(EsPromise promise) {
        EsMap map = new EsMap();
        map.pushString("ssid", GatewayUtils.getSsid(mContext));
        setVuePromise(map, promise);
    }

    //mac地址 todo 多种mac 看如何获取
    public void getMac(EsPromise promise) {
        EsMap map = new EsMap();
        String ethMac = NetworkUtils.getEthMac();
        String wifiMac = NetworkUtils.getWifiMac();
        map.pushString("ethMac", ethMac);//有线mac地址
        if (!TextUtils.isEmpty(wifiMac) && !wifiMac.equals("02:00:00:00:00:00")) {
            map.pushString("wifiMac", wifiMac);//无限mac地址
        } else {
            map.pushString("wifiMac", "");//无限mac地址
        }
        setVuePromise(map, promise);
    }

    //ipv4 ipv6
    public void getIp(EsPromise promise) {
        Map<String, String> ips = GatewayUtils.getIp(mContext);
        String ipv4Value = "";
        String ipv6Value = "";
        if (ips.containsKey("en0")) {
            ipv4Value = ips.get("en0");
            ipv6Value = GatewayUtils.getHostIpv6(ips.get("network_name"));
        } else if (ips.containsKey("vpn")) {
            ipv4Value = ips.get("vpn");
        }
        EsMap map = new EsMap();
        map.pushString("ipv4", ipv4Value);
        map.pushString("ipv6", ipv6Value);
        setVuePromise(map, promise);
    }

    //dns1 dns2
    public void getDeviceDns(EsPromise promise) {
        EsMap map = new EsMap();
        String dns1Value = "";
        String dns2Value = "";
        String dnsValue = NetWorkUtils.getDnsServer();
        if (!TextUtils.isEmpty(dnsValue)) {
            String[] dnsList = dnsValue.split(",");
            if (dnsList.length == 1) {
                dns1Value = dnsList[0];
                map.pushString("dns1", dns1Value);
                map.pushString("dns2", "");
            } else if (dnsList.length > 1) {
                dns1Value = dnsList[0];
                map.pushString("dns1", dns1Value);
                dns2Value = dnsList[1];
                map.pushString("dns2", dns2Value);
            }
            setVuePromise(map, promise);
        } else {
            setErrorVue(promise, "dns地址为空");
        }
    }

    //wifi版本 wifi频率
    public void getWifiVersion(EsPromise promise) {
        EsMap map = new EsMap();
        boolean isSupport5G = false;
        String wifiVersion = "5G";
        String networkSpeed = "0";

        int wifiType = NetWorkUtils.getWiFiFrequency(mContext);
        switch (wifiType) {
            case 1:
                wifiVersion = "2.4G";
                break;
            case 2:
                wifiVersion = "4G";
                break;
            case 3:
                wifiVersion = "5G";
                break;
        }
        map.pushBoolean("isSupport5G", wifiType > 2);
        map.pushString("wifiVersion", wifiVersion);
        map.pushString("supportProtocols", NetWorkUtils.getNetWorkType(mContext));
        setVuePromise(map, promise);
    }

    //设备显示信息-----------
    public void getDisplayInformation(EsPromise promise) {
        EsMap map = new EsMap();
        boolean isSupportHDR = DensityUtils.isHDRSupported();//是否支持HDR
        //boolean isSupportHDMI = DensityUtils.isHDMIOutputSupported();
        String hdmiVersion = DensityUtils.getHDMIVersion();//HDMI版本
        if (TextUtils.isEmpty(hdmiVersion)) {
            hdmiVersion = DensityUtils.getHDMIInfoFromSys();
        }
        //刷新率
        String refreshRate = DensityUtils.getRefreshRate(Objects.requireNonNull(EsProxy.get().getTopActivity())) + " Hz";
        //密度
        String densityValue = DensityUtils.getDensityDpi(mContext) + " (" + DensityUtils.getDensityId(mContext) + ")";
        //面板信息 屏幕供应商 显示技术等
        String panelInfo = DensityUtils.getPanelInfo();
        if (TextUtils.isEmpty(panelInfo)) {
            panelInfo = DensityUtils.getDisplayPanelInfo();
            if (TextUtils.isEmpty(panelInfo)) {
                panelInfo = DensityUtils.getPanelInfoFromSys(mContext);
            }
        }
        map.pushBoolean("isSupportHDR", isSupportHDR);//是否支持HDR
        map.pushString("hdmiVersion", hdmiVersion);//hdmi版本 为""不展示
        map.pushString("refreshRate", refreshRate);//刷新率
        map.pushString("density", densityValue);//屏幕密度
        map.pushString("panelInfo", panelInfo);//面板信息 为""不展示
        String width = String.valueOf(DensityUtils.getScreenWidth(mContext));
        String height = String.valueOf(DensityUtils.getScreenHeight(mContext));
        map.pushString("resolution", width + " X " + height);
        map.pushString("width", width);
        map.pushString("height", height);
        setVuePromise(map, promise);
    }

    //gpu信息 todo
    public void getGpuInfo(EsPromise promise) {
        EsMap map = new EsMap();
        if (TextUtils.isEmpty(SocUtils.gpuVendor) ||
                TextUtils.isEmpty(SocUtils.gpuRenderer) ||
                TextUtils.isEmpty(SocUtils.gpuVersion)) {
            SocUtils.getGpuInfoWithGLSurfaceView(map, promise);
        } else {
            map.pushString("gpuVendor", SocUtils.gpuVendor);
            map.pushString("gpuRenderer", SocUtils.gpuRenderer);
            map.pushString("gpuVersion", SocUtils.gpuVersion);
            setVuePromise(map, promise);
        }
    }

    public void getUsbInfo(EsPromise promise) {
        String usbValue;
        if (!TextUtils.isEmpty(RootUtils.isRoot(mContext)) && RootUtils.isRoot(mContext).equals("1")) {
            usbValue = DeviceUtils.SysUsbInfo();
        } else {
            if (DeviceUtils.isUsb3Support(mContext)) {
                usbValue = "USB 3.0";
            } else {
                usbValue = "USB 2.0";
            }
        }
        EsMap map = new EsMap();
        map.pushString("usbVersion", !TextUtils.isEmpty(usbValue) ? usbValue : "");
        setVuePromise(map, promise);
    }

    public void getBluetoothInfo(EsPromise promise) {
        String bluetoothVersion = DeviceUtils.getBluetoothVersion(mContext);
        EsMap map = new EsMap();
        map.pushString("bluetoothVersion", bluetoothVersion);
        setVuePromise(map, promise);
    }

    @Override
    public void destroy() {

    }

    //-------------------------------------------------------------------------------------------------------------------
    private static final FileFilter CPU_FILTER = pathname -> Pattern.matches("cpu[0-9]", pathname.getName());

    @Override
    public void getEsInfo(EsPromise promise) {
        /*EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, BuildConfig.ES_KIT_BUILD_TAG_COUNT);
            map.pushString(IEsInfo.ES_PROP_INFO_PACKAGE_NAME, BuildConfig.LIBRARY_PACKAGE_NAME);
            map.pushString(IEsInfo.ES_PROP_INFO_CHANNEL, BuildConfig.ES_KIT_BUILD_TAG_CHANNEL);
            map.pushString(IEsInfo.ES_PROP_INFO_BRANCH, BuildConfig.ES_KIT_BUILD_TAG);
            map.pushString(IEsInfo.ES_PROP_INFO_COMMIT_ID, BuildConfig.ES_KIT_BUILD_TAG_ID);
            map.pushString(IEsInfo.ES_PROP_INFO_RELEASE_TIME, BuildConfig.ES_KIT_BUILD_TAG_TIME);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);*/
    }

    private void setVuePromise(EsMap map, EsPromise promise) {
        try {
            promise.resolve(map);
        } catch (Throwable e) {
            promise.reject("程序异常");
            e.printStackTrace();
        }
    }

    private void setErrorVue(EsPromise promise, String value) {
        try {
            EsMap map = new EsMap();
            map.pushString("message", value);
            promise.resolve(map);
        } catch (Throwable e) {
            promise.reject("程序异常");
            e.printStackTrace();
        }
    }
}
