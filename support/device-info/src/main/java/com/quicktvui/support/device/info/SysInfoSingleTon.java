package com.quicktvui.support.device.info;

import java.util.ArrayList;
import java.util.List;

public class SysInfoSingleTon {
    private static final int BATTCHGCNTMETHOD_UNDETECTED = 0;
    private static final int BATTCHGRATEMETHOD_UNDETECTED = 0;
    private static final int GPUCLKMETHOD_UNDETECTED = 0;
    private static final int GPUUTIMETHOD_UNDETECTED = 0;
    public static final String OGLES_EXT_ID = "OGLES_Ext";
    public static final String OGLES_REN_ID = "OGLES_Ren";
    public static final String OGLES_VEN_ID = "OGLES_Ven";
    public static final String OGLES_VER_ID = "OGLES_Ver";
    private static SysInfoSingleTon mInstance = null;
    public int BattChgCnt_Method = 0;
    public int BattChgRate_Method = 0;
    public int BattChgRate_Multiplier = -1;
    public int CPUCoreClk_MaxCoreIdx = 0;
    public Float CPUCycles_Idle = Float.valueOf(0.0f);
    public Float CPUCycles_Used = Float.valueOf(0.0f);
    public ARMCPUInfo CPUInfo_ARM_CPUInfo = new ARMCPUInfo();
    public List<ARMCPUInfo> CPUInfo_ARM_CPUList = new ArrayList();
    public int CPUInfo_ARM_CoreCount = -1;
    public boolean CPUInfo_ARM_isBigLittle4_4 = false;
    public boolean CPUInfo_ARM_isExynos5410_5420 = false;
    public boolean CPUInfo_isARM = false;
    public boolean CPUInfo_isX86 = false;
    public boolean CPUInfo_isX86_AMD = false;
    public boolean CPUInfo_isX86_Intel = false;
    public boolean CPUInfo_isX86_VIA = false;
    public int CPUInfo_x86_CPUIDRev = -1;
    public int CPUInfo_x86_CoreCount = -1;
    public int CPUInfo_x86_Family = -1;
    public int CPUInfo_x86_LogicalCPUs = -1;
    public int CPUInfo_x86_Model = -1;
    public String CPUInfo_x86_ModelName = "";
    public int CPUInfo_x86_Stepping = -1;
    public String CPUInfo_x86_VendorID = "";
    public int DB_Battery_Capacity = -1;
    public String DB_Device_Model = "";
    public int DB_Device_Type = -1;
    public float DB_LCD_Diagonal = -1.0f;
    public int DB_LCD_Res = -1;
    public int DB_LCD_Type = -1;
    public int DB_RAM_Type = -1;
    public int GPUClock_Method = 0;
    public long GPUMinClock = -1;
    public int GPUUtil_Method = 0;
    public boolean OGLES_Changed = false;
    public String OGLES_Extensions = "";
    public String OGLES_Renderer = "";
    public String OGLES_Vendor = "";
    public String OGLES_Version = "";
    public boolean OGLES_Was = false;
    public int Sensor_MaxHWMonSensorIdx = 0;
    public int Sensor_MaxTZSensorIdx = 0;
    public int SoC_Model = -1;
    public boolean isRoundWatch = false;
    public int lastSelectedPage = 0;

    public static class ARMCPUInfo {
        public String arch = "";
        public String chipName = "";
        public String hardware = "";
        public int impl = -1;
        public String msmHardware = "";
        public int part = -1;
        public int rev = -1;
        public int variant = -1;

        public ARMCPUInfo() {
        }

        public ARMCPUInfo(ARMCPUInfo toClone) {
            this.chipName = toClone.chipName;
            this.hardware = toClone.hardware;
            this.msmHardware = toClone.msmHardware;
            this.impl = toClone.impl;
            this.arch = toClone.arch;
            this.variant = toClone.variant;
            this.part = toClone.part;
            this.rev = toClone.rev;
        }
    }

    public static synchronized SysInfoSingleTon getInstance() {
        SysInfoSingleTon sysInfoSingleTon;
        synchronized (SysInfoSingleTon.class) {
            if (mInstance == null) {
                mInstance = new SysInfoSingleTon();
                SysInfo.detectInitCPUInfo();
            }
            sysInfoSingleTon = mInstance;
        }
        return sysInfoSingleTon;
    }
}