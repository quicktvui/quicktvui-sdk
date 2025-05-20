package quicktvui.support.ui.icon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.quicktvui.sdk.base.component.IEsComponentView;


/**
 *
 */
public class ESAppIconView extends ImageView implements IEsComponentView {

    private int roundingRadius = 0;

    public ESAppIconView(Context context) {
        super(context);
    }

    public void setRoundingRadius(int roundingRadius) {
        this.roundingRadius = roundingRadius;
    }

    public void setAppIconDrawable(Drawable drawable) {
        if (roundingRadius > 0) {
            RoundedCorners roundedCorners = new RoundedCorners(roundingRadius);
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
            Glide.with(this)
                    .load(drawable)
                    .apply(options)
                    .into(this);
        }
        //
        else {
            setImageDrawable(drawable);
        }
    }
}
