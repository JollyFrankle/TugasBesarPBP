package com.example.tugasbesarpbp.api.http

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.ApiErrorListener
import com.example.tugasbesarpbp.api.models.Kost
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class KostApi {
    companion object {
        private const val BASE_URL = "http://20.168.252.140/UGD_PBP/public/api/"

        const val GET_ALL_URL = BASE_URL + "kost/"
        const val GET_BY_ID_URL = BASE_URL + "kost/"
        const val ADD_URL = BASE_URL + "kost"
        const val UPDATE_URL = BASE_URL + "kost/"
        const val DELETE_URL = BASE_URL + "kost/"

        fun getAllKosts(context: Context, search: String, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                "$GET_ALL_URL?search=$search",
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

        fun createKost(context: Context, kost: Kost, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNTNkMTg0ZTE0MzkzMmUxYjA1MjNkNzJjOGZjZDk3NjVmMDA0ZWFhYWMxNzkyNmFmNjhiODgwNWMzNjU4ZTAwNGZmOTRhZmQyMGYwYzlhZTEiLCJpYXQiOjE2Njk2ODc4MDguNjIxMzE5LCJuYmYiOjE2Njk2ODc4MDguNjIxMzIxLCJleHAiOjE3MDEyMjM4MDguNjAxODcxLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.TUp3BWm-V88oaCaqDPk79J9B3TWllD7Wey33-kiQUkfj4txJ5Hi2G-xLJPamDVCZOj6f8J4Rd-XQ_G16UrLVF-jsFf6S321WIEheu-ae1gjhorsRE16tgzgDz3e7I9uDsmeKe-aZnaCtZjfO3gKdA1ejMQGrLSXRVxnfghiQc5stA4TiBBbVW9EecVky7P2ev4gPWncL4Yy7XQkShcnxXHoYpsIl73-DpcncLtr_LXyPX82rFqOawH5SPGwA3UeahHeCzosmRtPbP7JZm6L9WBAnyCRCWPBZ7MyGIE5iFAD8mEmHdj2u865evTCsN1GqESoYvy_xtJhIzfx7GTUtnbU39BjdBTDuPhWLZBjuhixCcz_wvZy-kmw_9aYvr3f6oLTkOKROjzvRGmwuAGrTizo54bFwZJwVF71i8Ke_z1-rDUBUtr7XGyNy-vWN8A7aR0l8W9d0kxn8fwblSI4rGsh3cg8NeBtbIkeYG6KlUkh_zUJK3lh0nUoC97qcQWfT6Yb4EV8siQtTxUm0APX7fseJM1qX8XQcmAfaHWZGxgHVW-mK0jhs3ObdvtG3SZrqF6ugVwzzLp5cRCSIQ_5QXx8tPSo78lT1mhbojN0IWSEihvtwzz7Os5XatiXu03QI-TQegT2nUlTxIrlEr5Iexiq5362R0iQU9YMvnjff8RE")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                ADD_URL,
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
                    val json = gson.toJson(kost)
                    return json.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            queue.add(stringRequest)
        }

        fun getKostById(context: Context, id: Long, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                GET_BY_ID_URL + id,
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

        fun updateKost(context: Context, id: Long, kost: Kost, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.PUT,
                UPDATE_URL + id,
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
                    val json = gson.toJson(kost)
                    return json.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            queue.add(stringRequest)
        }

        fun deleteKost(context: Context, id: Long, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.DELETE,
                DELETE_URL + id,
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