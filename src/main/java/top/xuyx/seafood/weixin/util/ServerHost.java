package top.xuyx.seafood.weixin.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Helper class the get the external net IP address
 */
public class ServerHost {

    public static String getWebIp() {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {

        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }


    // 获取本机的外网IP
    public static String getWebIp0() {
        try {

            URL url = new URL("http://1212.ip138.com/ic.asp");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = "";
            StringBuffer sb = new StringBuffer("");
            String webContent = "";
            while ((s = br.readLine()) != null) {
                sb.append(s + "rn");
            }

            br.close();
            webContent = sb.toString();
            int start = webContent.indexOf("[") + 1;
            int end = webContent.indexOf("]");
            // System.out.println("webContent=" + webContent);
            // System.out.println("start=" + start);
            // System.out.println("end=" + end);
            if (start < 0 || end < 0) {
                return null;
            }
            webContent = webContent.substring(start, end);
            return webContent;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("本机的外网IP是：" + getWebIp());
    }
}
