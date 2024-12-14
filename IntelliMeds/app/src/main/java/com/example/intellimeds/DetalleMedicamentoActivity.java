package com.example.intellimeds;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetalleMedicamentoActivity extends AppCompatActivity {
    private Medicamento medicamento; // Para almacenar los datos del medicamento

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_medicamento);

        // Referencias a los TextViews y el botón
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvDosis = findViewById(R.id.tvDosis);
        TextView tvHorario = findViewById(R.id.tvHorario);
        TextView tvDias = findViewById(R.id.tvDias);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        // Recuperar el ID del medicamento desde el intent
        int medicamentoId = getIntent().getIntExtra("medicamento_id", -1);

        if (medicamentoId == -1) {
            // Si no hay un ID válido, muestra un mensaje y finaliza la actividad
            Toast.makeText(this, "No se encontró el medicamento", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener el medicamento desde la base de datos
        MedicamentoDB medicamentoDB = new MedicamentoDB(this, "medicamentos.db");
        medicamento = medicamentoDB.obtenerMedicamentoPorId(medicamentoId);

        if (medicamento != null) {
            // Mostrar los datos del medicamento
            tvNombre.setText("Nombre: " + medicamento.getNombre());
            tvDosis.setText("Dosis: " + medicamento.getDosis());
            tvHorario.setText("Horario: " + medicamento.getHorario());
            tvDias.setText("Días: " + medicamento.getDias());
        } else {
            Toast.makeText(this, "El medicamento no existe", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar el botón de eliminar
        btnEliminar.setOnClickListener(v -> {
            // Eliminar el medicamento de la base de datos
            medicamentoDB.eliminarMedicamento(medicamento.getId());

            // Mostrar un mensaje
            Toast.makeText(this, "Medicamento eliminado", Toast.LENGTH_SHORT).show();

            // Preparar el resultado para la actividad principal
            Intent intent = new Intent();
            setResult(RESULT_OK, intent); // Indicar que la actividad ha finalizado correctamente

            // Finalizar la actividad y volver a la actividad principal
            finish();
        });
    }
}
