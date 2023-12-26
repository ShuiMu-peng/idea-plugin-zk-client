package com.lipeng.entity;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.zookeeper.data.Stat;

/**
 * @author lipeng 2023/12/21
 */
@Getter
@Setter
@ToString
public class ZkNodeInfo {
    private String path;
    private String data;
    private String updateTime;
    private Integer size;
    private Integer version;

    public ZkNodeInfo(Stat stat, String path, String data) {
        this.path = path;
        this.data = data;
        this.updateTime = LocalDateTimeUtil.of(stat.getMtime()).toString();
        this.size = stat.getDataLength();
        this.version = stat.getVersion();
    }
}
