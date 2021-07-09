package org.uid.generator.support.util;

import lombok.extern.slf4j.Slf4j;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright:
 * Description: 订单号生成工具类
 * 19位订单号生成策略: 13位毫秒时间戳+内网IP地址取模(2位)+4位自增流水
 * 21位订单号生成策略: 10位毫秒时间戳+内网IP地址取模(2位)+4位自增流水+用户id后5位
 * Date: 2021/7/9 9:40 上午
 * @author snowxuyu
 */
@Slf4j
public class OrderCodeUtils {

    private static final int MIN_NUMBER = 1000;
    private static final int MAX_NUMBER = 9999;
    private static final AtomicInteger SEQUENCE = new AtomicInteger(MIN_NUMBER);

    private OrderCodeUtils() {
    }

    /**
     * 生成19位订单号
     *
     * @return
     */
    public static String generatorOrderCode() {
        if ( SEQUENCE.intValue() > MAX_NUMBER ) {
            SEQUENCE.getAndSet(MIN_NUMBER);
        }
        //13位毫秒时间戳+ip地址取模+自增流水
        try {
            return String.valueOf(Instant.now().toEpochMilli())
                    .concat(String.valueOf(Long.parseLong(InnerIpAddressUtils.getInnerIpAddress().replace(".", "")) % 32))
                    .concat(String.valueOf(SEQUENCE.getAndIncrement()));
        } catch (SocketException | UnknownHostException e) {
            log.error("生成订单号失败: ", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 根据用户id生成21位订单号
     *
     * @param memberId 用户id 必须大于5位
     * @return
     */
    public static String generatorOrderCode(Long memberId) {
        if ( SEQUENCE.intValue() > MAX_NUMBER ) {
            SEQUENCE.getAndSet(MIN_NUMBER);
        }
        if ( memberId == null ) {
            log.error("根据用户Id生成订单号 memberId: {}", memberId);
            throw new RuntimeException("用户id不能为空");
        }
        //10位秒时间戳+ip地址取模+自增流水+用户id后5位
        try {
            return String.valueOf(Instant.now().getEpochSecond())
                    .concat(String.valueOf(Long.parseLong(InnerIpAddressUtils.getInnerIpAddress().replace(".", "")) % 32))
                    .concat(String.valueOf(SEQUENCE.getAndIncrement()))
                    .concat(memberId.toString().substring(memberId.toString().length() - 5));
        } catch (SocketException | UnknownHostException e) {
            log.error("生成订单号失败: ", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
