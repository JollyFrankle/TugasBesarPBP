package com.example.tugasbesarpbp.api.http

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.ApiErrorListener
import com.example.tugasbesarpbp.api.models.Review
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class ReviewApi {
    companion object {
        private const val BASE_URL = "http://20.168.252.140/UGD_PBP/public/api/"

        private const val GET_BY_KOST_ID_URL = BASE_URL + "review/kost/"
        private const val GET_BY_USER_ID_URL = BASE_URL + "review/user/"
        private const val GET_BY_ID_URL = BASE_URL + "review/"
        private const val ADD_URL = BASE_URL + "review/"
        private const val UPDATE_URL = BASE_URL + "review/"
        private const val DELETE_URL = BASE_URL + "review/"

        fun getAllByIdKost(context: Context, kostId: Long, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                "$GET_BY_KOST_ID_URL$kostId",
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

        fun getAllByIdUser(context: Context, userId: Long, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                "$GET_BY_USER_ID_URL$userId",
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

        fun getLoggedInUserReviewByKostId(context: Context, id_kost: Long, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                "$GET_BY_ID_URL$id_kost",
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

        fun createReview(context: Context, id_kost: Long, review: Review, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                "$ADD_URL${id_kost}",
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
                    val json = gson.toJson(review)
                    return json.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            queue.add(stringRequest)
        }

        fun updateReview(context: Context, id: Long, review: Review, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.PUT,
                "$UPDATE_URL${id}",
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
                    val json = gson.toJson(review)
                    return json.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            queue.add(stringRequest)
        }

        fun deleteReview(context: Context, reviewId: Long, responseListener: Response.Listener<String>, errorResponse: (result: ApiErrorListener) -> Unit = { ApiErrorListener.defaultErrorListener(context) }) {
            val queue = Volley.newRequestQueue(context)
            val token = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("token", "")!!

            val stringRequest: StringRequest = object : StringRequest(
                Method.DELETE,
                "$DELETE_URL$reviewId",
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