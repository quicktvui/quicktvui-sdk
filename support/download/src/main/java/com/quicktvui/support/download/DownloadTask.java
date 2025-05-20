package com.quicktvui.support.download;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

/**
 *
 */
public class DownloadTask implements IDownloadTask {

    private static final String TAG = "DownloadTask";

    private final boolean DEBUG = false;

    private DownloadListener downloadListener;

    private boolean isDownloading;
    private boolean isDownloadCanceled;
    private Download download;

    private Executor taskExecutor;
    private Handler mHandler;

    private long interpolator;

    private long lastTime = 0;

    private HttpRequest httpRequest;

    public DownloadTask(DownloadConfiguration configuration) {
        this.interpolator = configuration.interpolator;
        this.taskExecutor = configuration.taskExecutor;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void download(Download download) {
        this.download = download;
        this.notifyDownloadInit();
    }

    @Override
    public void start() {
        if (download == null) {
            if (DEBUG) {
                Log.e(TAG, "#------notifyDownloadError------download == null----------->>>");
            }

            DownloadMessage message = new DownloadMessage();
            message.setMessage("Download Bean is null");
            message.setCode(DownloadError.ERROR_ILLEGAL_ARGUMENT);
            notifyDownloadError(message);
            return;
        }
        File downloadFile = download.getFile();
        if (downloadFile == null) {
            if (DEBUG) {
                Log.e(TAG, "#------notifyDownloadError------downloadFile == null----------->>>");
            }
            DownloadMessage message = new DownloadMessage();
            message.setMessage("Download File  is null");
            message.setCode(DownloadError.ERROR_ILLEGAL_ARGUMENT);
            notifyDownloadError(message);
            return;
        }
        try {
            if (downloadFile.exists()) {
                if (DEBUG) {
                    Log.e(TAG, "#-------目标文件存在则删除----->>>" + downloadFile.getAbsolutePath());
                }
                downloadFile.delete();
                downloadFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //开始下载
        this.isDownloading = true;
        //
        this.notifyDownloadStart();

        //
        String url = download.getFileUrl();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    httpRequest = HttpRequest.get(url);
                    try {
                        if (download.getParams() != null //
                                && download.getParams().getHeader() != null //
                                && download.getParams().getHeader().size() > 0) {
                            httpRequest.headers(download.getParams().getHeader());
                            if (DEBUG) {
                                Log.e(TAG, "#------headers----->>>" + download.getParams().getHeader());
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    int responseCode = httpRequest.code();
                    if (DEBUG) {
                        Log.e(TAG, url + "#--------responseCode----------->>>" + responseCode);
                    }

                    if (httpRequest.ok()) {
                        httpRequest.progress(new HttpRequest.UploadProgress() {
                            @Override
                            public void onUpload(long uploaded, long total) {
                                if (DEBUG) {
                                    Log.e(TAG, total + "#--------onUpload----------->>>:" + uploaded);
                                }
                                long currentTime = System.currentTimeMillis();
                                if (((currentTime - lastTime) >= interpolator)
                                        || (uploaded == total && total > 0)) {
                                    DownloadProgress progress = new DownloadProgress();
                                    progress.setDownloadSize(uploaded);
                                    progress.setTotalSize(total);
                                    notifyDownloadProgress(progress);

                                    lastTime = currentTime;
                                }
                            }
                        });
                        try {
                            long fileLength = httpRequest.getConnection().getContentLength();
                            //
                            if (download != null) {
                                download.setFileLength(fileLength);
                            }
                            //
                            httpRequest.incrementTotalSize(
                                    httpRequest.getConnection().getContentLength()
                            );
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        try {
                            httpRequest.receive(downloadFile);
                        }catch (HttpRequest.HttpRequestException e){
                            if (isDownloadCanceled || !isDownloading) {
                                if (DEBUG) {
                                    Log.e(TAG, "#------下载停止或者取消---网络出错------->>>");
                                }
                                return;
                            }
                            throw e;
                        }

                        if (!isDownloading) {
                            return;
                        }
                        if (isDownloadCanceled) {
                            deleteDownloadFile();
                            return;
                        }
                        String md5 = FileDigest.getFileMD5(downloadFile);
                        if (DEBUG) {
                            Log.e(TAG, "#------FileDigest----Thread------------>>>"
                                    + Thread.currentThread().getName());
                            Log.e(TAG, "#------FileDigest---------------->>>" + md5);
                        }
                        if (download != null && !TextUtils.isEmpty(download.getFileMD5())) {
                            if (download.getFileMD5().equals(md5)) {
                                if (DEBUG) {
                                    Log.e(TAG, "#------FileDigest----MD5校验成功----------->>>");
                                }
                                if (mHandler != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDownloadSuccess();
                                        }
                                    });
                                }
                            } else {
                                if (DEBUG) {
                                    Log.e(TAG, "#------FileDigest----MD5校验失败----------->>>");
                                }
                                deleteDownloadFile();

                                if (mHandler != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            DownloadMessage message = new DownloadMessage();
                                            message.setMessage("MD5校验错误");
                                            message.setCode(DownloadError.ERROR_DOWNLOAD_FILE_MD5);
                                            notifyDownloadError(message);
                                        }
                                    });
                                }
                            }
                        }
                        //
                        else {
                            if (mHandler != null) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDownloadSuccess();
                                    }
                                });
                            }
                        }
                    }
                    //
                    else {
                        if (!isDownloading || isDownloadCanceled) {
                            if (DEBUG) {
                                Log.e(TAG, "#------------onResponse-------任务已经取消---->>>");
                            }
                            return;
                        }
                        if (mHandler != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadFile.delete();
                                    DownloadMessage message = new DownloadMessage();
                                    message.setMessage("网络请求错误");
                                    message.setCode(DownloadError.ERROR_DOWNLOAD_REQUEST);
                                    notifyDownloadError(message);
                                }
                            });
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    DownloadMessage message = new DownloadMessage();
                    message.setCode(DownloadError.ERROR_DOWNLOAD_IO_EXCEPTION);
                    notifyDownloadError(message);
                }
            }
        });
    }

    private void deleteDownloadFile() {
        if (this.download != null && download.getFile() != null) {
            if (download.getFile().exists()) {
                download.getFile().delete();
            }
        }
    }

    private void disconnect() {
        try {
            if (httpRequest != null && httpRequest.getConnection() != null) {
                if (DEBUG) {
                    Log.e(TAG, "----------disconnect-------->>>>");
                }
                httpRequest.disconnect();
                httpRequest = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (DEBUG) {
            Log.e(TAG, "################stop##################3");
        }
        this.isDownloading = false;
        this.disconnect();
        this.notifyDownloadStop();
    }

    @Override
    public void cancel() {
        if (DEBUG) {
            Log.e(TAG, "################stop##################3");
        }
        this.isDownloadCanceled = true;
        this.disconnect();
        //delete
        this.deleteDownloadFile();
        this.notifyDownloadCancel();
    }

    @Override
    public void setDownloadListener(DownloadListener listener) {
        this.downloadListener = listener;
    }

    public void notifyDownloadCancel() {
        if (downloadListener != null) {
            DownloadStatus<DownloadMessage> downloadStatus = new DownloadStatus<>();
            downloadStatus.setDownload(download);
            downloadStatus.setState(DownloadState.DOWNLOAD_STATE_CANCEL);
            downloadListener.onDownloadStatusChanged(downloadStatus);
        }
    }

    public void notifyDownloadError(DownloadMessage message) {
        if (DEBUG) {
            Log.e(TAG, "#------------下载失败------->>>");
        }
        if (downloadListener != null) {
            DownloadStatus<DownloadMessage> downloadStatus = new DownloadStatus<>();
            downloadStatus.setDownload(download);
            downloadStatus.setData(message);
            downloadStatus.setState(DownloadState.DOWNLOAD_STATE_ERROR);
            downloadListener.onDownloadStatusChanged(downloadStatus);
        }
    }

    public void notifyDownloadStop() {
        if (downloadListener != null) {
            if (DEBUG) {
                Log.e(TAG, "#------------下载停止------->>>");
            }
            DownloadStatus<DownloadMessage> downloadStatus = new DownloadStatus<>();
            downloadStatus.setDownload(download);
            downloadStatus.setState(DownloadState.DOWNLOAD_STATE_STOP);
            downloadListener.onDownloadStatusChanged(downloadStatus);
        }
    }

    public void notifyDownloadSuccess() {
        if (DEBUG) {
            Log.e(TAG, "#------------下载成功------->>>");
        }
        if (downloadListener != null) {
            DownloadStatus<DownloadMessage> downloadStatus = new DownloadStatus<>();
            downloadStatus.setDownload(download);
            downloadStatus.setState(DownloadState.DOWNLOAD_STATE_SUCCESS);
            downloadListener.onDownloadStatusChanged(downloadStatus);
        }
    }

    public void notifyDownloadInit() {
        if (downloadListener != null) {
            DownloadStatus<DownloadMessage> downloadStatus = new DownloadStatus<>();
            downloadStatus.setDownload(download);
            downloadStatus.setState(DownloadState.DOWNLOAD_STATE_INIT);
            downloadListener.onDownloadStatusChanged(downloadStatus);
        }
    }

    public void notifyDownloadStart() {
        if (downloadListener != null) {
            DownloadStatus<DownloadMessage> downloadStatus = new DownloadStatus<>();
            downloadStatus.setDownload(download);
            downloadStatus.setState(DownloadState.DOWNLOAD_STATE_START);
            downloadListener.onDownloadStatusChanged(downloadStatus);
        }
    }

    public void notifyDownloadProgress(DownloadProgress progress) {
        if (downloadListener != null) {
            DownloadStatus<DownloadProgress> downloadStatus = new DownloadStatus<>();
            downloadStatus.setDownload(download);
            downloadStatus.setData(progress);
            downloadStatus.setState(DownloadState.DOWNLOAD_STATE_PROGRESS);
            downloadListener.onDownloadProgressChanged(downloadStatus);
        }
    }
}
