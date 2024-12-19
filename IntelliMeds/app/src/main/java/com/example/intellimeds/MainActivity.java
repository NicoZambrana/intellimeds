package com.example.intellimeds;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkMedicationReminder();
                handler.postDelayed(this, 10000); // Check every 10 seconds
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
                showMedicationReminder(medicamento);
            }
        }
    }

    private void showMedicationReminder(Medicamento medicamento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_meds))
                .setMessage(getString(R.string.medicine_name) + ": " + medicamento.getNombre() + "\n" +
                        getString(R.string.dose) + ": " + medicamento.getDosis())
                .setPositiveButton("OK", (dialog, which) -> {
                    acknowledgedReminders.add(medicamento.getId());
                    dialog.dismiss();
                });
        builder.show();
    }


}
