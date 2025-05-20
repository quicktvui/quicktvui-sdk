package com.quicktvui.support.device.info.model.beans;

import org.json.JSONObject;


public class StorageBean {

    /**
     * 剩余存储空间
     */
    private String freeStore;
    /**
     * 已用存储空间
     */
    private String usedStore;
    /**
     * 总共存储空间
     */
    private String totalStore;
    /**
     * 使用率
     */
    private int ratioStore;
    /**
     * 存储路径
     */
    private String storePath;

    /**
     * 剩余存储空间
     */
    private String freeMemory;
    /**
     * 已用存储空间
     */
    private String usedMemory;
    /**
     * 总共存储空间
     */
    private String totalMemory;
    /**
     * 使用率
     */
    private int ratioMemory;
    /**
     * 内存信息
     */
    private String memInfo;
    /**
     * 真实 ROM 空间
     */
    private String romSize;

    public String getFreeStore() {
        return freeStore;
    }

    public void setFreeStore(String freeStore) {
        this.freeStore = freeStore;
    }

    public String getUsedStore() {
        return usedStore;
    }

    public void setUsedStore(String usedStore) {
        this.usedStore = usedStore;
    }

    public String getTotalStore() {
        return totalStore;
    }

    public void setTotalStore(String totalStore) {
        this.totalStore = totalStore;
    }

    public int getRatioStore() {
        return ratioStore;
    }

    public void setRatioStore(int ratioStore) {
        this.ratioStore = ratioStore;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
    }

    public String getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getRatioMemory() {
        return ratioMemory;
    }

    public void setRatioMemory(int ratioMemory) {
        this.ratioMemory = ratioMemory;
    }

    public String getMemInfo() {
        return memInfo;
    }

    public void setMemInfo(String memInfo) {
        this.memInfo = memInfo;
    }

    public String getRomSize() {
        return romSize;
    }

    public void setRomSize(String romSize) {
        this.romSize = romSize;
    }

    @Override
    public String toString() {
        return "StorageBean{" +
                "freeStore='" + freeStore + '\'' +
                ", usedStore='" + usedStore + '\'' +
                ", totalStore='" + totalStore + '\'' +
                ", ratioStore=" + ratioStore +
                ", storePath='" + storePath + '\'' +
                ", freeMemory='" + freeMemory + '\'' +
                ", usedMemory='" + usedMemory + '\'' +
                ", totalMemory='" + totalMemory + '\'' +
                ", ratioMemory=" + ratioMemory +
                ", memInfo='" + memInfo + '\'' +
                ", romSize='" + romSize + '\'' +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("freeStore=", freeStore);
            jsonObject.put("usedStore", usedStore);
            jsonObject.put("totalStore", totalStore);
            jsonObject.put("ratioStore", ratioStore);
            jsonObject.put("storePath", storePath);
            jsonObject.put("freeMemory", freeMemory);
            jsonObject.put("usedMemory", usedMemory);
            jsonObject.put("totalMemory", totalMemory);
            jsonObject.put("ratioMemory", ratioMemory);
            jsonObject.put("memInfo", memInfo);
            jsonObject.put("romSize", romSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}
