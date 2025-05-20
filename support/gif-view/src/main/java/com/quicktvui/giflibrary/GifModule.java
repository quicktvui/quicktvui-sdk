//package tv.huan.giflibrary;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.rastermill.FrameSequence;
//import android.support.rastermill.FrameSequenceDrawable;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.Registry;
//import com.bumptech.glide.annotation.GlideModule;
//import com.bumptech.glide.module.AppGlideModule;
//
//import java.io.InputStream;
//
//@GlideModule
//public class GifModule extends AppGlideModule {
//    public GifModule() {
//    }
//
//    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
//        super.registerComponents(context, glide, registry);
//        registry.register(FrameSequence.class, FrameSequenceDrawable.class, new GifDrawableTranscoder()).append(InputStream.class, FrameSequence.class, new GifDecoder());
//    }
//
//    public boolean isManifestParsingEnabled() {
//        return false;
//    }
//}