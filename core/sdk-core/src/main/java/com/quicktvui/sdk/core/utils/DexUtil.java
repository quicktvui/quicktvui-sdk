package com.quicktvui.sdk.core.utils;

import android.content.Context;
import android.os.Build;

import com.sunrain.toolkit.utils.ReflectUtils;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dalvik.system.DexFile;

/**
 *
 */
public class DexUtil {

    public static void insertLibDir(Context context, File nativeLibsDir) {
        try {
            Comparator<File> nativeLibraryDirectoriesSort = (file, t1) -> file.getAbsolutePath().equals(nativeLibsDir.getAbsolutePath()) ? -1 :1;

            ClassLoader baseClassLoader = context.getClassLoader();
            if (L.DEBUG) L.logD("baseClassLoader: " + baseClassLoader);
            Object basePathList = ReflectUtils.reflect(baseClassLoader).field("pathList").get();
            if (L.DEBUG) L.logD("pathList:" + basePathList);

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1){
                File[] nativeLibraryDirectories = ReflectUtils.reflect(basePathList).field("nativeLibraryDirectories").get();
                final int N = nativeLibraryDirectories.length;
                File[] newNativeLibraryDirectories = new File[N + 1];
                System.arraycopy(nativeLibraryDirectories, 0, newNativeLibraryDirectories, 0, N);
                newNativeLibraryDirectories[N] = nativeLibsDir;
                Arrays.sort(newNativeLibraryDirectories, nativeLibraryDirectoriesSort);
                ReflectUtils.reflect(basePathList).field("nativeLibraryDirectories", newNativeLibraryDirectories);
            }else{
                List<File> nativeLibraryDirectories = ReflectUtils.reflect(basePathList).field("nativeLibraryDirectories").get();
                if(nativeLibraryDirectories != null){
                    for (File path : nativeLibraryDirectories) {
                        if(path.getAbsolutePath().equals(nativeLibsDir.getAbsolutePath())) return;
                    }
                }
                nativeLibraryDirectories.add(nativeLibsDir);
                Collections.sort(nativeLibraryDirectories, nativeLibraryDirectoriesSort);

                Object baseNativeLibraryPathElements = ReflectUtils.reflect(basePathList).field("nativeLibraryPathElements").get();
                final int baseArrayLength = Array.getLength(baseNativeLibraryPathElements);

                Class<?> elementClass = baseNativeLibraryPathElements.getClass().getComponentType();
                Object allNativeLibraryPathElements = Array.newInstance(elementClass, baseArrayLength + 1);

                Object element;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    element = elementClass.getConstructor(File.class).newInstance(nativeLibsDir);
                } else {
                    element = elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class).newInstance(nativeLibsDir, true, null, null);
                }

                Array.set(allNativeLibraryPathElements, 0, element);
                System.arraycopy(baseNativeLibraryPathElements, 0, allNativeLibraryPathElements, 1, baseArrayLength);

                ReflectUtils.reflect(basePathList).field("nativeLibraryPathElements", allNativeLibraryPathElements);
            }

            if (L.DEBUG) L.logD("baseClassLoader new : " + baseClassLoader);

        } catch (Exception e) {
            L.logW("insert so", e);
        }
    }
}
