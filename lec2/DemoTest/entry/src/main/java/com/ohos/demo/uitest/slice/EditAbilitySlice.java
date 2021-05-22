package com.ohos.demo.uitest.slice;

import com.ohos.demo.uitest.PreferencesHelper;
import com.ohos.demo.uitest.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.*;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;

import java.util.List;

public class EditAbilitySlice extends AbilitySlice implements IAbilityContinuation {
    Text mTitle;
    TextField mDetail;
    Button mContinue;
    String mCacheKey;
    String mCacheContent;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_edit);
        mTitle = (Text) findComponentById(ResourceTable.Id_title);
        mDetail = (TextField) findComponentById(ResourceTable.Id_detail);
        Button save = (Button) findComponentById(ResourceTable.Id_save);
        save.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                saveRecord();
            }
        });
        mContinue = (Button) findComponentById(ResourceTable.Id_move);
        mContinue.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                checkDevice();
            }
        });
        if (intent != null) {
            Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
            String key;
            String content;
            if (mCacheKey != null) {
                key = mCacheKey;
                content = mCacheContent;
            } else {
                key = intent.getStringParam("key");
                if (key == null || "".equals(key)) {
                    key = "Title"+preferences.getAll().size();
                    if (preferences.hasKey(key)) {
                        key = "Title"+preferences.getAll().size()+"_copy";
                    }
                }
                content = preferences.getString(key, "");
            }
            mTitle.setText(key);
            mDetail.setText(content);
        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void saveRecord() {
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
        preferences.putString(mTitle.getText(), mDetail.getText());
        preferences.flushSync();
        terminateAbility();
    }

    private void checkDevice() {
        // 通过FLAG_GET_ONLINE_DEVICE标记获得在线设备列表
        List<DeviceInfo> deviceInfoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        if (deviceInfoList.size() < 1) {
            showTip(this, "无在网设备");
        } else {
            showDeviceChooser(deviceInfoList);
        }
    }

    private static void showTip(Context context, String text) {
        ToastDialog toastDialog = new ToastDialog(context);
        toastDialog.setText(text);
        toastDialog.show();
    }

    private void showDeviceChooser(List<DeviceInfo> deviceInfoList) {
        ListDialog dialog = new ListDialog(this);
        String[] names = new String[deviceInfoList.size()];
        for(int i = 0; i<deviceInfoList.size();i++) {
            names[i] = deviceInfoList.get(i).getDeviceName();
        }
        dialog.setItems(names);
        dialog.setOnSingleSelectListener(new IDialog.ClickedListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                DeviceInfo info = deviceInfoList.get(i);
                try {
                    // 开始任务迁移
                    continueAbility();
                } catch (IllegalStateException | UnsupportedOperationException e) {
                }
                dialog.hide();
            }
        });
        // 点击后迁移到指定设备
        dialog.setListener(new ListContainer.ItemClickedListener() {
            @Override
            public void onItemClicked(ListContainer listContainer, Component component, int i, long l) {
                DeviceInfo info = deviceInfoList.get(i);
                try {
                    // 开始任务迁移
                    continueAbility();
                } catch (IllegalStateException | UnsupportedOperationException e) {
                }
                dialog.hide();
            }
        }, null , null);
        dialog.show();
    }


    @Override
    public boolean onStartContinuation() {
        return true;
    }

    @Override
    public boolean onSaveData(IntentParams intentParams) {
        intentParams.setParam("title", mTitle.getText());
        intentParams.setParam("content", mDetail.getText());
        return true;
    }

    @Override
    public boolean onRestoreData(IntentParams intentParams) {
        mCacheKey = getIntentString(intentParams, "title");
        mCacheContent = getIntentString(intentParams, "content");
        return true;
    }

    private String getIntentString(IntentParams intentParams, String key) {
        Object value = intentParams.getParam(key);
        if ((value != null) && (value instanceof String)) {
            return (String) value;
        }
        return null;
    }

    @Override
    public void onCompleteContinuation(int i) {
        terminateAbility();
    }
}
