package quicktvui.support.lottie;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import java.io.File;

import quicktvui.support.lottie.network.DefaultLottieNetworkFetcher;
import quicktvui.support.lottie.network.LottieNetworkCacheProvider;
import quicktvui.support.lottie.network.LottieNetworkFetcher;
import quicktvui.support.lottie.network.NetworkCache;
import quicktvui.support.lottie.network.NetworkFetcher;
import quicktvui.support.lottie.utils.LottieTrace;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class L {

  public static boolean DBG = false;
  public static final String TAG = "LOTTIE";

  private static boolean traceEnabled = false;
  private static boolean networkCacheEnabled = true;
  private static boolean disablePathInterpolatorCache = true;

  private static LottieNetworkFetcher fetcher;
  private static LottieNetworkCacheProvider cacheProvider;

  private static volatile NetworkFetcher networkFetcher;
  private static volatile NetworkCache networkCache;
  private static ThreadLocal<LottieTrace> lottieTrace;

  private L() {
  }

  public static void setTraceEnabled(boolean enabled) {
    if (traceEnabled == enabled) {
      return;
    }
    traceEnabled = enabled;
    if (traceEnabled && lottieTrace == null) {
      lottieTrace = new ThreadLocal<>();
    }
  }

  public static void setNetworkCacheEnabled(boolean enabled) {
    networkCacheEnabled = enabled;
  }

  public static void beginSection(String section) {
    if (!traceEnabled) {
      return;
    }
    getTrace().beginSection(section);
  }

  public static float endSection(String section) {
    if (!traceEnabled) {
      return 0;
    }
    return getTrace().endSection(section);
  }

  private static LottieTrace getTrace() {
    LottieTrace trace = lottieTrace.get();
    if (trace == null) {
      trace = new LottieTrace();
      lottieTrace.set(trace);
    }
    return trace;
  }

  public static void setFetcher(LottieNetworkFetcher customFetcher) {
    fetcher = customFetcher;
  }

  public static void setCacheProvider(LottieNetworkCacheProvider customProvider) {
    cacheProvider = customProvider;
  }

  @NonNull
  public static NetworkFetcher networkFetcher(@NonNull Context context) {
    NetworkFetcher local = networkFetcher;
    if (local == null) {
      synchronized (NetworkFetcher.class) {
        local = networkFetcher;
        if (local == null) {
          networkFetcher = local = new NetworkFetcher(networkCache(context), fetcher != null ? fetcher : new DefaultLottieNetworkFetcher());
        }
      }
    }
    return local;
  }

  @Nullable
  public static NetworkCache networkCache(@NonNull final Context context) {
    if (!networkCacheEnabled) {
      return null;
    }
    final Context appContext = context.getApplicationContext();
    NetworkCache local = networkCache;
    if (local == null) {
      synchronized (NetworkCache.class) {
        local = networkCache;
        if (local == null) {
          networkCache = local = new NetworkCache(cacheProvider != null ? cacheProvider :
              () -> new File(appContext.getCacheDir(), "lottie_network_cache"));
        }
      }
    }
    return local;
  }

  public static void setDisablePathInterpolatorCache(boolean disablePathInterpolatorCache) {
    L.disablePathInterpolatorCache = disablePathInterpolatorCache;
  }

  public static boolean getDisablePathInterpolatorCache() {
    return disablePathInterpolatorCache;
  }
}
