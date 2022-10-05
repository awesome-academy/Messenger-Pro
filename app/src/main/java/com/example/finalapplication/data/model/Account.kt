package com.example.finalapplication.data.model

import com.example.finalapplication.utils.NumberConstant
import java.io.Serializable

class Account() : Serializable {
    var password: String = ""
    var email: String = ""

    constructor(email: String, password: String) : this() {
        this.email = email
        this.password = password
    }

    fun validateAccount() = email.isEmpty() || password.isEmpty()

    fun validatePassWord(): Boolean {
        if (this.password.length < NumberConstant.LENGTH_MIN_PASSWORD) return false
        return true
    }

    fun validateEmail(): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        return true
    }

    fun validateConfirmPassword(confirm: String): Boolean {
        if (this.password == confirm) return true
        return false
    }

    companion object {
        const val serialVersionUID = 1L
        const val password = "password"
        const val email = "email"
    }
}
