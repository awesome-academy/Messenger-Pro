package com.example.finalapplication.screen.launch

import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.finalapplication.utils.Constant
import com.example.finalapplication.utils.TimeConstant
import com.example.finalapplication.utils.base.BaseActivity
import com.example.finalapplication.databinding.ActivityLaunchBinding
import com.example.finalapplication.screen.login.LoginActivity
import com.example.finalapplication.screen.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LaunchActivity : BaseActivity<ActivityLaunchBinding>(ActivityLaunchBinding::inflate) {
    private val launchViewModel: LaunchViewModel by viewModel()
    private var isFinish = false

    override fun handleEvent() {
        // TODO("Not yet implemented")
    }

    override fun initData() {
        // TODO("Not yet implemented")
    }

    override fun bindData() {
        lifecycleScope.launch {
             delay(TimeConstant.DEALAY_LAUNCH)
            launchViewModel.user.observe(this@LaunchActivity) { user ->
                if (user == null) {
                    val intent = Intent(this@LaunchActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (user.role.equals(Constant.ROLE_CLIENT) && !isFinish) {
                        isFinish = true
                        val intent = Intent(this@LaunchActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}
