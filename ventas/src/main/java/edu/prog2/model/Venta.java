package edu.prog2.model;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class Venta implements Format {
    private LocalDateTime fecha;
    private String numero;
    private Cliente cliente;

    // Constructor por defecto
    public Venta() {
        this(LocalDateTime.now(), "V0000", new Cliente("C01"));
    }

    // Constructor parametrizado
    public Venta(LocalDateTime fecha, String numero, Cliente cliente) {
        setNumero(numero);
        setCliente(cliente);
        setFecha(fecha);

    }

    public Venta(Cliente cliente, LocalDateTime fecha) {
        this();
        setCliente(cliente);
        setFecha(fecha);
    }

    public Venta(String numero) {
        this();
        setNumero(numero);
    }

    // Constructor copia
    public Venta(Venta a) {
        this(a.fecha, a.numero, a.cliente);
    }

    public Venta(JSONObject json) {
        this(LocalDateTime.parse(json.getString("fecha")),
                json.getString("numero"), new Cliente(json.getJSONObject("cliente")));
    }

    // Asesores
    public String getNumero() {
        return numero;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    // Mutadores
    public void setFecha(LocalDateTime fecha) {
        LocalDateTime fechaLimite = LocalDateTime.of(2022, 01, 01, 00, 00, 00);

        if (fecha.isBefore(fechaLimite)) {
            throw new IllegalArgumentException("No se permiten fechas menores a 2022-01-01 00:00.");
        }
        this.fecha = fecha;
    }

    public void setNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new NullPointerException(
                    "El número de una venta no puede ser null ni estar en blanco.");
        }
        this.numero = numero;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("El cliente asociado a una venta no puede ser null.");
        }
        this.cliente = cliente;
    }

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

        Venta venta = (Venta) obj;

        // Punto e
        if (!this.numero.equals(venta.numero)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s%n", numero, fecha, cliente.toString());
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%s%n", numero, fecha, cliente.getIdentificacion());
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }
}
