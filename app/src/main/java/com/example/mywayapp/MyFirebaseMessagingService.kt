package com.example.mywayapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Obtener el título y el cuerpo de la notificación
        val title = remoteMessage.notification?.title ?: "Notificación"
        val body = remoteMessage.notification?.body ?: "Tienes una nueva notificación."

        // Crear una notificación para mostrar cuando la app está en primer plano
        sendNotification(title, body)
    }

    private fun sendNotification(title: String, body: String) {
        val channelId = "default_channel"

        // Crear un canal de notificación (solo necesario en Android 8.0 o superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canal de notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal predeterminado"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Crear un PendingIntent que abrirá la aplicación al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Usar la hora actual para generar un ID único de notificación
        val notificationId = (System.currentTimeMillis() % 10000).toInt()

        // Crear la notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.logoapp_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup("group_key") // Asignar todas las notificaciones a un grupo
            .build()

        // Crear la notificación resumen (opcional, solo si deseas un contador de notificaciones)
        val summaryNotification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Notificaciones nuevas")
            .setContentText("Tienes nuevas notificaciones")
            .setSmallIcon(R.mipmap.logoapp_foreground)
            .setGroup("group_key")
            .setGroupSummary(true) // Esta es la notificación resumen
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Mostrar la notificación normal
        try {
            NotificationManagerCompat.from(this).notify(notificationId, notification)
            NotificationManagerCompat.from(this)
                .notify(0, summaryNotification)  // Resumen de notificaciones
        } catch (e: SecurityException) {
            Log.e("FCM", "Permiso denegado para notificaciones: ${e.message}")
        }
    }

}
