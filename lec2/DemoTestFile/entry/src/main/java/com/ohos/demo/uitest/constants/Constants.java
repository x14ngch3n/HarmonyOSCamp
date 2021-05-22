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

package com.ohos.demo.uitest.constants;

import ohos.hiviewdfx.HiLogLabel;

public class Constants {
    /**
     * move left
     *
     */
    public static final int MOVE_LEFT = 1;
    /**
     *  move right
     *
     */
    public static final int MOVE_RIGHT = 2;
    /**
     * move down
     *
     */
    public static final int MOVE_DOWN = 3;
    /**
     * move up
     *
     */
    public static final int MOVE_UP = 4;

    public static final int REQUEST_MOVE = 1;

    public static final int REQUEST_SAVE = 2;

    public static final String REQUEST_TYPE_KEY = "requestType";

    public static final String DATA_KEY = "move";

    public static final String UPDATE_EVENT = "remote_control_event";

    public static final String FILE_NAME = "save.png";

    private Constants() {
    }
}
