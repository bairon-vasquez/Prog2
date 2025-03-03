package edu.prog2.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.prog2.helpers.Utils;
import edu.prog2.model.Cliente;

//Punto 2 - Taller 4
public class ClienteService implements Service<Cliente> {

    // Punto 3 - Taller 4
    private List<Cliente> clientes;
    private String fileName;

    // Punto 6 - taller 4
    public ClienteService() throws IOException {

        fileName = Utils.PATH + "clientes";

        if (Utils.fileExists(fileName + ".csv")) {
            loadCSV();
        } else if (Utils.fileExists(fileName + ".json")) {
            loadJSON();
        } else {
            clientes = new ArrayList<>();
            //throw new IOException("Aún no se ha creado un archivo de clientes"); hubo
            // aqui un error con esto que lo solucionamos en clase
        }
    }

    // Punto 7 - Taller4
    @Override
    public JSONObject add(JSONObject json) throws Exception {
        Cliente c = new Cliente(json);
        if (clientes.contains(c)) {
            throw new ArrayStoreException(
                    String.format("El cliente %s ya existe", c.getNombre()));
        }

        if (clientes.add(c)) {
            Utils.writeData(clientes, fileName);
        }
        return c.toJSONObject();
    }

    // Punto 8 - Taller 4
    @Override
    public JSONObject get(int index) {
        return clientes.get(index).toJSONObject();
    }

    // Punto 9 - Taller 4
    @Override
    public JSONObject get(String identificacion) {
        int i = clientes.indexOf(new Cliente(identificacion));
        return i > -1 ? get(i) : null;
    }

    // Punto 10- Taller 4
    @Override
    public Cliente getItem(String identificacion) {
        int i = clientes.indexOf(new Cliente(identificacion));
        return i > -1 ? clientes.get(i) : null;
    }

    // Punto 11- taller 4
    @Override
    public JSONArray getAll() throws IOException {
        return new JSONArray(Utils.readText(fileName + ".json"));
    }

    // Punto 4 - Taller 4
    @Override
    public List<Cliente> loadCSV() throws IOException {
        clientes = new ArrayList<>();
        String text = Utils.readText(fileName + ".csv").trim();
        if (text.length() > 0) {
            try (Scanner sc = new Scanner(text).useDelimiter(";|[\n]+|[\r\n]+")) {
                while (sc.hasNext()) {
                    String identificacion = sc.next();
                    String nombre = sc.next();
                    String telefono = sc.next();
                    clientes.add(new Cliente(identificacion, nombre, telefono));
                }
            }
        }
        return clientes;
    }

    // Punto 5 - Taller 5
    @Override

    public List<Cliente> loadJSON() throws IOException {
        clientes = new ArrayList<>();
        String data = Utils.readText(fileName + ".json");
        JSONArray jsonArr = new JSONArray(data);

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            clientes.add(new Cliente(jsonObj));
        }

        return clientes;
    }

    // Punto 12 - Taller4
    @Override
    public JSONObject update(String identificacion, JSONObject json) throws Exception {
        Cliente cliente = getItem(identificacion);
        if (cliente == null) {
            throw new NullPointerException("No se encontró el cliente " + identificacion);
        }

        if (json.has("nombre")) {
            cliente.setNombre(json.getString("nombre"));
        }

        if (json.has("telefono")) {
            cliente.setTelefono(json.getString("telefono"));
        }

        Utils.writeData(clientes, fileName);
        return cliente.toJSONObject();
    }

    // Punto 13 - Taller 4
    @Override
    public void refreshAll() throws IOException {
        clientes = new ArrayList<>();
        loadCSV();
        Utils.writeJSON(clientes, fileName + ".json");
    }

    // Punto 14 - Taller 4
    @Override

    public void remove(String identificacion) throws Exception {
        JSONObject cliente = get(identificacion);
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "No hay una instancia con la identificación " + identificacion);
        }

        if (Utils.exists(Utils.PATH + "ventas", "cliente", cliente)) {
            throw new Exception(String.format(
                    "No eliminado. El cliente %s tiene registradas ventas", identificacion));
        }

        if (!clientes.remove(new Cliente(cliente))) {
            throw new Exception(String.format(
                    "Falló la eliminación del cliente con identificación %s", identificacion));
        }

        Utils.writeData(clientes, fileName);
    }

}
