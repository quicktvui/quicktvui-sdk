package quicktvui.support.lottie.network;


import android.support.annotation.NonNull;

import java.io.File;

/**
 * Interface for providing the custom cache directory where animations downloaded via url are saved.
 *
 *
 */
public interface LottieNetworkCacheProvider {

  /**
   * Called during cache operations
   *
   * @return cache directory
   */
  @NonNull File getCacheDir();
}