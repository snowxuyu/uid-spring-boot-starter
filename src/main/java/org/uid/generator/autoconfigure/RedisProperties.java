package org.uid.generator.autoconfigure;

import lombok.Data;

import java.time.Duration;

/**
 * Copyright:
 * Description:
 * Date: 2021/7/8 11:28 上午
 * @author snowxuyu
 */

@Data
public class RedisProperties {
    /**
     * 主机，默认为localhost
     */
    private String host = "localhost";
    /**
     * 端口，默认为6379
     */
    private int port = 6379;
    /**
     * 认证密码
     */
    private String password;
    /**
     * 连接哪个数据库，默认为15
     */
    private int database = 15;
    /**
     * 连接超时时间.
     */
    private Duration timeout;

    private final RedisProperties.Lettuce lettuce = new RedisProperties.Lettuce();


    /**
     * Lettuce client properties.
     */
    @Data
    public static class Lettuce {

        /**
         * Shutdown timeout.
         */
        private Duration shutdownTimeout = Duration.ofMillis(100);

        /**
         * Lettuce pool configuration.
         */
        private RedisProperties.Pool pool;

    }

    /**
     * Pool properties.
     */
    @Data
    public static class Pool {

        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 8;

        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if it is positive.
         */
        private int minIdle = 0;

        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit.
         */
        private int maxActive = 8;

        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely.
         */
        private Duration maxWait = Duration.ofMillis(-1);
    }
}
