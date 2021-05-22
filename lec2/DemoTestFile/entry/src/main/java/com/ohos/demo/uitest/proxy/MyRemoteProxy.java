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

import com.ohos.demo.uitest.constants.Constants;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.*;

import java.util.Map;

public class MyRemoteProxy implements IRemoteBroker {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, 0, "MyRemoteProxy");

    public static final int ERR_OK = 0;
    private static final String TAG = MyRemoteProxy.class.getSimpleName();
    // request successful flag
    private final IRemoteObject remote;

    public MyRemoteProxy(IRemoteObject remote) {
        this.remote = remote;
    }

    @Override
    public IRemoteObject asObject() {
        return remote;
    }

    public int senDataToRemote(int requestType, Map paramMap) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        int ec = 1;

        try {
            if (requestType == Constants.REQUEST_SAVE) {
                data.writeInt(requestType);
                remote.sendRequest(requestType, data, reply, option);
            } else if (paramMap.get(Constants.DATA_KEY) instanceof Integer) {
                int direction = (int) paramMap.get(Constants.DATA_KEY);
                data.writeInt(requestType);
                data.writeInt(direction);
                remote.sendRequest(requestType, data, reply, option);
            }

            ec = reply.readInt();
            if (ec != ERR_OK) {
                HiLog.error(LABEL_LOG, TAG, "RemoteException:");
            }
        } catch (RemoteException e) {
            HiLog.error(LABEL_LOG, TAG, "RemoteException:");
        } finally {
            ec = ERR_OK;
            data.reclaim();
            reply.reclaim();
        }
        return ec;
    }
}
