package com.example.intellimeds;

public class Medicamento {

    private int id; // Identificador único (clave primaria)
    private String nombre; // Nombre del medicamento
    private int dosis; // Dosis del medicamento (por ejemplo, en mg)
    private String horario; // Hora de toma (formato HH:mm)
    private String dias; // Días de la semana (como cadena separada por comas)
    private boolean tomado; // Indicador de si se ha tomado

    // Constructor principal
    public Medicamento(int id, String nombre, int dosis, String horario, String dias, boolean tomado) {
        this.id = id;
        this.nombre = nombre;
        this.dosis = dosis;
        this.horario = horario;
        this.dias = dias;
        this.tomado = tomado;
    }

    // Constructor sin ID (para nuevos medicamentos)
    public Medicamento(String nombre, int dosis, String horario, String dias, boolean tomado) {
        this(-1, nombre, dosis, horario, dias, tomado);
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDosis() {
        return dosis;
    }

    public void setDosis(int dosis) {
        this.dosis = dosis;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public boolean isTomado() {
        return tomado;
    }

    public void setTomado(boolean tomado) {
        this.tomado = tomado;
    }

    @Override
    public String toString() {
        return "Medicamento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", dosis=" + dosis +
                ", horario='" + horario + '\'' +
                ", dias='" + dias + '\'' +
                ", tomado=" + tomado +
                '}';
    }
}
