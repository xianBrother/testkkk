package com.xiaoxiao9575.socketapplication

import android.content.Context
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.WifiInfo
import android.widget.Toast
import com.xiaoxiao9575.socketapplication.NetWorkUtil
import com.xiaoxiao9575.socketapplication.NetWorkUtil.intIP2StringIP
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

object NetWorkUtil {
    @JvmStatic
    fun getIPAddress(context: Context): String? {
        val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(context,"请使用wifi",Toast.LENGTH_SHORT)
            } else if (info.type == ConnectivityManager.TYPE_WIFI) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return intIP2StringIP(wifiInfo.ipAddress)
            }
        } else {
        }
        return null
    }

    fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }
}