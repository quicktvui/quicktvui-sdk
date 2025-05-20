package com.quicktvui.sdk.core.assets;

import static com.quicktvui.sdk.core.internal.Constants.ERR_DOWNLOAD;
import static com.quicktvui.sdk.core.utils.CommonUtils.checkIsSafeFile;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sunrain.toolkit.utils.EncryptUtils;
import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.ShellUtils;
import com.sunrain.toolkit.utils.Utils;
import com.sunrain.toolkit.utils.ZipUtils;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.net.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.utils.HttpRequestUtils;
import com.quicktvui.sdk.base.EsException;

/**
 * <br>
 * 文件下载管理
 * <br>
 * <br>
 * Created by WeiPeng on 2023-09-21 17:51
 */
public class AssetBundleManager {

    public static final String LOAD_PREFIX_FILE = "file://";
    public static final String LOAD_PREFIX_ASSETS = "assets://";
    public static final String LOAD_PREFIX_HTTP = "http";

    private static final byte TYPE_RPK = 0;
    private static final byte TYPE_CARD = 1;
    private static final byte TYPE_SO = 2;
    private static final byte TYPE_PLG = 3;
    private static final byte TYPE_RUNTIME = 4;

    //region App

    public static File loadAppAsync(AssetData data) throws Exception {
        return loadAppAsync(data, null);
    }

    public static File loadAppAsync(AssetData data, AssetLoadCallback callback) throws Exception {
        return loadAssetAsync(TYPE_RPK, Constants.getEsAppDir(), data, callback);
    }

    //endregion

    //region Card

    public static File loadCardAsync(AssetData data) throws Exception {
        return loadCardAsync(data, null);
    }

    public static File loadCardAsync(AssetData data, AssetLoadCallback callback) throws Exception {
        return loadAssetAsync(TYPE_CARD, Constants.getEsCardDir(), data, callback);
    }

    //endregion

    //region So

    public static File loadSoAsync(AssetData data) throws Exception {
        return loadSoAsync(data, null);
    }

    public static File loadSoAsync(AssetData data, AssetLoadCallback callback) throws Exception {
        return loadAssetAsync(TYPE_SO, Constants.getEsSoDir(), data, callback);
    }

    //endregion

    //region Plugin

    public static File loadPluginAsync(AssetData data) throws Exception {
        return loadPluginAsync(data, null);
    }

    public static File loadPluginAsync(AssetData data, AssetLoadCallback callback) throws Exception {
        return loadAssetAsync(TYPE_PLG, Constants.getPluginDir(), data, callback);
    }

    //endregion

    //region runtime

    public static File loadRuntimeAsync(AssetData data) throws Exception {
        return loadRuntimeAsync(data, null);
    }

    public static File loadRuntimeAsync(AssetData data, AssetLoadCallback callback) throws Exception {
        return loadAssetAsync(TYPE_RUNTIME, Constants.getRuntimeDir(), data, callback);
    }

    //endregion

