package com.example.intellimeds;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class AgregarMedicamentoActivity extends AppCompatActivity {

    private EditText etNombre, etDosis;

    //Referencia al editor de Hora
    private LinearLayout timePickerButtonContainer;
    private TextView selectedTimeText;

    // Referencia a los ToggleButton
    private ToggleButton tbLunes, tbMartes, tbMiercoles, tbJueves, tbViernes, tbSabado, tbDomingo;
    private Button btnGuardar;
    private MedicamentoDB medicamentoDB;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento);
        medicamentoDB = new MedicamentoDB(this, "MedicamentosDB");

        // Referencias a los elementos de la vista
        etNombre = findViewById(R.id.et_nombre);
        etDosis = findViewById(R.id.et_dosis);

        //Referencia al editor de Hora
        timePickerButtonContainer = findViewById(R.id.timePickerButtonContainer);
        selectedTimeText = findViewById(R.id.selectedTimeText);

        timePickerButtonContainer.setOnClickListener(v -> {
            // Crear el MaterialTimePicker
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setHour(12)
                    .setMinute(0)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();

            materialTimePicker.addOnPositiveButtonClickListener(view -> {
                // Obtener la hora y el minuto seleccionados
                int hour = materialTimePicker.getHour();
                int minute = materialTimePicker.getMinute();

                String time = String.format("%02d:%02d", materialTimePicker.getHour(), materialTimePicker.getMinute());
                if (time != null) {
                    selectedTimeText.setText(time);
                }

            });

            // Mostrar el TimePicker
            materialTimePicker.show(getSupportFragmentManager(), "TIME_PICKER");
        });


        // Referencias a los Días
        tbLunes = findViewById(R.id.tb_lunes);
        tbMartes = findViewById(R.id.tb_martes);
        tbMiercoles = findViewById(R.id.tb_miercoles);
        tbJueves = findViewById(R.id.tb_jueves);
        tbViernes = findViewById(R.id.tb_viernes);
        tbSabado = findViewById(R.id.tb_sabado);
        tbDomingo = findViewById(R.id.tb_domingo);
        //Botón guardar
        btnGuardar = findViewById(R.id.btn_guardar);

        //si se le pasa un medicamento para modificar
        int medicamentoId = getIntent().getIntExtra("medicamento_id", -1);
        if (medicamentoId != -1) {
            Medicamento medicamento = medicamentoDB.obtenerMedicamentoPorId(medicamentoId);
            if (medicamento != null) {
                etNombre.setText(medicamento.getNombre());
                etDosis.setText(String.valueOf(medicamento.getDosis()));
                selectedTimeText.setText(medicamento.getHorario());
                establecerDiasSeleccionados(medicamento.getDias());
            }
        }

        btnGuardar.setOnClickListener(v -> {
            if (medicamentoId != -1) {
                modificarMedicamento(medicamentoId);
            } else {
                guardarMedicamento();
            }
        });
    }

    // Método para obtener los días seleccionados
    private String obtenerDiasSeleccionados() {
        StringBuilder diasSeleccionados = new StringBuilder();

        if (tbLunes.isChecked()) diasSeleccionados.append("Lunes, ");
        if (tbMartes.isChecked()) diasSeleccionados.append("Martes, ");
        if (tbMiercoles.isChecked()) diasSeleccionados.append("Miércoles, ");
        if (tbJueves.isChecked()) diasSeleccionados.append("Jueves, ");
        if (tbViernes.isChecked()) diasSeleccionados.append("Viernes, ");
        if (tbSabado.isChecked()) diasSeleccionados.append("Sábado, ");
        if (tbDomingo.isChecked()) diasSeleccionados.append("Domingo, ");

        // Quitar la última coma y espacio
        if (diasSeleccionados.length() > 0) {
            diasSeleccionados.setLength(diasSeleccionados.length() - 2);
        }

        return diasSeleccionados.toString();
    }

    // Método para establecer los días seleccionados en los ToggleButton
    private void establecerDiasSeleccionados(String dias) {
        if (dias.contains("Lunes") || dias.contains("Monday")) tbLunes.setChecked(true);
        if (dias.contains("Martes") || dias.contains("Tuesday")) tbMartes.setChecked(true);
        if (dias.contains("Miércoles") || dias.contains("Wednesday")) tbMiercoles.setChecked(true);
        if (dias.contains("Jueves") || dias.contains("Thursday")) tbJueves.setChecked(true);
        if (dias.contains("Viernes") || dias.contains("Friday")) tbViernes.setChecked(true);
        if (dias.contains("Sábado") || dias.contains("Saturday")) tbSabado.setChecked(true);
        if (dias.contains("Domingo") || dias.contains("Sunday")) tbDomingo.setChecked(true);
    }

    private void guardarMedicamento() {
        String nombre = etNombre.getText().toString().trim();
        String dosisStr = etDosis.getText().toString().trim();
        String horario = selectedTimeText.getText().toString().trim();
        String dias = obtenerDiasSeleccionados();

        // Validar entrada
        if (nombre.isEmpty() || dosisStr.isEmpty() || horario.isEmpty() || dias.isEmpty()) {
            Toast.makeText(this, getString(R.string.incomplete_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        int dosis = Integer.parseInt(dosisStr);

        // Guardar el medicamento en la base de datos
        medicamentoDB.addMedicamento(nombre, dosis, horario, dias);

        // Confirmar y regresar
        Toast.makeText(this, getString(R.string.create_successfull), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
private void modificarMedicamento(int id) {
    String nombre = etNombre.getText().toString().trim();
    String dosisStr = etDosis.getText().toString().trim();
    String horario = selectedTimeText.getText().toString().trim();
    String dias = obtenerDiasSeleccionados();

    if (nombre.isEmpty() || dosisStr.isEmpty() || horario.isEmpty() || dias.isEmpty()) {
        Toast.makeText(this, getString(R.string.incomplete_fields), Toast.LENGTH_SHORT).show();
        return;
    }

    int dosis = Integer.parseInt(dosisStr);
    medicamentoDB.modificarMedicamento(id, nombre, dosis, horario, dias, false);
    Toast.makeText(this, getString(R.string.modify_successfull), Toast.LENGTH_SHORT).show();
    // Regresar a MainActivity
    Intent intent = new Intent(AgregarMedicamentoActivity.this, MainActivity.class);
    setResult(RESULT_OK, intent);
    startActivity(intent);
    finish();  // Finaliza la actividad actual
}
}

