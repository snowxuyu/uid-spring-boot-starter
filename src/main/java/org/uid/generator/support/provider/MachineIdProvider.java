package org.uid.generator.support.provider;

/**
 * Copyright: 机器ID提供者
 * Description:
 * Date: 2021/7/7 5:39 下午
 * @author snowxuyu
 */

public interface MachineIdProvider {
    /**
     * 根据主机内网地址来获取机器ID
     * @param innerIpAddress
     * @return
     */
    long getMachineId(String innerIpAddress);
}
