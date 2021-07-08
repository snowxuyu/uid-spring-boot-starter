package org.uid.generator.support.provider;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Copyright:  基于Redis的机器ID提供者
 * Description:
 * Date: 2021/7/7 5:40 下午
 * Author: gaoguoxiang
 */

public class RedisMachineIdProvider implements MachineIdProvider{

    private static final String HOST_INNER_IP_ADDRESS = "HOST_INNER_IP_ADDRESS";

    private final StringRedisTemplate stringRedisTemplate;

    public RedisMachineIdProvider(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public long getMachineId(String innerIpAddress) {
        long machineId;
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        if(hashOperations.hasKey(HOST_INNER_IP_ADDRESS, innerIpAddress)){
            machineId = Long.parseLong(hashOperations.get(HOST_INNER_IP_ADDRESS, innerIpAddress).toString());
        } else {
            machineId = hashOperations.size(HOST_INNER_IP_ADDRESS);
            hashOperations.put(HOST_INNER_IP_ADDRESS, innerIpAddress, Long.toString(machineId));
        }
        return machineId;
    }
}
