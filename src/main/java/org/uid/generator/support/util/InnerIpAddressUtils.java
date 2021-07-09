package org.uid.generator.support.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Copyright:
 * Description:
 * Date: 2021/7/9 9:45 上午
 * @author snowxuyu
 */

public class InnerIpAddressUtils {

    private InnerIpAddressUtils(){}

    /**
     * 获取内网IP
     *
     * @return
     * @throws SocketException
     * @throws UnknownHostException
     */
    public static String getInnerIpAddress() throws SocketException, UnknownHostException {
        InetAddress candidateAddress = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface iface = networkInterfaces.nextElement();
            for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                InetAddress inetAddr = inetAddrs.nextElement();
                if ( !inetAddr.isLoopbackAddress() ) {
                    if ( inetAddr.isSiteLocalAddress() ) {
                        return inetAddr.getHostAddress();
                    }
                    if ( candidateAddress == null ) {
                        candidateAddress = inetAddr;
                    }
                }
            }
        }
        return candidateAddress == null ? InetAddress.getLocalHost().getHostAddress() : candidateAddress.getHostAddress();
    }
}
