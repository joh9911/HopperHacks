package com.example.hopperhacks

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.hopperhacks.databinding.ActivityMainBinding

class MainActivity: FragmentActivity() {
    var mBinding: ActivityMainBinding? = null
    val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}