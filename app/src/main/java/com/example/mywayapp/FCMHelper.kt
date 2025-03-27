package com.example.mywayapp

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FCMHelper(context: Context) {
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)

    fun sendNotification(token: String, title: String, body: String) {
        val json = JSONObject().apply {
            put("token", token)
            put("title", title)
            put("body", body)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, "https://api-bjnxp6q43q-uc.a.run.app/send", json,
            { response -> Log.d("FCM", "Respuesta: $response") },
            { error -> Log.e("FCM", "Error: ${error.message}") }
        )

        requestQueue.add(request)
    }
}
