package com.example.intellimeds;

import android.Manifest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentoAdapter adapter;
    private MedicamentoDB medicamentoDB;
    private Set<Integer> acknowledgedReminders;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 2;

    private Handler handler;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Verifica y solicita el permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
                return; // Wait for permission result before scheduling notifications
            }
        }
        // Verificar y solicitar el permiso SCHEDULE_EXACT_ALARM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM);
            }
        }
        // FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            // Acción al hacer clic en el botón flotante
            Intent intent = new Intent(MainActivity.this, AgregarMedicamentoActivity.class);
            startActivityForResult(intent, 1); // Request code 1 para agregar medicamento
        });

        // Inicializar la base de datos
        medicamentoDB = new MedicamentoDB(this, "MedicamentosDB");
        // Inicializar el conjunto de recordatorios reconocidos
        acknowledgedReminders = new HashSet<>();
        // Configurar RecyclerView
        recyclerView = findViewById(R.id.rv_medicamentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener medicamentos de la base de datos
        List<Medicamento> medicamentos = medicamentoDB.obtenerTodosMedicamentos();

        // Configurar el adaptador con los medicamentos
        adapter = new MedicamentoAdapter(medicamentos, medicamento -> {
            // Abrir la actividad de detalle cuando se haga clic en un medicamento
            Intent intent = new Intent(MainActivity.this, DetalleMedicamentoActivity.class);
            intent.putExtra("medicamento_id", medicamento.getId()); // Pasar el ID del medicamento
            // En MainActivity, antes de iniciar la actividad DetalleMedicamentoActivity
            Log.d("MainActivity", "ID del medicamento: " + medicamento.getId());

            startActivityForResult(intent, 2); // Request code 2 para ver detalles (y posiblemente eliminar)
        });
        recyclerView.setAdapter(adapter);
        //medicamentoDB.checkAllMedicamentos(); ---Comprobar elementos de db
        // Check medication reminders periodically
         handler = new Handler(Looper.getMainLooper());
         runnable = new Runnable() {
            @Override
            public void run() {
                checkMedicationReminder();
                handler.postDelayed(this, 60000); // Check every 60 seconds
            }
        };
        handler.post(runnable);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Recargar la lista de medicamentos tras la eliminación
            List<Medicamento> medicamentos = medicamentoDB.obtenerTodosMedicamentos();
            adapter = new MedicamentoAdapter(medicamentos, medicamento -> {
                Intent intent = new Intent(MainActivity.this, DetalleMedicamentoActivity.class);
                intent.putExtra("medicamento_id", medicamento.getId()); // Pasar el ID del medicamento
                // En MainActivity, antes de iniciar la actividad DetalleMedicamentoActivity
                Log.d("MainActivity", "ID del medicamento: " + medicamento.getId());

                startActivityForResult(intent, 2);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable); // Remover los callbacks del Handler
        }
        if (medicamentoDB != null) {
            medicamentoDB.close();
        }
    }

    private void checkMedicationReminder() {
        // Get current time and day
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());

        // Fetch medications from the database
        List<Medicamento> medicamentos = medicamentoDB.obtenerMedicamentosPorHorarioYDia(currentTime, currentDay);

        // Show reminders
        for (Medicamento medicamento : medicamentos) {
            if (!acknowledgedReminders.contains(medicamento.getId())) {
                if (!isFinishing()) {
                    showMedicationReminder(medicamento);
                    scheduleNotification(medicamento); // Llamar al método scheduleNotification

                }
            }
        }
    }

    private void showMedicationReminder(Medicamento medicamento) {
        if (isFinishing()) {
            // Si la actividad está en proceso de finalizar, no mostrar el diálogo
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_meds))
                .setMessage(getString(R.string.medicine_name) + ": " + medicamento.getNombre() + "\n" +
                        getString(R.string.dose) + ": " + medicamento.getDosis())
                .setPositiveButton("OK", (dialog, which) -> {
                    acknowledgedReminders.add(medicamento.getId());
                    dialog.dismiss();
                });
        if (!isFinishing()) {
            builder.show();
        }
    }


    private void scheduleNotification(Medicamento medicamento) {
        Log.d("MainActivity", "Scheduling notification for: " + medicamento.getNombre());

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicamentoNombre", medicamento.getNombre());
        intent.putExtra("medicamentoDosis", String.valueOf(medicamento.getDosis()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, medicamento.getId(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtMillis = System.currentTimeMillis() + 60000; // Programar para 60 segundos (ajusta según sea necesario)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.e("MainActivity", "Exact alarms not permitted");

                return;
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            Log.d("MainActivity", "Alarm set for: " + triggerAtMillis);

        } catch (SecurityException e) {
            Log.e("MainActivity", "Failed to set alarm", e);
            e.printStackTrace();
        }    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso POST_NOTIFICATIONS concedido
            } else {
                // Permiso POST_NOTIFICATIONS denegado
            }
        }
    }



}
