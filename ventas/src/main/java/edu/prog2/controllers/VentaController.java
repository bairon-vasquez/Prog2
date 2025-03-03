package edu.prog2.controllers;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.put;
import spark.Request;
import spark.Response;

import org.json.JSONObject;

import edu.prog2.helpers.StandardResponse;
import edu.prog2.services.VentaService;

public class VentaController {

    public VentaController(final VentaService ventaService) {

        path("/ventas", () -> {

            // punto 45 taller 6
            get("", (request, response) -> new StandardResponse(response, 226, "ok", ventaService.getAll()));

            // punto 46 taller 6
            get("/:arg", (Request request, Response response) -> {
                String arg = request.params(":arg");
                if (arg.matches("-?\\d+")) {
                    // si es un número en base 10, buscar por índice
                    int i = Integer.parseInt(arg, 10);
                    return new StandardResponse(response, 226, "ok", ventaService.get(i));
                } else {
                    // buscar por ID
                    return new StandardResponse(response, 226, "ok", ventaService.getFull(arg));
                }
            });

            // … como de costumbre aquí irán los métodos CRUD
            post("", (request, response) -> {
                JSONObject json = new JSONObject(request.body());
                json = ventaService.add(json);
                return new StandardResponse(response, 201, "ok", json);
            });

            put("/:id", (request, response) -> {
                String id = request.params(":id");
                JSONObject json = new JSONObject(request.body());
                json = ventaService.update(id, json);
                return new StandardResponse(response, 201, "ok", json);
            });

            delete("/:id", (request, response) -> {
                String id = request.params(":id");
                ventaService.remove(id);
                return new StandardResponse(response, 200, "ok");
            });
        });
    }
}
