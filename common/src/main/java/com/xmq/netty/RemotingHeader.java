/*
 * Copyright 2018 Qunar, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xmq.netty;

import com.xmq.util.MessageTypeEnum;

import java.util.Date;

public class RemotingHeader {
    public static final int DEFAULT_MAGIC_CODE = 0X86;
    private int magicCode = DEFAULT_MAGIC_CODE;
    private int requestCode = MessageTypeEnum.NULL.getType();
    private int length = 0;
    public static final short LENGTH_FIELD = 4 + 4 + 4;


    public int getMagicCode() {
        return magicCode;
    }

    public void setMagicCode(int magicCode) {
        this.magicCode = magicCode;
    }

    public static int getDefaultMagicCode() {
        return DEFAULT_MAGIC_CODE;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

}
