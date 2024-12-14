package com.example.intellimeds;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AgregarMedicamentoActivity extends AppCompatActivity {

    private EditText etNombre, etDosis, etHorario, etDias;
    private Button btnGuardar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento);

        // Referencias a los elementos de la vista
        etNombre = findViewById(R.id.et_nombre);
        etDosis = findViewById(R.id.et_dosis);
        etHorario = findViewById(R.id.et_horario);
        etDias = findViewById(R.id.et_dias);
        btnGuardar = findViewById(R.id.btn_guardar);

        // Manejar el clic en el botón Guardar
        btnGuardar.setOnClickListener(v -> guardarMedicamento());
    }

    private void guardarMedicamento() {
        String nombre = etNombre.getText().toString().trim();
        String dosisStr = etDosis.getText().toString().trim();
        String horario = etHorario.getText().toString().trim();
        String dias = etDias.getText().toString().trim();

        // Validar entrada
        if (nombre.isEmpty() || dosisStr.isEmpty() || horario.isEmpty() || dias.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int dosis = Integer.parseInt(dosisStr);

        // Guardar el medicamento en la base de datos
        MedicamentoDB medicamentoDB = new MedicamentoDB(this, "MedicamentosDB");
        medicamentoDB.addMedicamento(nombre, dosis, horario, dias);

        // Confirmar y regresar
        Toast.makeText(this, "Medicamento añadido correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}

