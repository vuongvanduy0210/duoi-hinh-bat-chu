package com.duyvv.dhbc.ui

import android.content.Intent
import com.duyvv.dhbc.databinding.ActivityMainBinding
import com.duyvv.dhbc.ui.play.PlayActivity
import com.duyvv.dhbc.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun createBinding() = ActivityMainBinding.inflate(layoutInflater)

    override val context = this

    override fun setUp() {
        super.setUp()

        binding.btnStartGame.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
        }
    }
}