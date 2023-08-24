package info.mx.tracks.util

import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

object NetworkUtils {

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    fun getMACAddress(interfaceName: String?): String {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                if (interfaceName != null) {
                    if (!networkInterface.name.equals(interfaceName, ignoreCase = true)) {
                        continue
                    }
                }
                val mac = networkInterface.hardwareAddress ?: return ""
                val buf = StringBuilder()
                for (idx in mac.indices) {
                    buf.append(String.format("%02X:", mac[idx]))
                }
                if (buf.isNotEmpty()) {
                    buf.deleteCharAt(buf.length - 1)
                }
                return buf.toString()
            }
        } catch (ex: Exception) {
        } // for now eat exceptions
        return ""
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                val addresses: List<InetAddress> = Collections.list(networkInterface.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        val hostAddress = address.hostAddress ?: ""
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(hostAddress);
                        val isIPv4 = hostAddress.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) {
                                return hostAddress
                            }
                        } else {
                            if (!isIPv4) {
                                val delimiter = hostAddress.indexOf('%') // drop ip6 zone suffix
                                return if (delimiter < 0) hostAddress.uppercase(Locale.getDefault()) else hostAddress.substring(
                                    0,
                                    delimiter
                                ).uppercase(Locale.getDefault())
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        } // for now eat exceptions
        return ""
    }
}
