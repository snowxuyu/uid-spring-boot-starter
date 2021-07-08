package org.uid.generator.autoconfigure;

/**
 * Copyright:
 * Description:
 * Date: 2021/7/7 5:12 下午
 * @author snowxuyu
 */

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "uid.generator.config")
public class UidGeneratorProperties {

    /**
     * 策略  redis or zookeeper
     */
    private StrategyEnum strategy;

    @NestedConfigurationProperty
    private RedisProperties redis;

    @NestedConfigurationProperty
    private ZookeeperProperties zookeeper;


    public enum StrategyEnum {

        /**
         * redis
         */
        redis,

        /**
         * zookeeper
         */
        zookeeper;
    }
}
