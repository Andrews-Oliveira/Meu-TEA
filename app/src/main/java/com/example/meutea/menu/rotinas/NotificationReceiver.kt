package com.example.meutea.menu.rotinas



import android.content.BroadcastReceiver
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.meutea.R
import com.example.meutea.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val atividade = intent.getStringExtra("atividade") ?: "Atividade programada"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()

        // Criar um canal de notificação (necessário para Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "rotina_channel",
                "Rotina Notificações",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir o app quando a notificação for clicada
        val intentMain = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intentMain, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Criando o som da notificação
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Construindo a notificação
        val notification = NotificationCompat.Builder(context, "rotina_channel")
            .setSmallIcon(R.drawable.ic_notificartion) // Adicione um ícone apropriado em `res/drawable`
            .setContentTitle("Lembrete de Rotina")
            .setContentText(atividade)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .build()

        // No Android 13+ a permissão é exigida em tempo de execução.
        // O módulo de rotinas deve solicitá-la ao usuário quando for ativado.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificationId, notification)
    }
}
