/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohos.demo.uitest.proxy;

import com.ohos.demo.uitest.ServiceAbility;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.app.Context;
import ohos.bundle.ElementName;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;

import java.util.Map;

/**
 * ConnectManagerIml
 *
 * @since 2021-02-25
 */
public class ConnectManagerIml implements ConnectManager {

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, 0, "ConnectManagerIml");

    private static final String TAG = ConnectManagerIml.class.getName();

    private static ConnectManager instance;
    private IAbilityConnection conn;
    private MyRemoteProxy proxy;

    /**
     * 获取实例
     *
     * @return instance
     */
    public static synchronized ConnectManager getInstance() {
        if (instance == null) {
            instance = new ConnectManagerIml();
        }
        return instance;
    }

    /**
     * 连接远程PA
     *
     * @param context context
     * @param deviceId deviceId
     */
    @Override
    public void connectPa(Context context, String deviceId) {
        if (deviceId != null && !deviceId.trim().isEmpty()) {
            Intent connectPaIntent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId(deviceId)
                    .withBundleName(context.getBundleName())
                    .withAbilityName(ServiceAbility.class.getName())
                    .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                    .build();
            connectPaIntent.setOperation(operation);
            conn = new IAbilityConnection() {
                @Override
                public void onAbilityConnectDone(ElementName elementName, IRemoteObject remote, int resultCode) {
                    HiLog.info(LABEL_LOG,  "===connectRemoteAbility done");
                    proxy = new MyRemoteProxy(remote);
                }

                @Override
                public void onAbilityDisconnectDone(ElementName elementName, int resultCode) {
                    HiLog.info(LABEL_LOG, TAG, "onAbilityDisconnectDone......");
                    proxy = null;
                }
            };
            context.connectAbility(connectPaIntent, conn);
        }
    }

    @Override
    public void sendRequest(int requestType, Map<String, Integer> params) {
        if (proxy != null) {
            proxy.senDataToRemote(requestType, params);
        }
    }
}
