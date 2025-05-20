//package eskit.sdk.core.ui.result;
//
//import android.app.Activity;
//import android.os.Build;
//import android.support.v4.app.ActivityCompat;
//import android.util.Pair;
//
//import androidx.activity.ComponentActivity;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//
//import com.sunrain.toolkit.utils.log.L;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import eskit.sdk.core.internal.EsViewManager;
//import eskit.sdk.core.internal.EsContext;
//import com.quicktvui.sdk.base.EsCallback;
//
///**
// * 权限申请
// * <p>
// * Create by weipeng on 2022/09/09 10:01
// */
//public class PermissionActivityResultHandler implements ActivityResultCallback<Boolean> {
//
//    private final ActivityResultLauncher<String> mRequestPermissionResult;
//    private Requester mRequester;
//
//    public PermissionActivityResultHandler(ComponentActivity activity) {
//        ActivityCompat.setPermissionCompatDelegate();
//        mRequestPermissionResult = activity.registerForActivityResult(
//                new ActivityResultContracts.RequestPermission(), this);
//    }
//
//    @Override
//    public void onActivityResult(Boolean isGranted) {
//        if (L.DEBUG)
//            L.logD("requestPermission permissions: " + isGranted);
//        if (mRequester == null) return;
//        mRequester.onActivityResult(isGranted);
//    }
//
//    public void requestPermission(String[] permissions, EsCallback<List<String>, Pair<List<String>, List<String>>> callback) {
//        if (permissions == null || permissions.length == 0) {
//            callback.onSuccess(Collections.emptyList());
//            return;
//        }
//        mRequester = new Requester(mRequestPermissionResult, permissions, callback);
//        mRequester.requestNext();
//    }
//
//    private static final class Requester {
//
//        private ActivityResultLauncher<String> permissionResult;
//        private List<String> permissions;
//        private EsCallback<List<String>, Pair<List<String>, List<String>>> callback;
//
//        private List<String> granted;
//        private List<String> never;
//        private List<String> denied;
//
//        private String currentRequest;
//
//        public Requester(ActivityResultLauncher<String> permissionResult, String[] permissions, EsCallback<List<String>, Pair<List<String>, List<String>>> callback) {
//            this.permissionResult = permissionResult;
//            this.permissions = new ArrayList<>(Arrays.asList(permissions));
//            this.callback = callback;
//
//            this.granted = new ArrayList<>(permissions.length);
//            this.never = new ArrayList<>(permissions.length);
//            this.denied = new ArrayList<>(permissions.length);
//        }
//
//        private void requestNext() {
//            if (permissions == null || permissions.size() == 0) {
//                try {
//                    if (this.denied.size() != 0 || this.never.size() != 0) {
//                        callback.onFailed(new Pair<>(never, denied));
//                    } else {
//                        callback.onSuccess(granted);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                release();
//                return;
//            }
//            currentRequest = permissions.get(0);
//            permissions.remove(0);
//            permissionResult.launch(currentRequest);
//        }
//
//        public void onActivityResult(Boolean isGranted) {
//            if (isGranted) {
//                this.granted.add(currentRequest);
//            } else {
//                this.denied.add(currentRequest);
//                EsViewManager vm = EsViewManager.get();
//                if (vm != null) {
//                    Activity activity = vm.getTopActivity();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (activity != null && !activity.shouldShowRequestPermissionRationale(currentRequest)) {
//                            never.add(currentRequest);
//                            this.denied.remove(currentRequest);
//                        }
//                    }
//                }
//            }
//            permissions.remove(currentRequest);
//            requestNext();
//        }
//
//        private void release() {
//            permissions.clear();
//            permissions = null;
//            permissionResult = null;
//            callback = null;
//            granted.clear();
//            granted = null;
//            denied.clear();
//            denied = null;
//            never.clear();
//            never = null;
//            currentRequest = null;
//        }
//    }
//}
