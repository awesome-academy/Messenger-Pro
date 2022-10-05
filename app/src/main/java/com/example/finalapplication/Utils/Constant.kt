package com.example.finalapplication.utils

import java.util.*

object Constant {
    const val ERROR_USER_NOT_CONNECT = "Can't be contacted"
    const val WARNING_ = "Warning!"
    const val ERROE_EMAIL_EMPTY = "enter your  email to reset password"
    const val NO_INTERNET = "No Internet, Please check!!"
    const val ROLE_CLIENT = "client"
    const val ROLE_STAFF = "staff"
    const val MSG_NO_DATA = "No data founded"
    const val ERROR_ = "Have error, please try late"
    const val ERROR_EMAIL_EXTIST = "Account Existed"
    const val ERROR_PASSWORD_SHORT = "Password need more 8 charactor"
    const val ERROR_CONFIRM_PASSWORD = "Confirm password different password"
    const val ERROR_ACCOUNT = "Account Incorrect"
    const val ERROR_USER = "Please Login"
    const val ERROR_NAME_EMPTY = "Name cannot be empty"
    const val ERROR_VALIDATE_MAIL = "Email is not validate"
    const val ERROR_INCORECT_PASSWORD = "Incorrect password"
    const val ERROR_ACCOUNT_EXIST = "Account Existed"
    const val MSG_SIGN_IN = "Sign in ...."
    const val MSG_CHECK_MAIL = "Please check your mail to get link reset password"
    const val MSG_UPDATE = "Update..."
    const val RECIVER = "cid"
    const val TYPE_IMAGE = "image/*"
    const val EXTENSION_IMAGE = ".jpg"
    const val PROVIDER = ".provider"
}

fun getNewid() = UUID.randomUUID()
