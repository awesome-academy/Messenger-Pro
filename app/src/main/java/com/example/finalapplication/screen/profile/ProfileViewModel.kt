package com.example.finalapplication.screen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.finalapplication.data.model.User
import com.example.finalapplication.data.repository.UserRepository
import com.example.finalapplication.data.repository.resource.Listenner
import com.example.finalapplication.utils.Constant
import com.example.finalapplication.utils.NumberConstant
import com.example.finalapplication.utils.base.BaseViewModel

class ProfileViewModel(private val userRepository: UserRepository) : BaseViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean>
        get() = _logoutSuccess

    init {
        launchTask<User>(onRequest = {
            userRepository.getUser(null, object : Listenner<User> {

                override fun onError(msg: String) {
                    message.value = msg
                }

                override fun onSuccess(result: User) {
                    _user.value = result
                }
            })
        }, isShowLoading = false)
    }

    fun logout() {
        userRepository.logout(object : Listenner<Boolean> {
            override fun onSuccess(data: Boolean) {
                _logoutSuccess.value = data
            }

            override fun onError(msg: String) {
                // TODO("Not yet implemented")
            }

        })
    }

    fun updateUser(user: User, name: String) {
        if (name.isEmpty()) {
            message.value = Constant.ERROR_NAME_EMPTY
            return
        }
        launchTask<Boolean>(onRequest = {
            userRepository.updateProfile(user, object : Listenner<Boolean> {
                override fun onSuccess(result: Boolean) {
                    loading.value = false
                }

                override fun onError(msg: String) {
                    message.value = msg
                }

            })
        })
    }

    fun updatePassword(
        user: User,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (oldPassword != user.userAccount?.password) {
            message.value = Constant.ERROR_INCORECT_PASSWORD
            return
        }
        if (newPassword.length < NumberConstant.LENGTH_MIN_PASSWORD || newPassword != confirmPassword) {
            message.value = Constant.ERROR_PASSWORD_SHORT + "\n" + Constant.ERROR_CONFIRM_PASSWORD
            return
        }
        user.userAccount?.let { it.password = newPassword }
        launchTask<Boolean>(onRequest = {
            userRepository.updatePassword(user, object : Listenner<Boolean> {
                override fun onSuccess(result: Boolean) {
                    loading.value = false
                }

                override fun onError(msg: String) {
                    message.value = msg
                }
            })
        })
    }

    fun updateAvatar(user: User) {
        launchTask<Boolean>(onRequest = {
            userRepository.updateAvatar(user, object : Listenner<Boolean> {
                override fun onSuccess(result: Boolean) {
                    loading.value = false
                }

                override fun onError(msg: String) {
                    message.value = msg
                }
            })
        })
    }
}
