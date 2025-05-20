package com.quicktvui.support.device.info.model.beans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chensongsong on 2020/5/29.
 */
public class PartitionsBean {

    /**
     * 文件夹路径
     */
    private String path;
    /**
     * 挂载路径
     */
    private String mount;
    /**
     * 类型
     */
    private String fs;
    /**
     * 权限
     */
    private String mod;
    /**
     * 可用大小
     */
    private String used;
    /**
     * 总共大小
     */
    private String size;
    /**
     * 已用占比
     */
    private int ratio;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public String getFs() {
        return fs;
    }

    public void setFs(String fs) {
        this.fs = fs;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        return "PartitionsBean{" +
                "path='" + path + '\'' +
                ", mount='" + mount + '\'' +
                ", fs='" + fs + '\'' +
                ", mod='" + mod + '\'' +
                ", used='" + used + '\'' +
                ", size='" + size + '\'' +
                ", ratio=" + ratio +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("path", path);
            jsonObject.put("mount", mount);
            jsonObject.put("fs", fs);
            jsonObject.put("mod", mod);
            jsonObject.put("used", used);
            jsonObject.put("size", size);
            jsonObject.put("ratio", ratio);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
