package com.quicktvui.support.core.module.device;

/**
 * 设备信息
 */
public class AndroidDevice {

    private String deviceId;
    private String deviceType;

    //
    private String ethMac;
    private String wifiMac;

    //
    private long totalMemory;
    private long availableMemory;

    //
    private long screenWidth;
    private long screenHeight;
    private long windowWidth;
    private long windowHeight;
    //
    private String resolution;
    private float density;
    private float densityDpi;
    private float scaledDensity;

    //
    private String buildModel;
    private String buildBrand;
    private String buildDevice;
    private String buildBoard;
    private String buildProduct;
    private String buildHardware;
    private String buildManufacturer;

    private String buildSerial;
    private String buildTags;
    private String buildId;
    private long buildTime;
    private String buildType;
    private String buildUser;
    private String buildBootloader;
    private String buildDisplay;
    private String buildFingerPrint;

    //
    private String buildVersionRelease;
    private String buildVersionIncremental;
    private String buildVersionBaseOS;
    private String buildVersionCodeName;
    private String buildVersionSecurityPatch;
    private int buildVersionPreviewSDKInt;
    private int buildVersionSDKInt;

    private String ipAddress;

    //-----------------------------------------------------------


    public String getEthMac() {
        return ethMac;
    }

    public void setEthMac(String ethMac) {
        this.ethMac = ethMac;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(long availableMemory) {
        this.availableMemory = availableMemory;
    }

    public long getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(long screenWidth) {
        this.screenWidth = screenWidth;
    }

    public long getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(long screenHeight) {
        this.screenHeight = screenHeight;
    }

    public long getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(long windowWidth) {
        this.windowWidth = windowWidth;
    }

    public long getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(long windowHeight) {
        this.windowHeight = windowHeight;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getDensityDpi() {
        return densityDpi;
    }

    public void setDensityDpi(float densityDpi) {
        this.densityDpi = densityDpi;
    }

    public float getScaledDensity() {
        return scaledDensity;
    }

    public void setScaledDensity(float scaledDensity) {
        this.scaledDensity = scaledDensity;
    }

    public String getBuildVersionRelease() {
        return buildVersionRelease;
    }

    public void setBuildVersionRelease(String buildVersionRelease) {
        this.buildVersionRelease = buildVersionRelease;
    }

    public String getBuildModel() {
        return buildModel;
    }

    public void setBuildModel(String buildModel) {
        this.buildModel = buildModel;
    }

    public String getBuildBrand() {
        return buildBrand;
    }

    public void setBuildBrand(String buildBrand) {
        this.buildBrand = buildBrand;
    }

    public String getBuildDevice() {
        return buildDevice;
    }

    public void setBuildDevice(String buildDevice) {
        this.buildDevice = buildDevice;
    }

    public String getBuildBoard() {
        return buildBoard;
    }

    public void setBuildBoard(String buildBoard) {
        this.buildBoard = buildBoard;
    }

    public String getBuildProduct() {
        return buildProduct;
    }

    public void setBuildProduct(String buildProduct) {
        this.buildProduct = buildProduct;
    }

    public String getBuildHardware() {
        return buildHardware;
    }

    public void setBuildHardware(String buildHardware) {
        this.buildHardware = buildHardware;
    }

    public String getBuildManufacturer() {
        return buildManufacturer;
    }

    public void setBuildManufacturer(String buildManufacturer) {
        this.buildManufacturer = buildManufacturer;
    }

    public String getBuildSerial() {
        return buildSerial;
    }

    public void setBuildSerial(String buildSerial) {
        this.buildSerial = buildSerial;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBuildTags() {
        return buildTags;
    }

    public void setBuildTags(String buildTags) {
        this.buildTags = buildTags;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getBuildUser() {
        return buildUser;
    }

    public void setBuildUser(String buildUser) {
        this.buildUser = buildUser;
    }

    public String getBuildBootloader() {
        return buildBootloader;
    }

    public void setBuildBootloader(String buildBootloader) {
        this.buildBootloader = buildBootloader;
    }

    public String getBuildDisplay() {
        return buildDisplay;
    }

    public void setBuildDisplay(String buildDisplay) {
        this.buildDisplay = buildDisplay;
    }

    public String getBuildFingerPrint() {
        return buildFingerPrint;
    }

    public void setBuildFingerPrint(String buildFingerPrint) {
        this.buildFingerPrint = buildFingerPrint;
    }

    public String getBuildVersionIncremental() {
        return buildVersionIncremental;
    }

    public void setBuildVersionIncremental(String buildVersionIncremental) {
        this.buildVersionIncremental = buildVersionIncremental;
    }

    public String getBuildVersionBaseOS() {
        return buildVersionBaseOS;
    }

    public void setBuildVersionBaseOS(String buildVersionBaseOS) {
        this.buildVersionBaseOS = buildVersionBaseOS;
    }

    public String getBuildVersionCodeName() {
        return buildVersionCodeName;
    }

    public void setBuildVersionCodeName(String buildVersionCodeName) {
        this.buildVersionCodeName = buildVersionCodeName;
    }

    public String getBuildVersionSecurityPatch() {
        return buildVersionSecurityPatch;
    }

    public void setBuildVersionSecurityPatch(String buildVersionSecurityPatch) {
        this.buildVersionSecurityPatch = buildVersionSecurityPatch;
    }

    public int getBuildVersionPreviewSDKInt() {
        return buildVersionPreviewSDKInt;
    }

    public void setBuildVersionPreviewSDKInt(int buildVersionPreviewSDKInt) {
        this.buildVersionPreviewSDKInt = buildVersionPreviewSDKInt;
    }

    public int getBuildVersionSDKInt() {
        return buildVersionSDKInt;
    }

    public void setBuildVersionSDKInt(int buildVersionSDKInt) {
        this.buildVersionSDKInt = buildVersionSDKInt;
    }

    @Override
    public String toString() {
        return "AndroidDevice{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", ethMac='" + ethMac + '\'' +
                ", wifiMac='" + wifiMac + '\'' +
                ", totalMemory=" + totalMemory +
                ", availableMemory=" + availableMemory +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", windowWidth=" + windowWidth +
                ", windowHeight=" + windowHeight +
                ", resolution='" + resolution + '\'' +
                ", density=" + density +
                ", densityDpi=" + densityDpi +
                ", scaledDensity=" + scaledDensity +
                ", buildModel='" + buildModel + '\'' +
                ", buildBrand='" + buildBrand + '\'' +
                ", buildDevice='" + buildDevice + '\'' +
                ", buildBoard='" + buildBoard + '\'' +
                ", buildProduct='" + buildProduct + '\'' +
                ", buildHardware='" + buildHardware + '\'' +
                ", buildManufacturer='" + buildManufacturer + '\'' +
                ", buildSerial='" + buildSerial + '\'' +
                ", buildTags='" + buildTags + '\'' +
                ", buildId='" + buildId + '\'' +
                ", buildTime='" + buildTime + '\'' +
                ", buildType='" + buildType + '\'' +
                ", buildUser='" + buildUser + '\'' +
                ", buildBootloader='" + buildBootloader + '\'' +
                ", buildDisplay='" + buildDisplay + '\'' +
                ", buildFingerPrint='" + buildFingerPrint + '\'' +
                ", buildVersionRelease='" + buildVersionRelease + '\'' +
                ", buildVersionIncremental='" + buildVersionIncremental + '\'' +
                ", buildVersionBaseOS='" + buildVersionBaseOS + '\'' +
                ", buildVersionCodeName='" + buildVersionCodeName + '\'' +
                ", buildVersionSecurityPatch='" + buildVersionSecurityPatch + '\'' +
                ", buildVersionPreviewSDKInt='" + buildVersionPreviewSDKInt + '\'' +
                ", buildVersionSDKInt='" + buildVersionSDKInt + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
