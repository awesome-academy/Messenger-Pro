package com.example.finalapplication.data.repository.resource

import com.example.finalapplication.data.model.Account
import com.example.finalapplication.data.model.User

interface UserDataSource {

    interface Remote {
        suspend fun getUser(id: String?, listen: Listenner<User>)
        suspend fun loginUser(account: Account, listen: Listenner<Boolean>)
        suspend fun registerUser(user: User, listen: Listenner<Boolean>)
        suspend fun updateProfile(user: User, listen: Listenner<Boolean>)
        suspend fun updatePassword(user: User, listen: Listenner<Boolean>)
        suspend fun updateAvatar(user: User, listen: Listenner<Boolean>)
        suspend fun forgotPassword(email: String, listen: Listenner<Boolean>)
        suspend fun getUserByName(name: String, lastIndex: String, listen: Listenner<List<User>>)
        fun updateStatus(status: Boolean)
        fun logout(listen: Listenner<Boolean>)
    }
}
