package edu.prog2.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.Producto;
import edu.prog2.model.TipoProducto;

public class ProductoService implements Service<Producto> {

    private List<Producto> productos;
    private String fileName;

    public ProductoService() throws IOException {

        fileName = Utils.PATH + "productos";

        if (Utils.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (Utils.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            productos = new ArrayList<>();
            // throw new IOException("Aún no se ha creado un archivo de productos");

        }
    }
    public void updateData() throws IOException{
        Utils.writeData(productos, fileName);
    }

/*     @Override
    public JSONObject add(JSONObject json) throws Exception {
        Producto p = new Producto(json);
        if (productos.contains(p)) {
            throw new ArrayStoreException(
                    String.format("El producto %s ya existe", p.getNombre()));
        }

        if (productos.add(p)) {
            Utils.writeData(productos, fileName);
        }
        return p.toJSONObject();
    } */
    
    @Override
    public JSONObject add(JSONObject json) throws Exception {
        json.put("id", Utils.getRandomKey(5));
        Producto p = new Producto(json);
        if (productos.contains(p)) {
            throw new ArrayStoreException(String.format("El producto %s ya existe", p.getNombre()));
        }

        if (productos.add(p)) {
            Utils.writeData(productos, fileName);
        }
        return p.toJSONObject();
    }

    @Override
    public JSONObject get(int index) {
        return productos.get(index).toJSONObject();
    }

    @Override
    public JSONObject get(String id) {
        int i = productos.indexOf(new Producto(id));
        return i > -1 ? get(i) : null;
    }

    @Override
    public Producto getItem(String id) {
        int i = productos.indexOf(new Producto(id));
        return i > -1 ? productos.get(i) : null;
    }

    @Override
    public JSONArray getAll() throws IOException {
        return new JSONArray(Utils.readText(fileName + ".json"));
    }

    @Override
    public List<Producto> loadCSV() throws IOException {
        productos = new ArrayList<>();
        String text = Utils.readText(fileName + ".csv").trim();
        if (text.length() > 0) {
            try (Scanner sc = new Scanner(text).useDelimiter(";|[\n]+|[\r\n]+")) {
                while (sc.hasNext()) {
                    String id = sc.next();
                    String nombre = sc.next();
                    TipoProducto tipo = TipoProducto.getEnum(sc.next());
                    double valorBase = sc.nextDouble();
                    double valorVenta = sc.nextDouble();
                    int disponibles = sc.nextInt();
                    LocalDate vence = LocalDate.parse(sc.next());
                    productos.add(new Producto(id, nombre, tipo, valorBase, valorVenta, disponibles, vence));
                }
            }
        }
        return productos;
    }

    @Override
    public List<Producto> loadJSON() throws IOException {
        productos = new ArrayList<>();
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            productos.add(new Producto(jsonObj));
        }

        return productos;
    }

    @Override
    public JSONObject update(String id, JSONObject json) throws Exception {
        Producto producto = getItem(id);
        if (producto == null) {
            throw new NullPointerException("No se encontró el producto " + id);
        }

        if (json.has("nombre")) {
            producto.setNombre(json.getString("nombre"));
        }

        if (json.has("tipo")) {
            producto.setTipo(TipoProducto.getEnum(json.optString("tipo")));
        }

        if (json.has("valorBase")) {
            producto.setValorBase(json.getDouble("valorBase"));
        }

        if (json.has("valorVenta")) {
            producto.setValorVenta(json.getDouble("valorVenta"));
        }

        if (json.has("disponibles")) {
            producto.setDisponibles(json.getInt("disponibles"));
        }
        
        if (json.has("vence")) {
            producto.setVence(LocalDate.parse(json.getString("vence")));
        }

        Utils.writeData(productos, fileName);
        return producto.toJSONObject();
    }

    @Override
    public void refreshAll() throws IOException {
        productos = new ArrayList<>();
        loadCSV();
        Utils.writeJSON(productos, fileName + ".json");
    }

    @Override
    public void remove(String id) throws Exception {
        JSONObject producto = get(id);
        if (producto == null) {
            throw new IllegalArgumentException(
                    "No hay una instancia con el id " + id);
        }

        if (Utils.exists(Utils.PATH + "detalles", "producto", producto)) {
            throw new Exception(String.format(
                    "No eliminado. El producto %s tiene detalles registrados", id));
        }
       
        if (!productos.remove(new Producto(producto))) {
            throw new Exception(String.format(
                    "Falló la eliminación del producto con Id %s", id));
        }

        Utils.writeData(productos, fileName);
    }
}
