package javasnack.snacks;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * Original source code:
 * - http://itpro.nikkeibp.co.jp/article/COLUMN/20070801/278834/
 * - http://d.hatena.ne.jp/nowokay/20120803
 * - https://gist.github.com/takashi209/3057659
 * 
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
 */
public class NetworkInterface1 implements Runnable {
    @Override
    public void run() {
        try {
            final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
            System.out.println("=============== InetAddress.getLoopbackAddress()");
            dumpInetAddress(loopbackAddress);

            final InetAddress localhostAddress = InetAddress.getLocalHost();
            System.out.println("=============== InetAddress.getLocalHost()");
            dumpInetAddress(localhostAddress);

            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                dumpNetworkInterfaceInformation(nics.nextElement());
            }
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static void dumpInetAddress(final InetAddress ia) {
        System.out.println("getHostAddress()=" + ia.getHostAddress());
        System.out.println("getHostName()=" + ia.getHostName());
        System.out.println("getCanonicalHostName()=" + ia.getCanonicalHostName());
        System.out.println("isAnyLocalAddress()=" + ia.isAnyLocalAddress());
        System.out.println("isLinkLocalAddress()=" + ia.isLinkLocalAddress());
        System.out.println("isLoopbackAddress()=" + ia.isLoopbackAddress());
        System.out.println("isMCGlobal()=" + ia.isMCGlobal());
        System.out.println("isMCLinkLocal()=" + ia.isMCLinkLocal());
        System.out.println("isMCNodeLocal()=" + ia.isMCNodeLocal());
        System.out.println("isMCOrgLocal()=" + ia.isMCOrgLocal());
        System.out.println("isMCSiteLocal()=" + ia.isMCSiteLocal());
        System.out.println("isMulticastAddress()=" + ia.isMulticastAddress());
        System.out.println("isSiteLocalAddress()=" + ia.isSiteLocalAddress());
    }

    /**
     * copied from:
     * - http://itpro.nikkeibp.co.jp/article/COLUMN/20070801/278834/
     * 
     *      (adding some changes for sysout.) thanx!
     */
    static void dumpNetworkInterfaceInformation(NetworkInterface nic) throws SocketException {
        System.out.println("-----------------------");
        System.out.println("Name: " + nic.getName());
        System.out.println("DisplayName: " + nic.getDisplayName());

        List<InterfaceAddress> iaddresses = nic.getInterfaceAddresses();
        for (InterfaceAddress iaddr : iaddresses) {
            System.out.println("  ==========================");
            System.out.println("  Address: " + iaddr.getAddress());
            System.out.println("  Broadcast: " + iaddr.getBroadcast());
            System.out.println("  Prefix Length: " + iaddr.getNetworkPrefixLength());
        }
        System.out.println("  ==========================");

        Enumeration<NetworkInterface> subInterfaces = nic.getSubInterfaces();
        while (subInterfaces.hasMoreElements()) {
            System.out.println("Sub Interface: " + subInterfaces.nextElement().getDisplayName());
        }

        NetworkInterface parent = nic.getParent();
        if (null != parent) {
            System.out.println("Parent Interface: " + parent.getDisplayName());
        }

        System.out.println("Status : " + (nic.isUp() ? "UP" : "DOWN"));
        System.out.println(nic.isLoopback() ? "Loopback" : "NO Loopback");
        System.out.println(nic.isPointToPoint() ? "PPP" : "NO PPP");
        System.out.println(nic.supportsMulticast() ? "MULTICAST Supported" : "MULTICAST NOT Supported");

        byte[] hwAddress = nic.getHardwareAddress();
        if (null != hwAddress) {
            System.out.print("MAC Address: ");
            for (byte segment : hwAddress) {
                System.out.printf("%02x ", segment);
            }
            System.out.println();
        }
        System.out.println("MTU: " + nic.getMTU());
        System.out.println(nic.isVirtual() ? "Virtual Interface" : "Physical Interface");
    }
}
