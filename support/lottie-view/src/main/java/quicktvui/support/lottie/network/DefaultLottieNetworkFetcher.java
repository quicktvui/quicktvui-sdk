package quicktvui.support.lottie.network;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DefaultLottieNetworkFetcher implements LottieNetworkFetcher {

  @Override
  @NonNull
  public LottieFetchResult fetchSync(@NonNull String url) throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod("GET");
    connection.connect();
    return new DefaultLottieFetchResult(connection);
  }
}
