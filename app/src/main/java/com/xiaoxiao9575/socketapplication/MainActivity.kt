package com.xiaoxiao9575.socketapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var service: TextView? = null
    private var client: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setListener()
    }

    private fun initView() {
        service = findViewById(R.id.service)
        client = findViewById(R.id.client)
    }

    private fun setListener() {
        service!!.setOnClickListener { startActivity(Intent(this@MainActivity, ServiceActivity::class.java)) }
        client!!.setOnClickListener { startActivity(Intent(this@MainActivity, ClientActivity::class.java)) }
    }
}