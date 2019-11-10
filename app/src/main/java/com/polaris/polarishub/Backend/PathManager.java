package com.polaris.polarishub.Backend;

import android.appwidget.AppWidgetManager;
import android.os.Environment;
import android.os.FileUtils;

import com.polaris.polarishub.MainActivity;
import com.yanzhenjie.andserver.util.IOUtils;

import java.io.File;

public class PathManager {

    private static PathManager sInstance;

    public static PathManager getInstance() {
        if(sInstance == null) {
            synchronized (PathManager.class) {
                if(sInstance == null) {
                    sInstance = new PathManager();
                }
            }
        }
        return sInstance;
    }

    private File mRootDir;

    private PathManager() {
      mRootDir = Environment.getExternalStorageDirectory();
      mRootDir = new File(mRootDir, "AndServer");
        IOUtils.createFolder(mRootDir);
    }

    public String getRootDir() {
        return mRootDir.getAbsolutePath();
    }

    public String getWebDir() {
        return new File(mRootDir, "web").getAbsolutePath();
    }
}