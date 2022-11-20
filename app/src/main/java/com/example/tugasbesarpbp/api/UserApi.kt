package com.example.tugasbesarpbp.api

class UserApi {
    companion object {
        private val BASE_URL = "http://20.168.252.140/UGD_PBP/public/api/"

        val LOGIN_URL = BASE_URL + "login"
        val REGISTER_URL = BASE_URL + "register"
        var LOGOUT_URL = BASE_URL + "logout"

        val GET_URL = BASE_URL + "profile"
        val UPDATE_URL = BASE_URL + "profile"
    }
}