// importaciones estáticas
// los import locales de JS tienen rutas relativas a la ruta del script que hace el enrutamiento
import * as Popper from "../utils/popper/popper.min.js";
import * as bootstrap from "../utils/bootstrap/bootstrap.min.js";
import { TabulatorFull as Tabulator } from "../utils/tabulator/js/tabulator_esm.min.js";
import { DateTime, Duration } from "../utils/luxon/luxon.min.js";
import icons from "../utils/own/icons.js";
import Helpers from "../utils/own/helpers.js";
import Modal from "../utils/own/modal.js";

class App {
  static async main() {
    //punto 7 taller 7
    window.DateTime = DateTime;
    window.Duration = Duration;
    window.Helpers = Helpers;
    window.Tabulator = Tabulator;
    window.Modal = Modal;
    window.icons = icons;
    window.current = null;

    //punto 8 taller 7
    const config = await Helpers.fetchData("./resources/assets/config.json");
    console.log(config);

    //punto 9 taller 7
    window.urlAPI = config.url;
    console.log(`URL de la aplicación: ${urlAPI}`);

    //punto 10 taller 7
    const { default: Toast } = await import(`../utils/own/toast.js`);
    window.Toast = new Toast();

    //punto 11 taller 7
    const listOptions = document.querySelectorAll("#main-menu a");
    console.log(listOptions);

    //punto 13 taler 7
    //listOptions.forEach(item => console.log(item))
    listOptions.forEach((item) =>
      item.addEventListener("click", App.#mainMenu)
    );
  }

  //punto 12 talles 7
  static async #mainMenu(e) {
    let option = "ninguna";
    if (e !== undefined) {
      e.preventDefault();
      option = e.target.text;
    }
    //punto 15 taller 7
    switch (option) {
      case "Realizar venta":
/*         Toast.info({
          message: `Falta implementar el acceso a ${option}`,
          mode: "success",
        }); */ //punto 16 taller 7
        const{ default: Ventas} = await import(`./ventas.js`);
        current = await Ventas.init();
        break;
      case "Clientes":
        //Toast.info({ message: `Falta implementar el acceso a ${option}` });
        const { default: Clientes } = await import(`./clientes.js`);
        current = await Clientes.init();
        console.log(`current es una instancia de tipo ${current.name}`);
        break;
      case "Productos":
        // Toast.info({ message: `Falta implementar el acceso a ${option}` })
        //punto 18 taller 7
        const { default: Productos } = await import(`./productos.js`);
        current = await Productos.init();
        console.log(`current es una instancia de tipo ${current.name}`);

        break;

      case "Informe de ventas":
        const { default: InfoVentas } = await import(`./infoventas.js`);
        current = await InfoVentas.init();
        break;
      case "Acerca de...":
          document.querySelector("main").innerHTML = await Helpers.loadPage("./resources/html/acercaDe.html");
          break
      default:
        console.warn("Fallo en ", e.target);
        Toast.info({
          message: `No hay un caso para la opción ${option} `,
          mode: "warning",
        });
    }
  }
}

await App.main();
