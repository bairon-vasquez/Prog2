package edu.prog2.model;

import java.time.LocalDate;
import org.json.JSONObject;
import edu.prog2.helpers.Utils;

public class Producto implements Format {
    private String id;
    private String nombre;
    private TipoProducto tipo;
    private double valorBase;
    private double valorVenta;
    private int disponibles;
    private LocalDate vence;

    // Punto 20-Taller 3
    // Constructor por defecto
    public Producto() {
        this(Utils.getRandomKey(5), "Producto sin nombre", TipoProducto.OTROS,
                0.0, 0.0, 0, LocalDate.now());
    }

    // Punto 19-Taller 3
    // Constructor parametrizado
    public Producto(String id, String nombre, TipoProducto tipo, double valorBase, double valorVenta, int disponibles,
            LocalDate vence) {
        setId(id);
        setNombre(nombre);
        setTipo(tipo);
        setValorBase(valorBase);
        setValorVenta(valorVenta);
        setDisponibles(disponibles);
        setVence(vence);
    }

    // Punto 23-Taller 3 --> Constructor copia
    public Producto(Producto p) {
        this(p.id, p.nombre, p.tipo, p.valorBase, p.valorVenta, p.disponibles, p.vence);
    }

    // Punto 21-Taller 3 --> Constructor para id
    public Producto(String id) {
        this(); // Llama al constructor por defecto para asignar valores iniciales
        setId(id); // Asigna el id dado por argumento
    }

    // punto 22-Taller 3 --> Constructor sin id
    public Producto(String nombre, TipoProducto tipo, double valorBase, double valorVenta, int disponibles,
            LocalDate vence) {
        this(Utils.getRandomKey(5), nombre, tipo, valorBase, valorVenta, disponibles, vence);
    }

    // Punto 24-Taller 3 --> constructor JSON
    public Producto(JSONObject json) {
        this(
                json.getString("id"),
                json.getString("nombre"),
                // json.getEnum(TipoProducto.class, "tipo"),
                TipoProducto.getEnum(json.optString("tipo")),
                json.getDouble("valorBase"),
                json.getDouble("valorVenta"),
                json.getInt("disponibles"),
                LocalDate.parse(json.getString("vence")));
    }

    // -------------ASCESORES Y MUTADORES ---------------------//
    // Punto 12-Taller 3
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new NullPointerException(
                    "La identificacion no puede tener un campo nulo, vacío o en blanco");
        }
        this.id = id;
    }

    // Punto 13-Taller 3
    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new NullPointerException(
                    "El nombre no puede tener un campo nulo, vacío o en blanco");
        }
        this.nombre = nombre;
    }

    // Punto 14-Taller 3
    public TipoProducto getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoProducto tipo) {
        if (tipo == null) {
            throw new NullPointerException(
                    "Tipo no puede tener un campo nulo, vacío o en blanco");
        }
        this.tipo = tipo;
    }

    public double getValorBase() {
        return this.valorBase;
    }

    // punto 15-Taller 3
    public void setValorBase(double valorBase) {
        if (valorBase < 0) {
            throw new IllegalArgumentException("No se permite valor negativo");
        }
        this.valorBase = valorBase;
    }

    public double getValorVenta() {
        return this.valorVenta;
    }

    // punto 15-Taller 3
    public void setValorVenta(double valorVenta) {
        if (valorVenta < 0) {
            throw new IllegalArgumentException("No se permite valor negativo");
        }
        this.valorVenta = valorVenta;
    }

    public int getDisponibles() {
        return this.disponibles;
    }

    public void setDisponibles(int disponibles) {
        if (disponibles < 0) {
            throw new IllegalArgumentException("No se permite valor negativo");
        }
        this.disponibles = disponibles;
    }

    public LocalDate getVence() {
        return this.vence;
    }

    // Punto 17-Taller 3
    public void setVence(LocalDate vence) {
        LocalDate fechaLimite = LocalDate.of(2022, 01, 01);

        if (vence.isBefore(fechaLimite)) {
            throw new IllegalArgumentException("La fecha de vencimiento es inválida");
        }
        this.vence = vence;
    }

    // Punto 18-Taller 3
    public double getSubtotal() {
        return this.disponibles * getValorVenta();
    }

    // punto 52 taller 6
    public String getTipoEnum() {
        return tipo.getValue();
    }

    // Punto 25-Taller 3
    @Override
    public boolean equals(Object o) {
        System.out.println("revisar Empresa::equals()");
        if (o == this) // (a)Si el argumento recibido Y this son iguales, retorna true
            return true;
        if (o == null) { // (b)Si el argumenrto recibido es null, retorna false
            return false;
        }
        if (getClass() != o.getClass()) { // (c) Si el argumento recibido es de un tipo desitino a Producto, retorna
                                          // false
            return false;
        }

        Producto producto = (Producto) o;
        // (d) Si id de this como el argumento son distintos y nombre de
        // this como el argumento estan en blanco lance una excepcion
        if (!this.id.equals(producto.id) && (this.nombre.isBlank() || producto.nombre.isBlank())) {
            throw new IllegalArgumentException("Error no se admiten productos sin nombre");
        }
        // (e) Si id de this como el argumento Y el nombre de this son iguales, lance
        // una excepcion
        if (!this.id.equals(producto.id) && this.nombre.equalsIgnoreCase(producto.nombre)) {
            throw new IllegalArgumentException("Nombre del producto existe");
        }
        if (!this.id.equals(producto.id) && !this.nombre.equalsIgnoreCase(producto.nombre)) {
            return false;
        }
        // (f) en cualquier otro caso retorna true
        return true;
    }

    // Punto 26-Taller 3
    @Override
    public String toCSV() {
        // este orden afecta el orden de lectura en loadCsV()
        return String.format("%s;%s;%s;%f;%f;%d;%s%n", id, nombre, tipo, valorBase, valorVenta, disponibles, vence);
    }

    // Punto 27-Taller 3
    @Override
    public String toString() {
        return String.format(
                "%-6s%-30s%-20s%10.2f%10.2f%5d%10.2f%12s",
                id, nombre, tipo, valorBase, valorVenta, disponibles, getSubtotal(), vence);
    }

    // Punto 28-Taller 3
    @Override
    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

}
