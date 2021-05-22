package com.ohos.demo.uitest.slice;

import com.ohos.demo.uitest.ResourceTable;
import com.ohos.demo.uitest.Util;
import com.ohos.demo.uitest.constants.Constants;
import com.ohos.demo.uitest.proxy.ConnectManager;
import com.ohos.demo.uitest.proxy.ConnectManagerIml;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.data.distributed.common.KvManagerConfig;
import ohos.data.distributed.common.KvManagerFactory;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.PixelMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ControlAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, 0, "ControlAbilitySlice");
    private Image mPreviewImg;

    private Component okButton;

    private Component leftButton;

    private Component rightButton;

    private Component upButton;

    private Component downButton;

    private Component refreshButton;

    private ConnectManager mConnectManager;

    private String mSrcDeviceId;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_control);
        mSrcDeviceId = intent.getStringParam("localDeviceId");
        initView();
        initConnection();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        okButton = findComponentById(ResourceTable.Id_ok_button);
        leftButton = findComponentById(ResourceTable.Id_left_button);
        rightButton = findComponentById(ResourceTable.Id_right_button);
        upButton = findComponentById(ResourceTable.Id_up_button);
        downButton = findComponentById(ResourceTable.Id_down_button);
        refreshButton = findComponentById(ResourceTable.Id_refresh);
        mPreviewImg = (Image) findComponentById(ResourceTable.Id_preview);
        okButton.setClickedListener(component -> {
            sendSaveRequest();
        });
        leftButton.setClickedListener(component -> {
            // 点击左键按钮
            sendMoveRequest(Constants.MOVE_LEFT);
        });
        rightButton.setClickedListener(component -> {
            // 点击右键按钮
            sendMoveRequest(Constants.MOVE_RIGHT);
        });
        upButton.setClickedListener(component -> {
            // 点击向上按钮
            sendMoveRequest(Constants.MOVE_UP);
        });
        downButton.setClickedListener(component -> {
            // 点击向下按钮
            sendMoveRequest(Constants.MOVE_DOWN);
        });
        refreshButton.setClickedListener(component -> {
            getContext().getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                    .asyncDispatch(() -> startPreview());
        });
    }

    private void initConnection() {
        mConnectManager = ConnectManagerIml.getInstance();
        mConnectManager.connectPa(this, mSrcDeviceId);
    }

    private void sendMoveRequest(int direction) {
        Map<String, Integer> map = new HashMap<>();
        map.put(Constants.DATA_KEY, direction);
        mConnectManager.sendRequest(Constants.REQUEST_MOVE, map);
    }

    private void sendSaveRequest() {
        HiLog.info(LABEL_LOG, "sendSaveRequest.");
        Map<String, Integer> map = new HashMap<>();
        mConnectManager.sendRequest(Constants.REQUEST_SAVE, map);
    }

    private void updatePreview() {
        HiLog.info(LABEL_LOG, "refreshPreview ");
        File distDir = Util.getDisFile(getContext());
        String filePath = distDir + File.separator + Constants.FILE_NAME;
        HiLog.info(LABEL_LOG, "updatePreview from " + filePath + ",");
        PixelMap pixelMap = Util.readDataFromFile(filePath);
        getUITaskDispatcher().asyncDispatch(() -> {
            mPreviewImg.setPixelMap(pixelMap);
        });
    }

    private void startPreview() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(mSrcDeviceId)
                .withBundleName(getBundleName())
                .withAbilityName("com.ohos.demo.uitest.PreviewAbility")
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }
}