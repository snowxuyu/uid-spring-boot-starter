package org.uid.generator.autoconfigure;

import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.uid.generator.support.IdWorker;
import org.uid.generator.support.provider.RedisMachineIdProvider;
import org.uid.generator.support.provider.ZookeeperMachineIdProvider;
import org.uid.generator.support.util.InnerIpAddressUtils;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Copyright:
 * Description:
 * Date: 2021/7/7 5:11 下午
 * @author snowxuyu
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(UidGeneratorProperties.class)
public class UidGeneratorAutoConfigure {

    private static final long MAX_SEQUENCE = -1L ^ (-1L << 12L);
    private static final long MACHINE_MASK = 1 << 5;

    @Bean
    @ConditionalOnProperty(prefix = "uid.generator.config", value = "strategy", havingValue = "redis")
    public RedisMachineIdProvider machineRedisIdProvider(UidGeneratorProperties uidGeneratorProperties) {
        RedisStandaloneConfiguration standaloneConfig = getStandaloneConfig(uidGeneratorProperties.getRedis());
        LettuceClientConfiguration clientConfiguration = getLettuceClientConfiguration(uidGeneratorProperties.getRedis());
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfig, clientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        StringRedisTemplate template = new StringRedisTemplate(lettuceConnectionFactory);
        return new RedisMachineIdProvider(template);
    }

    @Bean
    @ConditionalOnProperty(prefix = "uid.generator.config", value = "strategy", havingValue = "zookeeper")
    public ZookeeperMachineIdProvider machineZookeeperIdProvider(UidGeneratorProperties uidGeneratorProperties) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(uidGeneratorProperties.getZookeeper().getConnectString())
                .retryPolicy(new ExponentialBackoffRetry(uidGeneratorProperties.getZookeeper().getBaseSleepTimeMs(),
                        uidGeneratorProperties.getZookeeper().getMaxRetries()))
                .namespace(uidGeneratorProperties.getZookeeper().getNamespace())
                .build();
        return new ZookeeperMachineIdProvider(curatorFramework);
    }

    @Bean
    @ConditionalOnBean(RedisMachineIdProvider.class)
    public IdWorker idWorkerRedis(RedisMachineIdProvider machineIdProvider) throws SocketException, UnknownHostException {
        log.info("===============初始化基于redis的id生成器===============");
        String innerIpAddress = InnerIpAddressUtils.getInnerIpAddress();
        long machineId = machineIdProvider.getMachineId(innerIpAddress);
        long workerId = machineId % MACHINE_MASK;
        long dataCenterId = machineId >> 5;
        return new IdWorker(workerId, dataCenterId, MAX_SEQUENCE);
    }

    @Bean
    @ConditionalOnBean(ZookeeperMachineIdProvider.class)
    public IdWorker idWorkerZookeeper(ZookeeperMachineIdProvider machineIdProvider) throws SocketException, UnknownHostException {
        log.info("===============初始化基于zookeeper的id生成器===============");
        String innerIpAddress = InnerIpAddressUtils.getInnerIpAddress();
        long machineId = machineIdProvider.getMachineId(innerIpAddress);
        long workerId = machineId % MACHINE_MASK;
        long dataCenterId = machineId >> 5;
        return new IdWorker(workerId, dataCenterId, MAX_SEQUENCE);
    }


    private RedisStandaloneConfiguration getStandaloneConfig(RedisProperties redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(RedisProperties redisProperties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder;
        RedisProperties.Pool pool = redisProperties.getLettuce().getPool();
        if ( pool == null ) {
            builder = LettuceClientConfiguration.builder();
        } else {
            builder = new PoolBuilderFactory().createBuilder(pool);
        }
        if ( redisProperties.getTimeout() != null ) {
            builder.commandTimeout(redisProperties.getTimeout());
        }
        if ( redisProperties.getLettuce() != null ) {
            RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
            if ( lettuce.getShutdownTimeout() != null
                    && !lettuce.getShutdownTimeout().isZero() ) {
                builder.shutdownTimeout(redisProperties.getLettuce().getShutdownTimeout());
            }
        }
        builder.clientResources(DefaultClientResources.create());
        return builder.build();
    }

    /**
     * Inner class to allow optional commons-pool2 dependency.
     */
    private static class PoolBuilderFactory {

        public LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
            return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(pool));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool pool) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(pool.getMaxActive());
            config.setMaxIdle(pool.getMaxIdle());
            config.setMinIdle(pool.getMinIdle());
            if ( pool.getMaxWait() != null ) {
                config.setMaxWaitMillis(pool.getMaxWait().toMillis());
            }
            return config;
        }
    }
}
