package com.quicktvui.sdk.core.internal.loader;

import com.tencent.mtt.hippy.HippyRootView;

import java.io.File;

import com.quicktvui.sdk.core.entity.InfoEntity;

public class TaskEntity {
    private InfoEntity infoEntity;
    private HippyRootView hippyRootView;
    private File rpkFile;
    private boolean isConnectedNetwork;

    public File getRpkFile() {
        return rpkFile;
    }

    public void setRpkFile(File rpkFile) {
        this.rpkFile = rpkFile;
    }

    public boolean isConnectedNetwork() {
        return isConnectedNetwork;
    }

    public void setConnectedNetwork(boolean connectedNetwork) {
        isConnectedNetwork = connectedNetwork;
    }


    public HippyRootView getHippyRootView() {
        return hippyRootView;
    }

    public void setHippyRootView(HippyRootView hippyRootView) {
        this.hippyRootView = hippyRootView;
    }

    public InfoEntity getInfoEntity() {
        return infoEntity;
    }

    public void setInfoEntity(InfoEntity infoEntity) {
        this.infoEntity = infoEntity;
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "infoEntity=" + infoEntity +
                ", hippyRootView=" + hippyRootView +
                ", rpkFile=" + rpkFile +
                ", isConnectedNetwork=" + isConnectedNetwork +
                '}';
    }
}
