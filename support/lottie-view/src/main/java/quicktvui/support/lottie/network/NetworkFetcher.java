package quicktvui.support.lottie.network;

import android.content.Context;
import android.util.Pair;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieCompositionFactory;
import quicktvui.support.lottie.LottieResult;
import quicktvui.support.lottie.utils.Logger;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class NetworkFetcher {

  @Nullable
  private final NetworkCache networkCache;
  @NonNull
  private final LottieNetworkFetcher fetcher;

  public NetworkFetcher(@Nullable NetworkCache networkCache, @NonNull LottieNetworkFetcher fetcher) {
    this.networkCache = networkCache;
    this.fetcher = fetcher;
  }

  @NonNull
  @WorkerThread
  public LottieResult<LottieComposition> fetchSync(Context context, @NonNull String url, @Nullable String cacheKey) {
    LottieComposition result = fetchFromCache(context, url, cacheKey);
    if (result != null) {
      return new LottieResult<>(result);
    }

    Logger.debug("Animation for " + url + " not found in cache. Fetching from network.");

    return fetchFromNetwork(context, url, cacheKey);
  }

  @Nullable
  @WorkerThread
  private LottieComposition fetchFromCache(Context context, @NonNull String url, @Nullable String cacheKey) {
    if (cacheKey == null || networkCache == null) {
      return null;
    }
    Pair<FileExtension, InputStream> cacheResult = networkCache.fetch(url);
    if (cacheResult == null) {
      return null;
    }

    FileExtension extension = cacheResult.first;
    InputStream inputStream = cacheResult.second;
    LottieResult<LottieComposition> result;
    if (extension == FileExtension.ZIP) {
      result = LottieCompositionFactory.fromZipStreamSync(context, new ZipInputStream(inputStream), cacheKey);
    } else {
      result = LottieCompositionFactory.fromJsonInputStreamSync(inputStream, cacheKey);
    }
    if (result.getValue() != null) {
      return result.getValue();
    }
    return null;
  }

  @NonNull
  @WorkerThread
  private LottieResult<LottieComposition> fetchFromNetwork(Context context, @NonNull String url, @Nullable String cacheKey) {
    Logger.debug("Fetching " + url);

    LottieFetchResult fetchResult = null;
    try {
      fetchResult = fetcher.fetchSync(url);
      if (fetchResult.isSuccessful()) {
        InputStream inputStream = fetchResult.bodyByteStream();
        String contentType = fetchResult.contentType();
        LottieResult<LottieComposition> result = fromInputStream(context, url, inputStream, contentType, cacheKey);
        Logger.debug("Completed fetch from network. Success: " + (result.getValue() != null));
        return result;
      } else {
        return new LottieResult<>(new IllegalArgumentException(fetchResult.error()));
      }
    } catch (Exception e) {
      return new LottieResult<>(e);
    } finally {
      if (fetchResult != null) {
        try {
          fetchResult.close();
        } catch (IOException e) {
          Logger.warning("LottieFetchResult close failed ", e);
        }
      }
    }
  }

  @NonNull
  private LottieResult<LottieComposition> fromInputStream(Context context, @NonNull String url, @NonNull InputStream inputStream, @Nullable String contentType,
      @Nullable String cacheKey) throws IOException {
    FileExtension extension;
    LottieResult<LottieComposition> result;
    if (contentType == null) {
      // Assume JSON for best effort parsing. If it fails, it will just deliver the parse exception
      // in the result which is more useful than failing here.
      contentType = "application/json";
    }
    if (contentType.contains("application/zip") ||
        contentType.contains("application/x-zip") ||
        contentType.contains("application/x-zip-compressed") ||
        url.split("\\?")[0].endsWith(".lottie")) {
      Logger.debug("Handling zip response.");
      extension = FileExtension.ZIP;
      result = fromZipStream(context, url, inputStream, cacheKey);
    } else {
      Logger.debug("Received json response.");
      extension = FileExtension.JSON;
      result = fromJsonStream(url, inputStream, cacheKey);
    }

    if (cacheKey != null && result.getValue() != null && networkCache != null) {
      networkCache.renameTempFile(url, extension);
    }

    return result;
  }

  @NonNull
  private LottieResult<LottieComposition> fromZipStream(Context context, @NonNull String url, @NonNull InputStream inputStream, @Nullable String cacheKey)
      throws IOException {
    if (cacheKey == null || networkCache == null) {
      return LottieCompositionFactory.fromZipStreamSync(context, new ZipInputStream(inputStream), null);
    }
    File file = networkCache.writeTempCacheFile(url, inputStream, FileExtension.ZIP);
    return LottieCompositionFactory.fromZipStreamSync(context, new ZipInputStream(new FileInputStream(file)), url);
  }

  @NonNull
  private LottieResult<LottieComposition> fromJsonStream(@NonNull String url, @NonNull InputStream inputStream, @Nullable String cacheKey)
      throws IOException {
    if (cacheKey == null || networkCache == null) {
      return LottieCompositionFactory.fromJsonInputStreamSync(inputStream, null);
    }
    File file = networkCache.writeTempCacheFile(url, inputStream, FileExtension.JSON);
    return LottieCompositionFactory.fromJsonInputStreamSync(new FileInputStream(file.getAbsolutePath()), url);
  }
}
