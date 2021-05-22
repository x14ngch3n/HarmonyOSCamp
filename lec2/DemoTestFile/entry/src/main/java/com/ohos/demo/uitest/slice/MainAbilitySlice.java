package com.ohos.demo.uitest.slice;

import com.ohos.demo.uitest.Util;
import com.ohos.demo.uitest.ResourceTable;
import com.ohos.demo.uitest.constants.Constants;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.components.element.Element;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.app.Environment;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.data.distributed.common.KvManagerConfig;
import ohos.data.distributed.common.KvManagerFactory;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.event.commonevent.*;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.PixelMap;
import ohos.rpc.RemoteException;

import java.io.File;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, 0, "MainAbilitySlice");
    private static final String TAG = MainAbilitySlice.class.getName();
    private static final String CONTROL_ABILITY = "com.ohos.demo.uitest.ControlAbility";
    private TableLayout mTable;
    private Button mChooseBtn;
    private Image mPreviewImg;
    private MainAbilitySlice.MyCommonEventSubscriber mSubscriber;

    private int mCurrentRow = 0;
    private int mCurrentCol = 0;
    private int mMaxRow;
    private int mMaxCol;

    private DeviceInfo mControlDev;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        mTable = (TableLayout) findComponentById(ResourceTable.Id_tableLayout);
        mMaxRow = mTable.getRowCount();
        mMaxCol = mTable.getColumnCount();
        mChooseBtn = (Button) findComponentById(ResourceTable.Id_choose);
        mChooseBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                checkDevice();
            }
        });
        mPreviewImg = (Image) findComponentById(ResourceTable.Id_preview);
        updatePreview(0, 0);
        subscribe();
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
        unSubscribe();
    }

    private void checkDevice() {
        // 通过FLAG_GET_ONLINE_DEVICE标记获得在线设备列表
        List<DeviceInfo> deviceInfoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        if (deviceInfoList.size() < 1) {
            Util.showTip(this, "无在网设备");
        } else {
            showDeviceChooser(deviceInfoList);
        }
    }

    private void showDeviceChooser(List<DeviceInfo> deviceInfoList) {
        ListDialog dialog = new ListDialog(this);
        String[] names = new String[deviceInfoList.size()];
        for (int i = 0; i < deviceInfoList.size(); i++) {
            names[i] = deviceInfoList.get(i).getDeviceName();
        }
        dialog.setItems(names);
        dialog.setOnSingleSelectListener(new IDialog.ClickedListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                DeviceInfo info = deviceInfoList.get(i);
                Util.showTip(getContext(), "选择设备" + info.getDeviceName());
                openRemoteAbility(info.getDeviceId(), getBundleName(), CONTROL_ABILITY);
                mControlDev = info;
                dialog.hide();
            }
        });
        dialog.show();
    }

    public void openRemoteAbility(String deviceId, String bundleName, String abilityName) {
        Intent intent = new Intent();
        String localDeviceId = KvManagerFactory.getInstance()
                .createKvManager(new KvManagerConfig(this)).getLocalDeviceInfo().getId();
        intent.setParam("localDeviceId", localDeviceId);
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(deviceId)
                .withBundleName(bundleName)
                .withAbilityName(abilityName)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }


    private void subscribe() {
        MatchingSkills matchingSkills = new MatchingSkills();
        matchingSkills.addEvent(Constants.UPDATE_EVENT);
        CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(matchingSkills);
        mSubscriber = new MyCommonEventSubscriber(subscribeInfo);
        try {
            CommonEventManager.subscribeCommonEvent(mSubscriber);
        } catch (RemoteException e) {
            HiLog.error(LABEL_LOG, TAG, "subscribeCommonEvent occur exception.");
        }
    }

    private void unSubscribe() {
        try {
            CommonEventManager.unsubscribeCommonEvent(mSubscriber);
        } catch (RemoteException e) {
            HiLog.error(LABEL_LOG, "unSubscribe Exception");
        }
    }

    class MyCommonEventSubscriber extends CommonEventSubscriber {
        MyCommonEventSubscriber(CommonEventSubscribeInfo info) {
            super(info);
        }

        @Override
        public void onReceiveEvent(CommonEventData commonEventData) {
            Intent intent = commonEventData.getIntent();
            int requestType = intent.getIntParam(Constants.REQUEST_TYPE_KEY, -1);
            HiLog.info(LABEL_LOG, "onReceiveEvent requestType = " + requestType);
            switch (requestType) {
                case Constants.REQUEST_MOVE:
                    int direction = intent.getIntParam(Constants.DATA_KEY, 0);
                    handleMove(direction);
                    break;
                case Constants.REQUEST_SAVE:
                    getContext().getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                            .asyncDispatch(() -> handleSave());
                    break;
                default:
                    break;
            }
        }
    }

    private void handleMove(int direction) {
        mMaxRow = mTable.getRowCount();
        mMaxCol = mTable.getColumnCount();
        int delX = 0;
        int delY = 0;
        switch (direction) {
            case Constants.MOVE_LEFT:
                delX = -1;
                break;
            case Constants.MOVE_RIGHT:
                delX = 1;
                break;
            case Constants.MOVE_UP:
                delY = -1;
                break;
            case Constants.MOVE_DOWN:
                delY = 1;
                break;
        }
        int targetCol = mCurrentCol + delX;
        int targetRow = mCurrentRow + delY;
        HiLog.info(LABEL_LOG, "handleMove direction = " + direction
                + ", maxCol=" + mMaxCol + ", maxRow=" + mMaxRow
                + ", lastCol=" + mCurrentCol + ", lastRow=" + mCurrentRow
                + ", moveCol=" + delX + ", moveRow=" + delY);
        int last = mCurrentRow * mMaxCol + mCurrentCol;
        if (targetCol >= 0 && targetCol < mMaxCol) {
            mCurrentCol = targetCol;
        }
        if (targetRow >= 0 && targetRow < mMaxRow) {
            mCurrentRow = targetRow;
        }
        int target = mCurrentRow * mMaxCol + mCurrentCol;
        updatePreview(last, target);
    }

    private void updatePreview(int last, int target) {
        HiLog.info(LABEL_LOG, "handleMove from = " + last + " to " + target);
        Component lastView = mTable.getComponentAt(last);
        Component curView = mTable.getComponentAt(target);
        Element bg = lastView.getBackgroundElement();
        lastView.setBackground(null);
        curView.setBackground(bg);
        if (!(curView instanceof DirectionalLayout)) {
            return;
        }
        DirectionalLayout viewGroup = ((DirectionalLayout) curView);
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            Component view = viewGroup.getComponentAt(i);
            if (view instanceof Image) {
                PixelMap src = ((Image) view).getPixelMap();
                HiLog.info(LABEL_LOG, "updatePreview group = " + curView + ", src=" + src);
                mPreviewImg.setPixelMap(src);
                return;
            }
        }
    }

    private void handleSave() {
        PixelMap src = mPreviewImg.getPixelMap();
        File distDir = Util.getDisFile(getContext());
        String filePath = distDir + File.separator + Constants.FILE_NAME;
        HiLog.info(LABEL_LOG, "handleSave to "+filePath);
        Util.writDataToFile(src, filePath);
        String sdcardPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator + Constants.FILE_NAME;;
        HiLog.info(LABEL_LOG, "handleSave cache to "+sdcardPath);
        Util.writDataToFile(src, sdcardPath);
    }
}
