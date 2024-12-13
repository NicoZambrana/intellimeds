package com.example.intellimeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MedicamentoDB {
    private SQLiteDatabase db;

    public MedicamentoDB(Context context, String dbName) {
        DBHelper dbHelper = new DBHelper(context, dbName);
        db = dbHelper.getWritableDatabase();
    }

    //Método para añadir un nuevo medicamento
    public void addMedicamento(String nombre, int dosis, String horario, String dias) {
        ContentValues values = new ContentValues();
        values.put(MedsContract.MedsEntry.COLUMN_NOMBRE, nombre);
        values.put(MedsContract.MedsEntry.COLUMN_DOSIS, dosis);
        values.put(MedsContract.MedsEntry.COLUMN_HORARIO, horario);
        values.put(MedsContract.MedsEntry.COLUMN_DIAS, dias);
        values.put(MedsContract.MedsEntry.COLUMN_TOMADO, 0);
        db.insert(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, null, values);
    }

    //Método para buscar un medicamento mediante el nombre
    public Medicamento buscarMedicamento(String nombre) {
        String[] columns = {
                MedsContract.MedsEntry.COLUMN_ID,
                MedsContract.MedsEntry.COLUMN_NOMBRE,
                MedsContract.MedsEntry.COLUMN_DOSIS,
                MedsContract.MedsEntry.COLUMN_HORARIO,
                MedsContract.MedsEntry.COLUMN_DIAS,
                MedsContract.MedsEntry.COLUMN_TOMADO
        };
        String where = MedsContract.MedsEntry.COLUMN_NOMBRE + " = ?";
        String[] whereArgs = {nombre};

        Cursor cursor = db.query(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, columns, where, whereArgs, null, null, null);

        Medicamento medicamento = null;
        try {
            if (cursor.moveToFirst()) {
                medicamento = new Medicamento(
                        cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_NOMBRE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_DOSIS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_HORARIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_DIAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_TOMADO)) == 1
                );
            }
        } finally {
            cursor.close();
        }
        return medicamento;
    }

    // Método para obtener todos los medicamentos
    public List<Medicamento> obtenerTodosMedicamentos() {
        List<Medicamento> lista = new ArrayList<>();
        Cursor cursor = db.query(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Medicamento medicamento = new Medicamento(
                    cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_NOMBRE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_DOSIS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_HORARIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_DIAS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_TOMADO)) == 1
            );
            lista.add(medicamento);
        }
        cursor.close();
        return lista;
    }

    // Cierra la base de datos al finalizar
    @Override
    protected void finalize() throws Throwable {
        db.close();
        super.finalize();
    }

}
