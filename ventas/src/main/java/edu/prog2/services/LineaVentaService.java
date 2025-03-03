package edu.prog2.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.LineaVenta;
import edu.prog2.model.Producto;
import edu.prog2.model.Venta;

public class LineaVentaService implements Service<LineaVenta> {

    private List<LineaVenta> detalles;
    private String fileName;
    private VentaService ventas; // se supone que VentaService aún no existe
    private ProductoService productos;

    // punto 25 taller 6
    public LineaVentaService(VentaService ventas, ProductoService productos) throws Exception {

        this.ventas = ventas;
        this.productos = productos;
        fileName = Utils.PATH + "detalles";

        if (Utils.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (Utils.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            detalles = new ArrayList<>();
        }

    }

    // punto 36 taller 6
    @Override
    public JSONObject add(JSONObject json) throws Exception {
        JSONObject venta = json.getJSONObject("venta");
        if (!json.has("detalle")) {
            throw new IllegalArgumentException("No se encontró el detalle de la venta");
        }
        JSONArray detalle = json.getJSONArray("detalle");
        if (detalle.length() == 0) {
            throw new IllegalArgumentException("No se pueden crear ventas sin detalles de ventas");
        }
        double totalVenta = 0;
        for (int i = 0; i < detalle.length(); i++) {
            if (addLineaVenta(venta, detalle, i)) {
                totalVenta += detalle.getJSONObject(i).getDouble("subtotal");

            }
        }
        venta.put("totalVenta", totalVenta);
        Utils.writeData(detalles, fileName);

        return json;

    }

    // punto 37 taller 6
    private boolean addLineaVenta(JSONObject venta, JSONArray detalle, int i) throws IOException {
        JSONObject lineaVenta = detalle.getJSONObject(i);
        JSONObject producto = productos.get(lineaVenta.getString("producto"));
        int cantidad = lineaVenta.getInt("cantidad");
        if (cantidad < 0) {
            throw new IllegalArgumentException("Cantidad negativa: " + cantidad);
        }
        if (producto.getInt("disponibles") < cantidad) {
            throw new IllegalArgumentException("Cantidad no disponible: " + cantidad);
        }
        boolean ok = cantidad > 0;
        if (cantidad > 0) {
            Producto p = productos.getItem(producto.getString("id"));
            p.setDisponibles(p.getDisponibles() - cantidad);
            LineaVenta lv = new LineaVenta(Utils.getRandomKey(5), cantidad, p, new Venta(venta));
            ok = detalles.add(lv);
            JSONObject json = lv.toJSONObject();
            json.remove("venta");
            detalle.put(i, json);
            productos.updateData();
        }

        return ok;

    }

    @Override
    public JSONObject get(int index) {
        return detalles.get(index).toJSONObject();
    }

    @Override
    public JSONObject get(String id) {
        int i = detalles.indexOf(new LineaVenta(id));
        return i > -1 ? get(i) : null;
    }

    @Override
    public LineaVenta getItem(String id) {
        int i = detalles.indexOf(new LineaVenta(id));
        return i > -1 ? detalles.get(i) : null;
    }

    // punto 41 taller 6 
    @Override
    public JSONArray getAll() throws Exception {
        return new JSONArray(Utils.readText(fileName + ".json"));
    }

    // punto 42 taller 6, sobrecarga delmetodo getAll
    public JSONArray getAll(String numeroFactura) throws Exception {
        JSONArray selected = new JSONArray();
        getAll().forEach(item -> {
            JSONObject lineaVenta = (JSONObject) item;
            if (lineaVenta.getJSONObject("venta").getString("numero").equals(numeroFactura)) {
                lineaVenta.remove("venta");
                selected.put(lineaVenta);
            }
        });
        return selected;
    }

    // punto 29 taller 6
    @Override
    public List<LineaVenta> loadCSV() throws Exception {
        detalles = new ArrayList<>();
        String text = Utils.readText(fileName + ".csv").trim();
        if (text.length() > 0) {
            try (Scanner sc = new Scanner(text).useDelimiter(";|[\n]+|[\r\n]+")) {
                while (sc.hasNext()) {
                    String id = sc.next();
                    String numero = sc.next();
                    int cantidad = sc.nextInt();
                    Producto producto = new Producto(productos.get(sc.next()));
                    Venta v = new Venta(ventas.get(numero));
                    double valorUnitario = sc.nextDouble();
                    producto.setValorVenta(valorUnitario);

                    detalles.add(new LineaVenta(id, cantidad, producto, v));
                }
            }
        }
        return detalles;
    }

    // punto 30 taller 6
    @Override
    public List<LineaVenta> loadJSON() throws Exception {
        detalles = new ArrayList<>();
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            detalles.add(new LineaVenta(jsonObj));
        }

        return detalles;
    }

    // punto 47 taller 6
    @Override
    public JSONObject update(String id, JSONObject json) throws Exception {
        return new JSONObject().put("message", "Por políticas internas no se permite hacer cambios a una venta");
    }

    @Override
    public void refreshAll() throws Exception {
        detalles = new ArrayList<>();
        loadCSV();
        Utils.writeJSON(detalles, fileName + ".json");
    }

    @Override
    public void remove(String id) throws Exception {
        JSONObject JSONAux = new JSONObject();
        JSONAux.put("message", "Por políticas internas no se permiten hacer cambios a una venta");
    }
}