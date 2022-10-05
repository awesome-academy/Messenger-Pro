package com.example.finalapplication.screen.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.finalapplication.data.model.User
import com.example.finalapplication.databinding.DialogChangePasswordBinding
import com.example.finalapplication.databinding.DialogChangeProfileBinding
import com.example.finalapplication.databinding.FragmentProfileBinding
import com.example.finalapplication.screen.login.LoginActivity
import com.example.finalapplication.utils.Constant
import com.example.finalapplication.utils.NetworkUtils
import com.example.finalapplication.utils.base.BaseFragment
import com.example.finalapplication.utils.loadImageByUrl
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val profileViewModel: ProfileViewModel by viewModel()
    private var user: User = User()
    private lateinit var loading: ProgressDialog
    private var dialogChangeProfile: AlertDialog? = null
    private var dialogChangePassword: AlertDialog? = null
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result.data
                val uri = intent?.data
                user.avatar = uri.toString()
                profileViewModel.updateAvatar(user)
            }
        }

    override fun handleEvent() {
        binding.apply {
            buttonLogout.setOnClickListener {
                profileViewModel.logout()
            }
            buttonChangeProfile.setOnClickListener {
                showDialogChangeProfile()
            }
            buttonChangePassword.setOnClickListener {
                showDialodChangePassword()
            }
            imageAvatar.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = Constant.TYPE_IMAGE
                activityResultLauncher.launch(intent)
            }
        }
    }

    private fun showDialodChangePassword() {
        dialogChangePassword = AlertDialog.Builder(context).create()
        val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)
        dialogChangePassword?.setView(dialogBinding.root)
        dialogChangePassword?.show()
        dialogBinding.apply {
            buttonCancel.setOnClickListener { dialogChangePassword?.cancel() }
            buttonSubmit.setOnClickListener {
                val oldPassword = edittextOldPassword.text.toString()
                val newPassword = edittextNewPassword.text.toString()
                val confirmPassword = edittextConfirmPassword.text.toString()
                profileViewModel.updatePassword(user, oldPassword, newPassword, confirmPassword)
            }
        }
    }

    private fun showDialogChangeProfile() {
        dialogChangeProfile = AlertDialog.Builder(context).create()
        val dialogBinding = DialogChangeProfileBinding.inflate(layoutInflater)
        dialogChangeProfile?.setView(dialogBinding.root)
        dialogChangeProfile?.show()
        dialogBinding.apply {
            (edittextName as TextView).text = user.name
            (edittextAccountnumber as TextView).text = user.bankAccountName
            (edittextBankAccountName as TextView).text = user.bankAccount
            (edittextPhoneNumber as TextView).text = user.phoneNumber
            buttonCancel.setOnClickListener { dialogChangeProfile?.cancel() }
            buttonSubmit.setOnClickListener {
                val name = edittextName.text.toString()
                val phoneNumber = edittextPhoneNumber.text.toString()
                val nameAccount = edittextBankAccountName.text.toString()
                val bankAccount = edittextAccountnumber.text.toString()
                user.name = name
                user.phoneNumber = phoneNumber
                user.bankAccountName = nameAccount
                user.bankAccount = bankAccount
                profileViewModel.updateUser(user, name)
            }
        }
    }

    override fun initData() {
        loading = ProgressDialog(context)
        loading.setMessage(Constant.MSG_UPDATE)
    }

    override fun bindData() {
        val dialogNoInternet =
            NetworkUtils.getDialogNoInternet(context, layoutInflater, binding.root)
        val isConnected =
            NetworkUtils(context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        isConnected.observe(this) { data ->
            if (data == false) dialogNoInternet.show()
            else dialogNoInternet.hide()
        }
        profileViewModel.user.observe(this) { user ->
            binding.apply {
                this@ProfileFragment.user = user
                imageAvatar.loadImageByUrl(user.avatar, context)
                textName.text = user.name
                textEmail.text = user.userAccount?.email
            }
        }
        profileViewModel.isLoading.observe(this) { data ->
            if (data) loading.show()
            else {
                loading.cancel()
                dialogChangePassword?.cancel()
                dialogChangeProfile?.cancel()
            }
        }
        profileViewModel.errorMessage.observe(this) { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
        profileViewModel.logoutSuccess.observe(this) { data ->
            if (data) {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}
