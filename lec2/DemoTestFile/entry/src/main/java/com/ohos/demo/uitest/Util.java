/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.ohos.demo.uitest;

import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {
    private static final String TAG = "Util";
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, 0, "Util");

    private static Context saveContext;

    public static void setSaveContext(Context context) {
        saveContext =  context;
    }

    public static void writDataToFile(PixelMap pixelMap, String fileName) {
        ImagePacker imagePacker = ImagePacker.create();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
            ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
            packingOptions.format = "image/jpeg";
            packingOptions.quality = 90;
            boolean result = imagePacker.initializePacking(outputStream, packingOptions);
            result = imagePacker.addImage(pixelMap);
            long dataSize = imagePacker.finalizePacking();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static PixelMap readDataFromFile(String fileName) {
        String pathName = fileName;
        if (pathName == null) {
            return null;
        }
        PixelMap pixelMapNoOptions = null;
        try {
            ImageSource imageSource = ImageSource.create(pathName, null);
            pixelMapNoOptions = imageSource.createPixelmap(null);
        } catch (Exception e) {
            HiLog.error(LABEL_LOG, "readDataFromFile Exception "+e.toString());
        }
        return pixelMapNoOptions;
    }

    public static File getDisFile(Context context) {
        Context disContext = saveContext == null ? context :saveContext;
        File distributedDir = disContext.getDistributedDir();
        if (distributedDir == null) {
            HiLog.error(LABEL_LOG, "getDisFile error");
            return null;
        }
        File dir = distributedDir;
        HiLog.info(LABEL_LOG, "getDisFile =" + dir + ", saveContext="+saveContext+",Context="+context);
        return dir;
    }

    public static void showTip(Context context, String text) {
        ToastDialog toastDialog = new ToastDialog(context);
        toastDialog.setText(text);
        toastDialog.show();
    }
}
