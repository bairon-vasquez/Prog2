package edu.prog2;
//punto 18 taller 4

import java.util.Locale;

import edu.prog2.controllers.ClienteController;
import edu.prog2.controllers.ProductoController;
import edu.prog2.controllers.VentaController;
import edu.prog2.helpers.StandardResponse;
import edu.prog2.model.TipoProducto;
import edu.prog2.services.*;
import spark.Spark;

import static spark.Spark.*;

public class AppRest {

    public static void main(String[] args) throws Exception {
        //punto 17 taller 9
        enableCORS();
        StandardResponse.DEBUGGIN = true;
        Locale.setDefault(new Locale("es_CO"));

        /*
         * before((request, response) -> response.type("application/json"));
         * 
         * ClienteService clienteService = new ClienteService();
         * 
         * post("/clientes", (request, response) -> {
         * JSONObject json = new JSONObject(request.body());
         * return clienteService.add(json);
         * });
         * 
         * get("/clientes", (request, response) -> clienteService.getAll());
         */

        // punto 2 taller 5

        before((request, response) -> {
            response.type("application/json");
            response.header("Access-Control-Allow-Origin", "*");
        });

        exception(Exception.class, (exception, request, response) -> new StandardResponse(response, exception));

        notFound((request, response) -> {
            String message = String.format(
                    "La ruta solicitada [%s] no ha sido mapeada",
                    request.pathInfo());
            return new StandardResponse(response, 404, message);
        });

        // punto 5 taller 5

        /*
         * get("/prueba", (request, response) -> {
         * int a = 10;
         * int b = 0;
         * return String.format("{\"resultado\": %d}", a / b);
         * });
         */

        // punto 2 del taller 6
        ClienteService clienteService = new ClienteService();
        new ClienteController(clienteService);

        ProductoService productoService = new ProductoService();
        new ProductoController(productoService);

        // punto 32
        VentaService ventaService = new VentaService(clienteService, productoService);
        new VentaController(ventaService);

        // punto 49 taller 6
        get("/categorias", (req, res) -> new StandardResponse(
                res, 226, "ok", TipoProducto.getAll()));
    }
    //punto 16 taller 9
    private static void enableCORS() {
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "ok";
        });
    }

}