package edu.prog2.model;

import org.json.JSONObject;

public class LineaVenta implements Format {
    private String id;
    private int cantidad;
    private Producto producto;
    private Venta venta;

    public LineaVenta() {
        this("",0, null, null);
    }

    public LineaVenta(String id,int cantidad, Producto producto, Venta venta) {
        setId(id);
        setProducto(producto);
        setCantidad(cantidad);
        setVenta(venta);
    }

    public LineaVenta(LineaVenta c) {
        this(c.id, c.cantidad, c.producto, c.venta);
    }
    public LineaVenta(String id){
        this();
        setId(id);
    }

    public LineaVenta(JSONObject json) {
        this(json.getString("id") ,json.getInt("cantidad"), new Producto(json.getJSONObject("producto")),
                new Venta(json.getJSONObject("venta")));
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new NullPointerException("El ID de una línea de venta no puede ser nulo ni estar en blanco.");
        }
        this.id = id;
    }

    public void setProducto(Producto producto) {
        this.producto = new Producto(producto);
    }

    public void setCantidad(int cantidad) {
/*         if (cantidad > producto.getDisponibles()) {
            throw new IllegalArgumentException("La cantidad que se desea comprar es mayor al inventario que se tiene");
        } */
        this.cantidad = cantidad;
        
    }

    public void setCantidadDisponible(){
        int cantidadDisponible = producto.getDisponibles() - this.cantidad;
        this.producto.setDisponibles(cantidadDisponible);
    }

    public double getSubtotal(){
        return cantidad * producto.getValorVenta();
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public String getId() {
        return id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public Venta getVenta() {
        return venta;
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

        LineaVenta lineaVenta = (LineaVenta) obj;
        //
        if (!this.id.equals(lineaVenta.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d %s %f%n", id, venta.getNumero(), cantidad, producto.getId(), producto.getValorVenta());
    }

    @Override
    public String toCSV() {
        return String.format("%s;%s;%d;%s;%f%n", id, venta.getNumero(), cantidad, producto.getId(), producto.getValorVenta());
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

}
