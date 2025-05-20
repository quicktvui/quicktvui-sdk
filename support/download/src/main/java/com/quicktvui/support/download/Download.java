package com.quicktvui.support.download;

import java.io.File;

/**
 *
 */
public class Download {

    private int id;
    private String fileUrl;
    private String fileMD5;
    private String fileName;
    private String fileType;
    private long fileLength;
    private long downloadLength;
    private File file;
    private DownloadParams params;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getDownloadLength() {
        return downloadLength;
    }

    public void setDownloadLength(long downloadLength) {
        this.downloadLength = downloadLength;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public DownloadParams getParams() {
        return params;
    }

    public void setParams(DownloadParams params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Download)) return false;

        Download download = (Download) o;

        return getFileUrl() != null ? getFileUrl().equals(download.getFileUrl()) : download.getFileUrl() == null;
    }

    @Override
    public int hashCode() {
        return getFileUrl() != null ? getFileUrl().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Download{" + "id=" + id + ", fileUrl='" + fileUrl + '\'' + ", fileMD5='" + fileMD5 + '\'' + ", fileName='" + fileName + '\'' + ", fileType='" + fileType + '\'' + ", fileLength=" + fileLength + ", downloadLength=" + downloadLength + ", file=" + file + ", params=" + params + '}';
    }
}
