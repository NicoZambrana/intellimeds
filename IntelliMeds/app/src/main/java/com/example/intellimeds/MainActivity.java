package com.example.intellimeds;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentoAdapter adapter;
    private MedicamentoDB medicamentoDB;

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
            startActivityForResult(intent, 2); // Request code 2 para ver detalles (y posiblemente eliminar)
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                // Recargar la lista de medicamentos tras la eliminación
                List<Medicamento> medicamentos = medicamentoDB.obtenerTodosMedicamentos();
                adapter = new MedicamentoAdapter(medicamentos, medicamento -> {
                    // Acción al hacer clic en un medicamento (opcional)
                });
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar la base de datos para evitar fugas de memoria
        try {
            medicamentoDB.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
