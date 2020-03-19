package com.xiaoxiao9575.socketapplication

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class ClientActivity : AppCompatActivity() {
    private var et_ip: EditText? = null
    private var et_msg: EditText? = null
    private var tv_send: TextView? = null
    private var tv_confirm: TextView? = null
    private var mSocket: Socket? = null
    private var socketConnectThread: SocketConnectThread? = null
    private val sb = StringBuffer()
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
    private var tv_msg: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        socketConnectThread = SocketConnectThread()
        initView()
        setListener()
    }

    private fun initView() {
        et_ip = findViewById(R.id.et_ip)
        et_msg = findViewById(R.id.et_msg)
        tv_send = findViewById(R.id.tv_send)
        tv_confirm = findViewById(R.id.tv_confirm)
        tv_msg = findViewById(R.id.tv_msg)
    }

    private fun setListener() {
        tv_send!!.setOnClickListener { send(et_msg!!.text.toString()) }
        tv_confirm!!.setOnClickListener {
            tv_confirm!!.isEnabled = false
            Toast.makeText(this,"建立连接成功",Toast.LENGTH_SHORT)
            socketConnectThread!!.start()
        }
    }

    internal inner class SocketConnectThread : Thread() {
        override fun run() {
            mSocket = try { //指定ip地址和端口号
                Socket(et_ip!!.text.toString(), 1989)
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            startReader(mSocket!!)
        }
    }

    fun send(str: String) {
        if (str.length == 0) {
            return
        }
        object : Thread() {
            override fun run() {
                try {
                    val writer = DataOutputStream(mSocket!!.getOutputStream())
                    writer.writeUTF(str)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun startReader(socket: Socket) {
        object : Thread() {
            override fun run() {
                val reader: DataInputStream
                try {
                    reader = DataInputStream(socket.getInputStream())
                    while (true) {
                        val msg = reader.readUTF()
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
}