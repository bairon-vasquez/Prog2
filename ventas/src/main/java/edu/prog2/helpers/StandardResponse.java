package edu.prog2.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spark.Response;

public class StandardResponse {

    public static boolean DEBUGGIN;
    private JSONObject json;

    public StandardResponse(Response response, int status, String message) {
        response.type("application/json");
        response.status(status);
        this.json = new JSONObject()
                .put("status", status)
                .put("message", message);
    }

    public StandardResponse(Response response, String message) {
        response.type("application/json");
        response.status(418);
        this.json = new JSONObject().put("message", message);
    }

    public StandardResponse(Response response, int status, Exception e) {
        this(response, e);
        response.status(status);
    }

    public StandardResponse(Response response, Exception e) {
        String message = e.getMessage() == null ? "Para más detalles, ver stack trace en la consola del servidor" : e.getMessage();
        response.type("application/json");
        response.status(418);
        this.json = new JSONObject()
                .put("error", e.toString())
                .put("message", message);
        response.body(json.toString());
        if (DEBUGGIN) {
            System.out.println("-".repeat(30) + " Reporte de excepciones " + "-".repeat(30));
            e.printStackTrace();
        }
    }

    public StandardResponse(Response response, int status, String message, String data) {
        this(response, status, message);
        this.json.put("data", getData(data));
    }

    public StandardResponse(Response response, String message, String data) {
        this(response, message);
        this.json.put("data", getData(data));
    }

    public StandardResponse(Response response, int status, String message, JSONObject data) {
        this(response, status, message);
        this.json.put("data", data);
    }

    public StandardResponse(Response response, String message, JSONObject data) {
        this(response, message);
        this.json.put("data", data);
    }

    public StandardResponse(Response response, int status, String message, JSONArray data) {
        this(response, status, message);
        this.json.put("data", data);
    }

    public StandardResponse(Response response, String message, JSONArray data) {
        this(response, message);
        this.json.put("data", data);
    }

    public JSONObject getData(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            String message = "Se esperaba una respuesta en formato JSON, pero mire esto:\n";
            return new JSONObject().put("info", message + data);
        }
    }

    public String getTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
