package com.example.intellimeds;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context, String dbName) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext(), dbName);
        }
        return instance;
    }


    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MedsContract.MedsEntry.TABLE_MEDICAMENTOS + " (" +
                    MedsContract.MedsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MedsContract.MedsEntry.COLUMN_NOMBRE + " TEXT, " +
                    MedsContract.MedsEntry.COLUMN_DOSIS + " INTEGER, " +
                    MedsContract.MedsEntry.COLUMN_HORARIO + " TEXT, " +
                    MedsContract.MedsEntry.COLUMN_DIAS + " TEXT, " +
                    MedsContract.MedsEntry.COLUMN_TOMADO + " INTEGER DEFAULT 0);";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MedsContract.MedsEntry.TABLE_MEDICAMENTOS;

    private DBHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Este método será invocado al establecer la conexión con la BD
        // en el caso de que la creación de la BD sea necesaria
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Este método será invocado al establecer la conexión con la BD
        // en el caso de que la versión de la BD almacenada sea inferior a
        // la versión de la BD que queremos abrir (especificada por
        // DATABASE_VERSION proporcionada en el constructor de la clase)
        //
        // Una política de actualización simple: eliminar los datos almacenados
        // y comenzar de nuevo con una BD vacía
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Este método será invocado al establecer la conexión con la BD
        // en el caso de que la versión de la BD almacenada sea superior a
        // la versión de la BD que queremos abrir (especificada por
        // DATABASE_VERSION proporcionada en el constructor de la clase)
        //
        // Una política de actualización simple: eliminar los datos almacenados
        // y comenzar de nuevo con una BD vacía
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
