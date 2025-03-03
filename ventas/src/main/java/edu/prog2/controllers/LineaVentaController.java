package edu.prog2.controllers;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.put;

import org.json.JSONObject;

import edu.prog2.helpers.StandardResponse;
import edu.prog2.services.LineaVentaService;
import spark.Request;
import spark.Response;
public class LineaVentaController {

    
public LineaVentaController(final LineaVentaService lineaVentaService) {

        path("/detalles", () -> {

            get("", (request, response) -> new StandardResponse(response, 226, "ok", lineaVentaService.getAll()));

            get("/:arg", (Request request, Response response) -> {
                String arg = request.params(":arg");
                if (arg.matches("-?\\d+")) {
                    // si es un número en base 10, buscar por índice
                    int i = Integer.parseInt(arg, 10);
                    return new StandardResponse(response, 226, "ok", lineaVentaService.get(i));
                } else {
                    // buscar por ID
                    return new StandardResponse(response, 226, "ok", lineaVentaService.get(arg));
                }
            });

            post("", (request, response) -> {
                JSONObject json = new JSONObject(request.body());
                json = lineaVentaService.add(json);
                return new StandardResponse(response, 201, "ok", json);
            });

            put("/:id", (request, response) -> {
                String id = request.params(":id");
                JSONObject json = new JSONObject(request.body());
                json = lineaVentaService.update(id, json);
                return new StandardResponse(response, 201, "ok", json);
            });

            delete("/:id", (request, response) -> {
                String id = request.params(":id");
                lineaVentaService.remove(id);
                return new StandardResponse(response, 200, "ok");
            });
        });

    }

}
 