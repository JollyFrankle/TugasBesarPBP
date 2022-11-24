package com.example.tugasbesarpbp.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.example.tugasbesarpbp.MainActivity
import org.json.JSONObject

class ApiErrorListener(val jsonData: JSONObject, val statusCode: Int) {
    companion object {
        // Error response listener
        fun errorListener(context: Context, myCallback: (result: ApiErrorListener) -> Unit = { defaultErrorListener(context) }) = Response.ErrorListener {
                // Kalau ada error
                val respObj: JSONObject
                try {
                    respObj = JSONObject(String(it.networkResponse.data))

                    if (it.networkResponse.statusCode == 401) {
                        // Kalau token expired
                        Toast.makeText(
                            context,
                            "Token expired, silahkan login kembali",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Hapus session
                        val spSession =
                            context.getSharedPreferences("session", Context.MODE_PRIVATE)
                        spSession.edit().clear().apply()
                        // Kembali ke halaman login
                        context.startActivity(Intent(context, MainActivity::class.java))
                        if (context is Activity) {
                            context.finish()
                        }
                    } else {
                        // Kalau error lain
                        myCallback(ApiErrorListener(respObj, it.networkResponse.statusCode))
                    }
                } catch (e: Exception) {
                    val response = it.networkResponse
                    var dialogContent = ""
                    if (response != null) {
                        dialogContent = "Error ${response.statusCode}\r\nHubungi admin."
                    } else {
                        dialogContent = "Tidak dapat terhubung ke server.\r\nPeriksa koneksi internet."
                    }
                    AlertDialog.Builder(context)
                        .setMessage(dialogContent)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }

        fun defaultErrorListener(context: Context) = errorListener(context) {
            val dialogContent = "Error ${it.statusCode}\r\nHubungi admin."
            AlertDialog.Builder(context)
                .setMessage(dialogContent)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}