    private synchronized static File loadAssetAsync(byte type, File dir, AssetData data, AssetLoadCallback callback) throws Exception {
        File cacheDir = Constants.getCacheDir();
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        File cacheFile = new File(cacheDir, String.valueOf(data.url.hashCode()));
        String path = data.version != 0 ?
                String.format(Locale.ENGLISH, "%s/%.1f", data.id, data.version)
                : String.format(Locale.ENGLISH, "%s/%s", data.id, data.versionStr);

        // app_rpk/apps/es.hello.world/1.0.0
        File versionDir = new File(dir, path);
        // app_rpk/apps/es.hello.world/.md5
        File md5Dir = new File(versionDir.getParentFile(), Constants.PATH_RPK_MD5);
        // app_rpk/apps/es.hello.world/1.0.0 或者
        // app_rpk/apps/es.hello.world/1.0.0/android
        File codeDir = type == TYPE_RPK || type == TYPE_CARD ? new File(versionDir, Constants.PATH_RPK_CODE) : versionDir;

        // bugfix 2.5版本缓存过so后, >= 2.6的版本会再次更新so
        // bugfix 2.5 /data/data/com.extscreen.runtime/app_rpk/libs/eskit.so.hp.v1/arm64/v1
        // >=
        // bugfix 2.6 /data/data/com.extscreen.runtime/app_rpk/libs/eskit.so.hp.v1/1.0
        if(type == TYPE_SO && !TextUtils.isEmpty(data.abi)) {
            // 检测历史版本是否存在
            File historyVersion = new File(dir, String.format(Locale.ENGLISH, "%s/%s/v%d", data.id, data.abi, (int) data.version));
            if(historyVersion.exists()) return historyVersion;
        }

        if (cacheFile.exists()) { // 文件存在说明上次没下载完或解压失败
            L.logIF("delete last");
            FileUtils.delete(versionDir);
            L.logIF("delete cache");
            FileUtils.delete(cacheFile);
        }

        if (isCacheValidate(type, codeDir, md5Dir, data.md5)) {
            return codeDir;
        } else {
            L.logWF("cache version invalidate");
            FileUtils.delete(md5Dir);
            FileUtils.delete(codeDir);
        }

        File parentFile = versionDir.getParentFile();
        if (data.autoRemove && parentFile != null && parentFile.exists()) {
            deleteAllOldVersions(parentFile.listFiles());
        }
        versionDir.mkdirs();
        versionDir.setLastModified(System.currentTimeMillis());
        data.url = postBeforeDownload(callback, data.url);
        if (data.url.startsWith(LOAD_PREFIX_HTTP)) {
            downloadFileFromInternet(data.url, cacheFile, callback);
            L.logIF("download success");
        } else {
            File tmpFile = new File(cacheFile.getParent(), cacheFile.getName() + ".part");
            FileUtils.delete(tmpFile);
            if (data.url.startsWith(LOAD_PREFIX_FILE)) {
                String filePath = data.url.substring(7);
                ShellUtils.CommandResult result = ShellUtils.execCmd("chmod 777 " + filePath, false);
                if (result.result != 0) {
                    L.logEF(result.toString());
                }
                try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                    copyFileWithProgress(inputStream, tmpFile, callback);
                }
            } else if (data.url.startsWith(LOAD_PREFIX_ASSETS)) {
                String filePath = data.url.substring(9);

                try (InputStream inputStream = Utils.getApp().getAssets().open(filePath)) {
                    copyFileWithProgress(inputStream, tmpFile, callback);
                }
            }
            FileUtils.rename(tmpFile, cacheFile.getName());
            L.logIF("copy success");
        }
        cacheFile = postAfterDownload(callback, cacheFile);
        if (!isMatchMd5(cacheFile, data.md5)) {
            FileUtils.delete(cacheFile);
            throw new EsException(Constants.ERR_MD5, "md5 mismatch");
        }
        File unzipDir = tryFixTargetDir(type, cacheFile, codeDir);
        ZipUtils.unzipFile(cacheFile, unzipDir);
        L.logIF("unzip success");
        String fileMd5 = TextUtils.isEmpty(data.md5) ? EncryptUtils.encryptMD5File2String(cacheFile).toLowerCase() : data.md5;
        FileUtils.delete(cacheFile);
        FileUtils.createOrExistsDir(md5Dir);
        FileUtils.createOrExistsFile(new File(md5Dir, fileMd5));
        return codeDir;
    }

    /**
     * 下载rpk （ 类型：File:// 和 assets:// ） 进度回调
     */
    public static void copyFileWithProgress(InputStream inputStream, File outputFile, AssetLoadCallback callback) throws IOException {
        long totalBytes = inputStream.available();
        long copiedBytes = 0;
        byte[] buffer = new byte[4096];
        int bytesRead;

        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                copiedBytes += bytesRead;
                int progress = (int) ((copiedBytes * 100) / totalBytes);
                postDownloadProgress(callback, progress);
            }
        }
    }

    private static File tryFixTargetDir(byte type, File cacheDir, File codeDir) {
        if (type == TYPE_RPK || type == TYPE_CARD) {
            try (ZipFile zipFile = new ZipFile(cacheDir)) {
                ZipEntry indexEntry = zipFile.getEntry(Constants.FILE_JS_INDEX);
                if (indexEntry == null) { // 说明有android文件夹
                    return codeDir.getParentFile();
                }
            } catch (Exception e) {
                L.logEF("check rpk dir", e);
            }
        }
        return codeDir;
    }

    /**
     * 判断缓存是否有效
     **/
    private static boolean isCacheValidate(int type, File targetDir, File md5Dir, String md5) {
        // 1. 宽松验证
        // 对于没强md5需求的应用，有文件即可
        //
        // 2. 严格验证
        // 有md5强需求的应用，需要判断上次的md5

        boolean isCheckLoose = false; // 宽松验证
        if (targetDir.exists()) {
            if (type == TYPE_RPK || type == TYPE_CARD) {
                isCheckLoose = new File(targetDir, Constants.FILE_JS_INDEX).exists() ||
                        new File(targetDir, Constants.PATH_RPK_CODE + File.separator + Constants.FILE_JS_INDEX).exists();
            } else {
                File[] _tmp;
                isCheckLoose = (_tmp = targetDir.listFiles()) != null && _tmp.length > 0;
            }
        }

        if (isCheckLoose && !TextUtils.isEmpty(md5)) { // 严格验证
            boolean isCheckStrict = false;
            if (md5Dir.exists() && md5Dir.isDirectory()) {
                File[] guessMd5Files = md5Dir.listFiles();
                if (guessMd5Files != null && guessMd5Files.length > 0) {
                    String lastMd5 = guessMd5Files[0].getName();
                    if (Objects.equals(lastMd5, md5)) {
                        isCheckStrict = true;
                    }
                }
            }
            return isCheckStrict;
        }

        return isCheckLoose;
    }

    /**
     * 删除旧版本
     **/
    private static void deleteAllOldVersions(@Nullable File[] files) {
        if (files == null || files.length == 0) return;
        for (File file : files) {
            if (checkIsSafeFile(file)) continue;
            L.logIF("delete old ver");
            FileUtils.delete(file);
        }
    }

    private static boolean isMatchMd5(File file, String md5) {
        if (!file.exists()) return false;
        if (TextUtils.isEmpty(md5)) return true;
        return md5.equalsIgnoreCase(EncryptUtils.encryptMD5File2String(file));
    }

    /**
     * 下载
     **/
    public static void downloadFileFromInternet(String url, File outFile, AssetLoadCallback callback) {
        HttpRequest req = HttpRequestUtils.wrapper(HttpRequest.get(url));
        int code = req.code();
        L.logIF("code: " + code);
        if (code != HttpURLConnection.HTTP_OK) {
            L.logIF("" + url);
            throw new EsException(ERR_DOWNLOAD, code + " " + req.message()).setReasonCode(code);
        }

        File tmpDownloadFile = new File(outFile.getParent(), outFile.getName() + ".part");
        FileUtils.delete(tmpDownloadFile);

        InputStream inputStream = req.buffer();
        long totalBytes = req.contentLength(); // 获取文件的总大小
        long downloadedBytes = 0;
        byte[] buffer = new byte[4096];
        int bytesRead;

        try (FileOutputStream fos = new FileOutputStream(tmpDownloadFile)) {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                downloadedBytes += bytesRead;
                // 计算进度并输出
                int progress = (int) ((downloadedBytes * 100) / totalBytes);
                postDownloadProgress(callback, progress);
            }
        } catch (IOException e) {
            throw new EsException(ERR_DOWNLOAD, "save archive failed", null);
        }

        FileUtils.rename(tmpDownloadFile, outFile.getName());
    }

    private static String postBeforeDownload(AssetLoadCallback callback, String url){
        if (callback != null) {
            return callback.beforeDownload(url);
        }
        return url;
    }

    private static File postAfterDownload(AssetLoadCallback callback, File file){
        if (callback != null) {
            return callback.afterDownload(file);
        }
        return file;
    }

    private static void postDownloadProgress(AssetLoadCallback callback, int progress){
        if (callback != null) {
            callback.downloading(progress);
        }
    }

}
