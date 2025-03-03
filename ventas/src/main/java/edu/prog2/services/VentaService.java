package edu.prog2.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.Cliente;
import edu.prog2.model.Venta;

public class VentaService implements Service<Venta> {

    private List<Venta> ventas;
    private String fileName;
    private ClienteService clientes;
    private LineaVentaService lineasVentas;

    // punto 43 taller 6
    public JSONObject getFull(String numero) throws Exception {

        JSONObject venta = get(numero);
        if (venta == null) {
            throw new Exception("No se encontró la venta con número " + numero);
        }
        JSONArray detalle = lineasVentas.getAll(numero);
        double totalVenta = 0;

        for (int i = 0; i < detalle.length(); i++) {
            totalVenta += detalle.getJSONObject(i).getDouble("subtotal");
        }
        venta.put("total", totalVenta);
        return new JSONObject().put("venta", venta).put("detalle", detalle);

    }

    // punto 24 taller 6
    public VentaService(ClienteService clientes, ProductoService productos) throws Exception {
        this.clientes = clientes;
        fileName = Utils.PATH + "ventas";

        if (Utils.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (Utils.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            ventas = new ArrayList<>();
        }

        this.lineasVentas = new LineaVentaService(this, productos); // punto 26 taller 6
    }

    // punto 35 taller 6
    @Override
    public JSONObject add(JSONObject json) throws Exception {
        JSONObject venta = json.getJSONObject("venta");
        venta.put("numero", Utils.getRandomKey(5));
        JSONObject cliente = clientes.get(venta.getString("cliente"));

        if (cliente == null) {
            throw new ArrayStoreException("No se encontró el cliente al que intenta vender");
        }

        venta.put("cliente", cliente);
        Venta v = new Venta(venta);

        if (ventas.add(v)) {
            Utils.writeData(ventas, fileName);
            lineasVentas.add(json);
        }

        return json;
    }

    // punto 39 taller 6
    @Override
    public JSONObject get(int index) {
        return ventas.get(index).toJSONObject();
    }

    // punto 38 taller 6
    @Override
    public JSONObject get(String numero) throws Exception {
        int i = ventas.indexOf(new Venta(numero));
        return i > -1 ? get(i) : null;
    }

    @Override
    public Venta getItem(String numero) {
        int i = ventas.indexOf(new Venta(numero));
        return i > -1 ? ventas.get(i) : null;
    }

    @Override
    public JSONArray getAll() throws Exception {
        JSONArray jsonArr = new JSONArray();
        for (Venta venta: ventas) {
            JSONObject ventaDetalles = getFull(venta.getNumero());
            jsonArr.put(ventaDetalles);

        }
        return jsonArr;
    }

    // Punto 51 Taller 6
    public LineaVentaService getLineasVentas() {
        return lineasVentas;
    }

    // punto 27 taller 6
    @Override
    public List<Venta> loadCSV() throws Exception {
        ventas = new ArrayList<>();
        String text = Utils.readText(fileName + ".csv").trim();
        if (text.length() > 0) {
            try (Scanner sc = new Scanner(text).useDelimiter(";|[\n]+|[\r\n]+")) {
                while (sc.hasNext()) {
                    String numero = sc.next();
                    LocalDateTime fecha = LocalDateTime.parse(sc.next());
                    String clienteId = sc.next();
                    Cliente cliente = new Cliente(clientes.get(clienteId));
                    Venta ven = new Venta(fecha, numero, cliente);
                    ventas.add(ven);
                }
            }
        }
        return ventas;
    }

    // punto 28 taller 6
    @Override
    public List<Venta> loadJSON() throws Exception {
        ventas = new ArrayList<>();
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            ventas.add(new Venta(jsonObj));
        }
        return ventas;
    }

    // punto 47 taller 6
    @Override
    public JSONObject update(String id, JSONObject json) throws Exception {
        return new JSONObject().put("message", "Por políticas internas no se permite hacer cambios a una venta");
    }

    @Override
    public void refreshAll() throws Exception {
        ventas = new ArrayList<>();
        loadCSV();
        Utils.writeJSON(ventas, fileName + ".json");
    }

    @Override
    public void remove(String id) throws Exception {
        JSONObject JSONAux = new JSONObject();
        JSONAux.put("message", "Por políticas internas no se permiten hacer cambios a una venta");
    }
}