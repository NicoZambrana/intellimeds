package com.example.intellimeds;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ReminderReceiver", "onReceive called"); // Debug log

        // Verifica si tiene el permiso de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ReminderReceiver", "Permission not granted for notifications");

            return;
        }
        // Obtener información del medicamento desde el Intent
        String medicamentoNombre = intent.getStringExtra("medicamentoNombre");
        String medicamentoDosis = intent.getStringExtra("medicamentoDosis");
        Log.d("ReminderReceiver", "Notification triggered for: " + medicamentoNombre);

        // Crear el canal de notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("meds_channel", "Medicamentos", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager.getNotificationChannel("meds_channel") == null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "meds_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.alert_meds))
                .setContentText(context.getString(R.string.medicine_name) + ": " + medicamentoNombre + "\n" +
                        context.getString(R.string.dose) + ": " + medicamentoDosis)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Mostrar la notificación
        notificationManager.notify(1, builder.build());
    }
}