package com.duyvv.dhbc.ui.play

import com.duyvv.dhbc.databinding.ActivityPlayBinding
import com.duyvv.dhbc.base.BaseActivity

class PlayActivity : BaseActivity<ActivityPlayBinding>() {

    override fun createBinding() = ActivityPlayBinding.inflate(layoutInflater)

    override val context = this
}