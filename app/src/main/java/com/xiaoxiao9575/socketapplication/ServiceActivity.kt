package com.xiaoxiao9575.socketapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.xiaoxiao9575.socketapplication.NetWorkUtil.getIPAddress
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ServiceActivity : AppCompatActivity() {
    private var tv_showIP: TextView? = null
    private var tv_ip: TextView? = null
    private var tv_msg: TextView? = null
    private var mServerSocket: ServerSocket? = null
    private var mSocket: Socket? = null
    private val sb = StringBuffer()
    @SuppressLint("HandlerLeak")
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                val data = msg.data
                sb.append(data.getString("msg"))
                sb.append("\n")
                tv_msg!!.text = sb.toString()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        initView()
        setListener()
        try {
            mServerSocket = ServerSocket(1989)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val socketAcceptThread = SocketAcceptThread()
        socketAcceptThread.start()
    }

    private fun initView() {
        tv_showIP = findViewById(R.id.tv_showIP)
        tv_ip = findViewById(R.id.tv_ip)
        tv_msg = findViewById(R.id.tv_msg)
    }

    private fun setListener() {
        tv_showIP!!.setOnClickListener { tv_ip!!.text = getIPAddress(this@ServiceActivity) }
    }

    internal inner class SocketAcceptThread : Thread() {
        override fun run() {
            mSocket = try { //等待客户端的连接，Accept会阻塞，直到建立连接，
//所以需要放在子线程中运行。
                mServerSocket!!.accept()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("info", "run: ==============" + "accept error")
                return
            }
            Log.e("info", "accept success==================")
            //启动消息接收线程
            startReader(mSocket)
        }
    }

    /**
     * 从参数的Socket里获取最新的消息
     */
    private fun startReader(socket: Socket?) {
        object : Thread() {
            override fun run() {
                val reader: DataInputStream
                try { // 获取读取流
                    reader = DataInputStream(socket!!.getInputStream())
                    while (true) {
                        println("*等待客户端输入*")
                        // 读取数据
                        val msg = reader.readUTF()
                        println("获取到客户端的信息：=$msg")
                        //告知客户端消息收到
                        val writer = DataOutputStream(mSocket!!.getOutputStream())
                        writer.writeUTF("收到：$msg") // 写一个UTF-8的信息
                        //发消息更新UI
                        val message = Message()
                        message.what = 1
                        val bundle = Bundle()
                        bundle.putString("msg", msg)
                        message.data = bundle
                        handler.sendMessage(message)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        if (mServerSocket != null) {
            try {
                mServerSocket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onDestroy()
    }
}