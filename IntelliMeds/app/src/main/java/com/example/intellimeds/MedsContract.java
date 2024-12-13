package com.example.intellimeds;

import android.provider.BaseColumns;

public final class MedsContract {
    private MedsContract() {
    }

    public static abstract class MedsEntry implements BaseColumns {
        public static final String TABLE_MEDICAMENTOS = "Medicamentos";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_DOSIS = "dosis";
        public static final String COLUMN_HORARIO = "horario";
        public static final String COLUMN_DIAS = "dias";
        public static final String COLUMN_TOMADO = "tomado";
    }
}
