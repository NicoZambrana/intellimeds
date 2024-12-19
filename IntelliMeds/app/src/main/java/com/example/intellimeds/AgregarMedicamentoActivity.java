package com.example.intellimeds;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        if (tbLunes.isChecked()) diasSeleccionados.append(getString(R.string.monday_short)).append(", ");
        if (tbMartes.isChecked()) diasSeleccionados.append(getString(R.string.tuesday_short)).append(", ");
        if (tbMiercoles.isChecked()) diasSeleccionados.append(getString(R.string.wednesday_short)).append(", ");
        if (tbJueves.isChecked()) diasSeleccionados.append(getString(R.string.thursday_short)).append(", ");
        if (tbViernes.isChecked()) diasSeleccionados.append(getString(R.string.friday_short)).append(", ");
        if (tbSabado.isChecked()) diasSeleccionados.append(getString(R.string.saturday_short)).append(", ");
        if (tbDomingo.isChecked()) diasSeleccionados.append(getString(R.string.sunday_short)).append(", ");

        // Quitar la última coma y espacio
        if (diasSeleccionados.length() > 0) {
            diasSeleccionados.setLength(diasSeleccionados.length() - 2);
        }

        return diasSeleccionados.toString();
    }

    // Método para establecer los días seleccionados en los ToggleButton
    private void establecerDiasSeleccionados(String dias) {
        if (dias.contains(getString(R.string.monday_short))) tbLunes.setChecked(true);
        if (dias.contains(getString(R.string.tuesday_short))) tbMartes.setChecked(true);
        if (dias.contains(getString(R.string.wednesday_short))) tbMiercoles.setChecked(true);
        if (dias.contains(getString(R.string.thursday_short))) tbJueves.setChecked(true);
        if (dias.contains(getString(R.string.friday_short))) tbViernes.setChecked(true);
        if (dias.contains(getString(R.string.saturday_short))) tbSabado.setChecked(true);
        if (dias.contains(getString(R.string.sunday_short))) tbDomingo.setChecked(true);
    }

    private void guardarMedicamento() {
        String nombre = etNombre.getText().toString().trim();
        String dosisStr = etDosis.getText().toString().trim();
        String horario = selectedTimeText.getText().toString().trim();
        String dias = obtenerDiasSeleccionados();
        //Log.d("Agregar med","dias: "+dias);
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

