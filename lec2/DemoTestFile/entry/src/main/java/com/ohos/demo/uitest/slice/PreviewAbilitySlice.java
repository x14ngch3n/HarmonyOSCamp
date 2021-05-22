package com.ohos.demo.uitest.slice;

import com.ohos.demo.uitest.ResourceTable;
import com.ohos.demo.uitest.Util;
import com.ohos.demo.uitest.constants.Constants;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.app.Environment;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.PixelMap;

import java.io.File;

public class PreviewAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, 0, "PreviewAbilitySlice");
    private static final String TAG = PreviewAbilitySlice.class.getName();

    private Image mPreviewImg;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_preview);
        mPreviewImg = (Image) findComponentById(ResourceTable.Id_preview);
        loadPreview();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void loadPreview() {
        String sdcardPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator + Constants.FILE_NAME;;
        HiLog.info(LABEL_LOG, "updatePreview from " + sdcardPath + ",");
        PixelMap pixelMap = Util.readDataFromFile(sdcardPath);
        getUITaskDispatcher().asyncDispatch(() -> {
            mPreviewImg.setPixelMap(pixelMap);
        });
    }
}
