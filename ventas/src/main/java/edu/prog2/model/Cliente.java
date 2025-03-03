package edu.prog2.model;

import org.json.JSONObject;

public class Cliente implements Format {
    private String identificacion;
    private String nombre;
    private String telefono;

    // constructor por defecto
    public Cliente() {
        this("C00", " Cliente Sin nombre ", "Sin telefono");
    }

    // Constructor parametrizado
    public Cliente(String identificacion, String nombre, String telefono) {
        setIdentificacion(identificacion);
        setNombre(nombre);
        setTelefono(telefono);
    }

    // Constructor copia
    public Cliente(Cliente c) {
        this(c.identificacion, c.nombre, c.telefono);
    }

    // este lo agregue por prueba
    public Cliente(String identificacion) {
        this();
        setIdentificacion(identificacion);
    }

    // Constructor JSON
    public Cliente(JSONObject json) {
        this(
                json.getString("identificacion"),
                json.getString("nombre"),
                json.getString("telefono"));
    }

    // ASCESORES Y MUTADORES
    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new NullPointerException(
                    "La identificación no puede ser un valor nulo, vacío o en blanco");
        }
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new NullPointerException(
                    "El nombre no puede tener un campo nulo, vacío o en blanco");
        }
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;

    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // ----------------------------------------------------------------------//

    // Punto 9-Taller 3
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Cliente other = (Cliente) obj;
        String message;
        if (!this.identificacion.equals(other.identificacion) &&
                (this.nombre.isBlank() || other.nombre.isBlank())) {
            message = String.format(
                    "Error en los clientes %s y/o %s. No se admiten clientes sin nombre",
                    this.identificacion, other.identificacion);
            throw new IllegalArgumentException(message);
        }
        if (!this.identificacion.equals(other.identificacion)) {
            return false;
        }
        return true;
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%s%n", identificacion, nombre, telefono);
    }

    @Override
    public String toString() {
        return String.format("%10s %-30s %15s", identificacion, nombre, telefono);
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

}
