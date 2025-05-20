package quicktvui.support.ui.icon;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;


/**
 *
 */
@ESKitAutoRegister
public class ESAppIconViewComponent implements IEsComponent<ESAppIconView> {

    protected static final String OP_SET_ROUND_RADIUS = "setRoundingRadius";
    protected static final String OP_SET_PACKAGE_NAME = "setPackageName";

    private PackageManager packageManager;


    @Override
    public ESAppIconView createView(Context context, EsMap initParams) {
        packageManager = context.getPackageManager();
        return new ESAppIconView(context);
    }

    //0.
    @EsComponentAttribute
    public void init(final ESAppIconView appIconView, EsMap esMap) {
        String packageName = esMap.getString("packageName");
        int roundingRadius = esMap.getInt("roundingRadius");
        if (roundingRadius < 0 || TextUtils.isEmpty(packageName)) {
            return;
        }
        roundingRadius(appIconView, roundingRadius);
        packageName(appIconView, packageName);
    }

    //1.
    @EsComponentAttribute
    public void roundingRadius(final ESAppIconView appIconView, int roundingRadius) {
        if (roundingRadius < 0) {
            return;
        }
        appIconView.setRoundingRadius(roundingRadius);
    }

    //2.
    @EsComponentAttribute
    public void packageName(final ESAppIconView appIconView, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            Drawable icon = packageManager.getApplicationIcon(applicationInfo);
            if (icon != null) {
                appIconView.setAppIconDrawable(icon);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatchFunction(ESAppIconView view, String functionName, EsArray params, EsPromise esPromise) {

        switch (functionName) {
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                esPromise.resolve(map);
                break;
            case OP_SET_ROUND_RADIUS:
                try {
                    if (view != null) {
                        int roundingRadius = params.getInt(0);
                        view.setRoundingRadius(roundingRadius);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PACKAGE_NAME:
                try {
                    if (view != null) {
                        String packageName = params.getString(0);
                        packageName(view, packageName);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void destroy(ESAppIconView appIconView) {
        appIconView.setImageDrawable(null);
    }
}
