package com.example.finalapplication.screen.main

import com.example.finalapplication.data.model.User
import com.example.finalapplication.data.repository.UserRepository
import com.example.finalapplication.utils.base.BaseViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class MainViewModel(val userRepository: UserRepository) : BaseViewModel() {

    fun updateStatus(status: Boolean) {
        launchTask<User>(onRequest = {
            withContext(NonCancellable) {
                userRepository.updateStatus(status)
            }
        })
    }
}
