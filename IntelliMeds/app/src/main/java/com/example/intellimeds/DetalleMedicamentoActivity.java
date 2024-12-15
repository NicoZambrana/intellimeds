package com.example.intellimeds;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetalleMedicamentoActivity extends AppCompatActivity {
    private Medicamento medicamento; // Para almacenar los datos del medicamento
    private MedicamentoDB medicamentoDB; // Base de datos
    private TextView tvNombre, tvDosis, tvHorario, tvDias; // Declarar TextViews como variables de clase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_medicamento);

        // Referencias a los TextViews y el botón
         tvNombre = findViewById(R.id.tvNombre);
         tvDosis = findViewById(R.id.tvDosis);
         tvHorario = findViewById(R.id.tvHorario);
         tvDias = findViewById(R.id.tvDias);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnModificar = findViewById(R.id.btnModificar);

        // Recuperar el ID del medicamento desde el intent
        int medicamentoId = getIntent().getIntExtra("medicamento_id", -1);
        // En DetalleMedicamentoActivity, antes de recuperar el medicamento
        Log.d("DetalleMedicamentoActivity", "ID recibido: " + medicamentoId);

        if (medicamentoId == -1) {
            // Si no hay un ID válido, muestra un mensaje y finaliza la actividad
            Toast.makeText(this, "No se encontró el medicamento", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener el medicamento desde la base de datos
        medicamentoDB = new MedicamentoDB(this, "MedicamentosDB");
        medicamento = medicamentoDB.obtenerMedicamentoPorId(medicamentoId);


        if (medicamento != null) {
            // Mostrar los datos del medicamento
            mostrarDetallesMedicamento();

        } else {
            Toast.makeText(this, "El medicamento no existe en bd", Toast.LENGTH_SHORT).show();
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
        btnModificar.setOnClickListener(v -> {
            // Launch AgregarMedicamentoActivity for result
            Intent intent = new Intent(DetalleMedicamentoActivity.this, AgregarMedicamentoActivity.class);
            intent.putExtra("medicamento_id", medicamento.getId());
            startActivityForResult(intent, 3); // Request code 3 for modify medicamento
        });

    }
    // Handle the result in onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                // Reload medicamento details after modification
                medicamento = medicamentoDB.obtenerMedicamentoPorId(medicamento.getId());
                if (medicamento != null) {
                    mostrarDetallesMedicamento();

                } else {
                    Toast.makeText(this, "El medicamento no existe después de la modificación", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
    private void mostrarDetallesMedicamento() {
        tvNombre.setText("Nombre: " + medicamento.getNombre());
        tvDosis.setText("Dosis: " + medicamento.getDosis());
        tvHorario.setText("Horario: " + medicamento.getHorario());
        tvDias.setText("Días: " + medicamento.getDias());
    }
}
