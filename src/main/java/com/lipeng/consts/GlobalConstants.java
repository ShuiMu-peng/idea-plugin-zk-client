package com.lipeng.consts;

import cn.hutool.core.util.StrUtil;

/**
 * @author lipeng 2023/12/23
 */
public class GlobalConstants {
    public static final String CLOSE_ITEM = "----please select----";
    public static final String CLOSE_MSG = "Add or select the zookeeper address";

    public static boolean isCloseMsg(String host) {
        return StrUtil.equals(host, CLOSE_MSG);
    }
}
