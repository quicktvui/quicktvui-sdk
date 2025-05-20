package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.os.Build;
import android.util.Pair;

import com.quicktvui.support.device.info.utils.CommandUtils;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.device.info.utils.EmulatorUtils;

public class EmulatorInfo {

    /**
     * 获取应用列表
     *
     * @param context
     * @return
     */
//    @AddTrace(name = "EmulatorInfo.getEmulatorInfo")
    public static List<Pair<String, String>> getEmulatorInfo(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("Su Version", EmulatorUtils.getSuVersion()));
        list.add(new Pair<>("Sound", EmulatorUtils.getSound()));
        list.add(new Pair<>("Battery", EmulatorUtils.getBatteryInfo(context)));
        list.add(new Pair<>("BrandInfo", EmulatorUtils.getBrandInfo()));
        String launcher = EmulatorUtils.getLauncherPackageName(context);
        list.add(new Pair<>("Launcher", launcher));
        EmulatorUtils.setLauncherInfo(context, list, launcher);
        list.add(new Pair<>("ThermalCount", EmulatorUtils.thermalCheck() + ""));
        list.add(new Pair<>("CameraCheck", EmulatorUtils.cameraCheck(context) + ""));
        list.add(new Pair<>("RSSI", NetWorkInfo.getRssi(context)));
        list.add(new Pair<>("Abi", CommandUtils.getProperty("ro.product.cpu.abi")));
        list.add(new Pair<>("AbiList", CommandUtils.getProperty("ro.product.cpu.abilist")));
        list.add(new Pair<>("Hardware", CommandUtils.getProperty("ro.hardware")));
        list.add(new Pair<>("Arch", CommandUtils.execute("uname -m")));
        list.add(new Pair<>("BluetoothFile", EmulatorUtils.bluetoothCheck() + ""));
        list.add(new Pair<>("SpecialFiles", EmulatorUtils.specialFilesEmulatorCheck() + ""));
        list.add(new Pair<>("x86", EmulatorUtils.getArch() + ""));
        list.add(new Pair<>("MapsArch", EmulatorUtils.getMapsArch()));
        // 新增 ARM 模拟器检测特性
        EmulatorUtils.getMapsInfo(list);
        list.add(new Pair<>("Qemu", EmulatorUtils.qemuCheck() + ""));
        list.add(new Pair<>("Pipes", EmulatorUtils.checkPipes() + ""));
        list.add(new Pair<>("Model Name", EmulatorUtils.getModelName()));
        list.add(new Pair<>("Product", CommandUtils.getProperty("ro.product.name")));
        list.add(new Pair<>("Manufacturer", CommandUtils.getProperty("ro.product.manufacturer")));
        list.add(new Pair<>("Brand", CommandUtils.getProperty("ro.product.brand")));
        list.add(new Pair<>("Device", CommandUtils.getProperty("ro.product.device")));
        list.add(new Pair<>("Model", CommandUtils.getProperty("ro.product.model")));
        list.add(new Pair<>("Fingerprint", Build.FINGERPRINT));
        return list;
    }

}
