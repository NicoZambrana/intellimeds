package com.example.intellimeds;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MedicamentoDB {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public MedicamentoDB(Context context, String dbName) {
        dbHelper = DBHelper.getInstance(context, dbName);
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

    //Método para buscar un medicamento mediante el id
    @SuppressLint("Range")
    public Medicamento obtenerMedicamentoPorId(int id) {
        String[] columns = {
                MedsContract.MedsEntry.COLUMN_ID,
                MedsContract.MedsEntry.COLUMN_NOMBRE,
                MedsContract.MedsEntry.COLUMN_DOSIS,
                MedsContract.MedsEntry.COLUMN_HORARIO,
                MedsContract.MedsEntry.COLUMN_DIAS,
                MedsContract.MedsEntry.COLUMN_TOMADO
        };
        String where = MedsContract.MedsEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        Cursor cursor = db.query(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, columns, where, whereArgs, null, null, null);
        Log.d("MedicamentoDB", "Número de filas encontradas: " + cursor.getCount());

        Medicamento medicamento = null;

        if (cursor.moveToFirst()) {
            Log.d("MedicamentoDB", "Medicamento encontrado: " + cursor.getString(cursor.getColumnIndex(MedsContract.MedsEntry.COLUMN_NOMBRE)));
            medicamento = new Medicamento(
                    cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_NOMBRE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_DOSIS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_HORARIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_DIAS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MedsContract.MedsEntry.COLUMN_TOMADO)) == 1
            );
        } else {
            Log.d("MedicamentoDB", "No se encontró medicamento con ID: " + id);
        }
        cursor.close();
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

    //Modificar el medicamento
    public void modificarMedicamento(int id, String nombre, int dosis, String horario, String dias, boolean tomado) {
        ContentValues values = new ContentValues();
        values.put(MedsContract.MedsEntry.COLUMN_NOMBRE, nombre);
        values.put(MedsContract.MedsEntry.COLUMN_DOSIS, dosis);
        values.put(MedsContract.MedsEntry.COLUMN_HORARIO, horario);
        values.put(MedsContract.MedsEntry.COLUMN_DIAS, dias);
        values.put(MedsContract.MedsEntry.COLUMN_TOMADO, tomado ? 1 : 0);
        Log.d("modificarMedicamento","horario: "+horario+", dias: "+dias);
        String where = MedsContract.MedsEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        int filasActualizadas = db.update(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, values, where, whereArgs);

        if (filasActualizadas > 0) {
            System.out.println("Medicamento actualizado correctamente");
        } else {
            System.out.println("No se encontró el medicamento para actualizar");
        }
    }

    // Método para eliminar un medicamento por su ID
    public void eliminarMedicamento(int id) {
        String whereClause = MedsContract.MedsEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    public void checkAllMedicamentos() {
        Cursor cursor = db.query(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MedsContract.MedsEntry.COLUMN_ID));
            String nombre = cursor.getString(cursor.getColumnIndex(MedsContract.MedsEntry.COLUMN_NOMBRE));
            Log.d("DB_CHECK", "ID: " + id + ", Nombre: " + nombre);
        }
        cursor.close();
    }


    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
    public List<Medicamento> obtenerMedicamentosPorHorarioYDia(String horario, String dia) {
        List<Medicamento> lista = new ArrayList<>();
        // Convert both the COLUMN_DIAS and the 'dia' parameter to lowercase for case-insensitive comparison
        String where = MedsContract.MedsEntry.COLUMN_HORARIO + " = ? AND LOWER(" + MedsContract.MedsEntry.COLUMN_DIAS + ") LIKE ?";
        String[] whereArgs = {horario, "%" + dia.toLowerCase() + "%"}; // Convert 'dia' to lowercase
        //Log.d("obtenerMedPorHorayDia","Horario a buscar: "+horario+", Dia a buscar: "+dia);
        Cursor cursor = db.query(MedsContract.MedsEntry.TABLE_MEDICAMENTOS, null, where, whereArgs, null, null, null);

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


}
