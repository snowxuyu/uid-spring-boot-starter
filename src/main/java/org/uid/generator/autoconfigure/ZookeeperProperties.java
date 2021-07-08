package org.uid.generator.autoconfigure;

import lombok.Data;

/**
 * Copyright:
 * Description:
 * Date: 2021/7/8 1:23 下午
 * Author: gaoguoxiang
 */

@Data
public class ZookeeperProperties {
    /**
     * 连接地址  ip1:port1,ip2:port2,ip3:port3
     */
    private String connectString;
    /**
     * 命名空间，默认为IdWorker
     */
    private String namespace = "IdWorker";
    /**
     * 基本休眠时间（单位：毫秒），默认为3000毫秒
     */
    private Integer baseSleepTimeMs = 3000;
    /**
     * 最大重试次数，默认为3次
     */
    private Integer maxRetries = 3;
}
