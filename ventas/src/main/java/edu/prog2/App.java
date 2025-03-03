package edu.prog2;
import edu.prog2.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import edu.prog2.helpers.*;

public class App {

    static List<Cliente> clientes;
    static List<Producto> productos;
    public static void main( String[] args ){
        menu();
        
    }

    private static void menu(){
        try {
            inicializar();
        } catch (Exception e) {
            e.printStackTrace();
        }

        do {
            try {
                int opcion = leerOpcion();
                switch (opcion) {
                    case 1:
                        crearClienteV1();
                        break;
                    case 2:
                        crearClienteV2();
                        break;
                    case 3:
                        crearClienteV3();
                        break;
                    case 4:
                        crearClienteV4();
                        break;
                    case 5:
                        actualizarCliente();
                        break;
                    case 6:
                        listarClientes();
                        break;
                    case 7:
                        comrpobacionesClientes();
                        break;
                    case 8:
                        crearProductoV1();
                        break;
                    case 9:
                        crearProductoV2();
                        break;
                    case 10:
                        crearProductoV3();
                        break;
                    case 11:
                        crearProductoV4();
                        break;
                    case 12:
                        actualizarProductos();
                        break;
                    case 13:
                        listarProductos();
                        break;
                    case 14:
                        comparacionesProductos();
                        break;
                    case 0:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción Inválida");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }

    private static void inicializar(){
        System.out.print("\033[H\033[2J");
        Locale.setDefault(new Locale("es_CO"));
        clientes = new ArrayList<>();
        productos = new ArrayList<>();
    }

    static int leerOpcion() {
        String opciones = String.format("\n%sMenú de opciones:%s\n", Utils.GREEN, Utils.RESET)
                + "  1 - Crear cliente con el constructor por defecto        8 - Crear producto con el construtor por defecto\n"
                + "  2 - Crear cliente con el constructor parametrizado      9 - Crear producto con el construtor parametrizado\n"
                + "  3 - Crear cliente con el constructor copia             10 - Crear producto con el construtor copia\n"
                + "  4 - Crear cliente a partir de un objeto JSON           11 - Crear producto  a partir  de un objeto JSON\n"
                + "  5 - Actualizar los datos de una instancia de clientes  12 - Actualizar los datos de una instancia de producto\n"
                + "  6 - Listar clientes                                    13 - Listar productos\n"
                + "  7 - Comprobaciones de igualdad con clientes            14 - Comprobaciones de igualdad con producto\n"

                + String.format("  %s0 - Salir%s\n", Utils.RED, Utils.RESET)
                + String.format(
                        "\nElija una opción (%s0 para salir%s) > ",
                        Utils.RED, Utils.RESET);

        int opcion = Keyboard.readInt(opciones);
        return opcion;
    }

    private static void crearClienteV1(){
        Cliente c = new Cliente();
        clientes.add(c);
    }

    private static void crearClienteV2(){
        Cliente c = new Cliente("C01", "Carlos Cuesta Iglesias", "3111234321");
        clientes.add(c);
    }

    private static void crearClienteV3(){
        Cliente c = new Cliente("C03");
        clientes.add(c);
    }

    private static void crearClienteV4() {
        String str = """
                    {
                        "identificacion" : "C10",
                        "nombre" : "Jorge Giraldo Gómez",
                        "telefono" : "3119998888"
                    }
                """;
    
        JSONObject json = new JSONObject(str);
        clientes.add(new Cliente(json));
    
    }

    private static void actualizarCliente(){
        int totalClientes = listarClientes();
        if (totalClientes == 0) {
            return;
        }

        int i = 0;
        do {
            i = Keyboard.readInt("Ingrese el índice del cliente a modificar (0-Salir): ");
        } while (i < 0 && i >= totalClientes);

        if (i == 0) {
            return;
        }

        Cliente c = clientes.get(i - 1); // c es una referencia a un elemento de la lista
        System.out.printf("%nCliente a modificar:%n%s%n%n", c);

        String identificacion = Keyboard.readString("Identificación: ");
        if (!identificacion.equals("")) {
            c.setIdentificacion(identificacion);
        }

        String nombre = Keyboard.readString("Nombre: ");
        if (!nombre.equals("")) {
            c.setNombre(nombre);
        }

        String telefono = Keyboard.readString("Teléfono: ");
        if (!telefono.equals("")) {
            c.setTelefono(telefono);
        }
    }

    private static int listarClientes(){
        int i = 0;
        for (Cliente cl : clientes) {
            System.out.printf("%2d >%s%n", ++i, cl);
        }

        if (i == 0) {
            System.out.println("No hay clientes creados");
        }

        return i;
    }

    private static void comrpobacionesClientes(){
        int totalClientes = listarClientes();
        if (totalClientes < 1) {
            System.out.println("Pruebe los casos del 1 al 4 para poder realizar comparaciones");
            return;
        }

        System.out.println("\n------Comprobaciones------");

        Cliente c1 = clientes.get(1);
        Cliente c3 = clientes.get(3);

        System.out.println("\n" + c1 + "\n" + c3);
        System.out.println(c1.equals(c3) ? "Son iguales" : "No son iguales");
        System.out.println();

        c1 = clientes.get(1);
        c3 = clientes.get(1);

        System.out.println("\n" + c1 + "\n" + c3);
        System.out.println(c1 == c3 ? "Son iguales" : "No son iguales");

        c1 = new Cliente(clientes.get(1));
        c3 = new Cliente(clientes.get(1));

        System.out.println("\n" + c1 + "\n" + c3);
        System.out.println(c1 == c3 ? "Son iguales" : "No son iguales");
    }

    private static void crearProductoV1(){
        Producto p = new Producto();
        productos.add(p);
    }

    private static void crearProductoV2(){
        Producto p = new Producto("P001", "MAGUI", TipoProducto.HARINAS, 15000, 17000, 4, LocalDate.of(2023, 10, 20));
        productos.add(p);
    }

    private static void crearProductoV3(){
        Producto p = new Producto("P002");
        productos.add(p);
    }

    private static void crearProductoV4(){
        String str = """
                {
                    "id" : "P003",
                    "nombre" : "tomate",
                    "tipo" : "FRUTAS_Y_VERDURAS",
                    "valorBase" : "4000",
                    "valorVenta" : "4500",
                    "disponibles" : "20",
                    "vence" : "2022-01-01"
                }
                """;
        
        JSONObject json = new JSONObject(str);
        productos.add(new Producto(json));
    }

    private static int listarProductos(){
        int i = 0;
        for (Producto pr : productos) {
            System.out.printf("%2d > %s%n", ++i, pr);
        }
        
        if (i == 0) {
            System.out.println("No hay productos creados");
        }
        
        return i;
    }
    
    private static void actualizarProductos(){
        listarProductos();
        int totalProductos = productos.size();
        if (totalProductos == 0) {
            System.out.println("No hay productos existentes");
            return;
        }

        int i = 0;
        do {
            i = Keyboard.readInt("Ingrese el índice del producto a modificar (0-Salir): ");
        } while (i < 0 && i >= totalProductos);

        if (i == 0) {
            return;
        }

        Producto productoModificar = productos.get(i - 1);
        System.out.println("\nActualización de producto: \n" + productoModificar.toString());

        String id = Keyboard.readString("Id: ");
        if (!id.equals("")) {
            productoModificar.setId(id);
        }

        String nombre = Keyboard.readString("Nombre: ");
        if (!nombre.equals("")) {
            productoModificar.setNombre(nombre);
        }
        
        TipoProducto tipo = Keyboard.readEnum(TipoProducto.class, "Tipo: ");
        if (tipo != TipoProducto.OTROS) {
            productoModificar.setTipo(tipo);
        }

        double valorBase = Keyboard.readDouble(0, 10000000, "Valor base: ");
        if (valorBase != 0) {
            productoModificar.setValorBase(valorBase);
        }

        double valorVenta = Keyboard.readDouble(0, 10000000, "Valor venta: ");
        if (valorVenta != 0) {
            productoModificar.setValorVenta(valorVenta);
        }

        int disponibles = Keyboard.readInt(0, 100, "Disponibles: ");
        productoModificar.setDisponibles(disponibles);
 
        LocalDate vence = Keyboard.readDate("Fecha vencimiento: (AAAA-MM-DD)");
        LocalDate fechaActual = LocalDate.now();
        long diasDiferencia = fechaActual.until(vence).getDays();

        if (diasDiferencia >= 5 && diasDiferencia <= 700) {
            productoModificar.setVence(vence);
        }

        System.out.println("Producto modificado correctamente");
    }

    private static void comparacionesProductos(){
        int totalProductos = listarProductos();
        if (totalProductos < 1) {
            System.out.println("Pruebe los casos del 8 al 13 para poder realizar comparaciones");
            return;
        }

        System.out.println("\n------Comprobaciones------");

        Producto p1 = productos.get(1);
        Producto p3 = productos.get(3);

        System.out.println("\n" + p1 + "\n" + p3);
        System.out.println(p1.equals(p3) ? "Son iguales" : "No son iguales");
        System.out.println();

        p1 = productos.get(1);
        p3 = productos.get(1);

        System.out.println("\n" + p1 + "\n" + p3);
        System.out.println(p1 == p3 ? "Son iguales" : "No son iguales");

        p1 = new Producto(productos.get(1));
        p3 = new Producto(productos.get(1));

        System.out.println("\n" + p1 + "\n" + p3);
        System.out.println(p1 == p3 ? "Son iguales" : "No son iguales");
    }
}
