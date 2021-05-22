package com.ohos.demo.uitest;

import com.ohos.demo.uitest.constants.Constants;
import com.ohos.demo.uitest.proxy.ConnectManagerIml;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventManager;
import ohos.rpc.*;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ServiceAbility extends Ability {
    private static final String TAG = "ServiceAbility";
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "ServiceAbility");

    private MyRemote remote = new MyRemote();

    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "ServiceAbility::onStart");
        super.onStart(intent);
    }

    @Override
    public void onBackground() {
        super.onBackground();
        HiLog.info(LABEL_LOG, "ServiceAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        HiLog.info(LABEL_LOG, "ServiceAbility::onStop");
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        super.onConnect(intent);
        return remote.asObject();
    }

    @Override
    public void onDisconnect(Intent intent) {
    }

    /**
     * MyRemote
     *
     * @since 2021-02-25
     */
    public class MyRemote extends RemoteObject implements IRemoteBroker {
        private MyRemote() {
            super("===MyService_Remote");
        }

        @Override
        public IRemoteObject asObject() {
            return this;
        }

        @Override
        public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
            HiLog.info(LABEL_LOG,  "===onRemoteRequest......");
            int requestType = data.readInt();
            if (requestType == Constants.REQUEST_MOVE) {
                int direction = data.readInt();
                sendEvent(requestType, direction);
            } else {
                sendEvent(requestType, 0);
            }
            return true;
        }
    }

    private void sendEvent(int requestType, int direction) {
        HiLog.info(LABEL_LOG,  "sendEvent......");
        try {
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withAction(Constants.UPDATE_EVENT)
                    .build();
            intent.setOperation(operation);
            if (requestType == Constants.REQUEST_MOVE) {
                intent.setParam(Constants.DATA_KEY, direction);
            }
            intent.setParam(Constants.REQUEST_TYPE_KEY, requestType);
            CommonEventData eventData = new CommonEventData(intent);
            CommonEventManager.publishCommonEvent(eventData);
        } catch (RemoteException e) {
            HiLog.error(LABEL_LOG,  "publishCommonEvent occur exception.");
        }
    }
}