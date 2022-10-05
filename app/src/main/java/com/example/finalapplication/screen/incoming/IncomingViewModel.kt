package com.example.finalapplication.screen.incoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.finalapplication.data.model.DataNotification
import com.example.finalapplication.data.model.Notification
import com.example.finalapplication.data.model.User
import com.example.finalapplication.data.repository.NotificationRepository
import com.example.finalapplication.data.repository.UserRepository
import com.example.finalapplication.data.repository.resource.Listenner
import com.example.finalapplication.utils.FcmConstant
import com.example.finalapplication.utils.base.BaseViewModel

class IncomingViewModel(
    val notificationRepository: NotificationRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _isFinish = MutableLiveData<Boolean>()
    val isFinish: LiveData<Boolean>
        get() = _isFinish

    init {
        launchTask<User>(onRequest = {
            userRepository.getUser(null, object : Listenner<User> {
                override fun onSuccess(data: User) {
                    _user.postValue(data)
                }

                override fun onError(msg: String) {
                    // TODO("Not yet implemented")
                }
            })
        })
    }

    fun sendNotification(currentUser: User?, inviterToken: String, type: String?) {
        val dataNotification = DataNotification(
            currentUser?.id.toString(),
            currentUser?.name.toString(),
            currentUser?.avatar.toString(),
            currentUser?.fcmToken.toString(),
            FcmConstant.RESPOND_INVITATION,
            type
        )
        val token = listOf(inviterToken)
        val notification = Notification(dataNotification, token)
        launchTask<Boolean>(onRequest = {
            notificationRepository.sendNotification(
                notification,
                object : Listenner<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        if (type == FcmConstant.DENY_INVITATION) _isFinish.postValue(true)
                    }

                    override fun onError(msg: String) {
                        message.value = msg
                    }

                })
        })
    }
}
