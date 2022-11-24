package com.example.tugasbesarpbp.api.http

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.ApiErrorListener
import com.example.tugasbesarpbp.api.models.User
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class UserApi {
    companion object {
        private const val BASE_URL = "http://20.168.252.140/UGD_PBP/public/api/"

        const val LOGIN_URL = BASE_URL + "login"
        const val REGISTER_URL = BASE_URL + "register"
        const val LOGOUT_URL = BASE_URL + "logout"

        const val GET_URL = BASE_URL + "profile"
        const val UPDATE_URL = BASE_URL + "profile"
        const val CHECK_URL = BASE_URL + "auth"

        fun cekLogin(context: Context, username: String, password: String, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                LOGIN_URL,
                responseListener,
                ApiErrorListener.errorListener(context, errorResponse)
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["password"] = password
                    return params
                }
            }
            queue.add(stringRequest)
        }

        fun register(context: Context, user: User, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                REGISTER_URL,
                responseListener,
                ApiErrorListener.errorListener(context, errorResponse)
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val json = gson.toJson(user)
                    return json.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            queue.add(stringRequest)
        }

        fun getProfile(context: Context, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                GET_URL,
                responseListener,
                ApiErrorListener.errorListener(context, errorResponse)
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }
            queue.add(stringRequest)
        }

        fun updateProfile(context: Context, user: User, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.PUT,
                UPDATE_URL,
                responseListener,
                ApiErrorListener.errorListener(context, errorResponse)
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }

                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val json = gson.toJson(user)
                    return json.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            queue.add(stringRequest)
        }

        fun logout(context: Context, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                LOGOUT_URL,
                responseListener,
                ApiErrorListener.errorListener(context, errorResponse)
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }
            queue.add(stringRequest)
        }

        fun checkToken(context: Context, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                CHECK_URL,
                responseListener,
                ApiErrorListener.errorListener(context, errorResponse)
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }
            queue.add(stringRequest)
        }
    }
}