package edu.prog2.services;
//Punto 1 - TALLER 4
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public interface Service<T> {

    public JSONObject add(JSONObject json) throws Exception;

    public JSONObject get(int index);

    public JSONObject get(String id) throws Exception;

    public T getItem(String id);

    public JSONArray getAll() throws Exception;

    public List<T> loadCSV() throws Exception;

    public List<T> loadJSON() throws Exception;

    public JSONObject update(String id, JSONObject json) throws Exception;

    public void refreshAll() throws Exception;

    public void remove(String id) throws Exception;

}


