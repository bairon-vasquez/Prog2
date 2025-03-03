//punto 17 taller 7
export default class Productos {
  //punto 10 taller 8
  static #table;
  static #modal;
  static #currentOption;
  static #form;
  static #tipos;
  constructor() {
    throw new Error("No utilice el constructor. Use Productos.init()");
  }
  //punto 4 taller 8
  static async init() {
    //punto 18 taller9
    try {
      //punto 12 taller 9
      Productos.#tipos = await Helpers.fetchData(`${urlAPI}/categorias`);
      //punto 10 taller 9
      Productos.#form = await Helpers.loadPage(
        "./resources/html/producto.html"
      );
      const response = await Helpers.fetchData(`${urlAPI}/productos`);
      // console.log(response)
      //punto 5 taller 8
      console.log(`message: "${response.message}"`);
      //console.log("data:", response.data);
      //punto 6 taller 8
      response.data.forEach((producto) => console.log(producto));
      //otra version del paso anterior
      /*     response.data.forEach(function (producto) {
          console.log(producto)
      }) */

      //punto 7 taller 8
      //document.querySelector('main').innerHTML = `${response.data[0].nombre}`
      //punto 8 taller 8
      document.querySelector("main").innerHTML = ` <div class="p-2 w-full">
          <div id="productos" class="m-2"></div>
      </dv>`;

      Productos.#table = new Tabulator("#productos", {
        height: "calc(100vh - 190px)", // establecer la altura mejora la velocidad
        data: response.data, // asignar a la tabla los datos obtenidos en el paso 4
        layout: "fitColumns", // ajustar columnas al ancho de la tabla (opcional)
        columns: [
          // definir las columnas de la tabla
          { title: "CÓDIGO", field: "id", width: 150, hozAlign: "center" },
          { title: "NOMBRE", field: "nombre", hozAlign: "left" },
          { title: "TIPO", field: "tipoEnum", hozAlign: "left" },
          {
            title: "DISP.",
            field: "disponibles",
            hozAlign: "left",
            hozAlign: "center",
          },
          {
            title: "VENCIMIENTO",
            field: "vence",
            width: 150,
            hozAlign: "center",
          },
          { title: "Vr. BASE", field: "valorBase", hozAlign: "center" },
          { title: "Vr. VENTA", field: "valorVenta", hozAlign: "center" },

          //punto 3 taller 9
          {
            formatter: Productos.#editRowButton,
            width: 40,
            hozAlign: "center",
            cellClick: Productos.#editRowClick,
          },
          //punto 5 taller 9
          {
            formatter: Productos.#deleteRowButton,
            width: 40,
            hozAlign: "center",
            cellClick: Productos.#deleteRowClick,
          },
        ],
        // agregar un botón al final de la tabla
        footerElement: `
              <div class='container-fluid d-flex justify-content-end p-0'>
                  <button id='add-productos' class='btn btn-info btn-sm'>
                     ${icons.plusSquare}&nbsp;&nbsp;Nuevo producto</button>
              </div>
          `.trim(),
      });

      //punto 23 taller 9
      Productos.#table.on("tableBuilt", () =>
        document
          .querySelector("#add-productos")
          .addEventListener("click", Productos.#addRow)
      );
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la opción de productos",
        mode: "danger",
        error: e,
      });
    }

    return this; // esta instrucción queda fuera del bloque try {}
  }

  //punto 1 taller 9
  static #editRowButton = () => `
      <button id="edit-row" 
              class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar"
      >${icons.edit}</button>`;
  //punto 2 taller 9
  /*   static #editRowClick = (e, cell) => {
      console.log("Editar: ", cell.getRow().getData());
    }; */
  //punto 8 taller 9
  static #editRowClick = (e, cell) => {
    Productos.#currentOption = "edit";
    Productos.#modal = new Modal({
      size: "default",
      title: "Actualizar producto",
      content: Productos.#form,
      buttons: [
        {
          id: "edit",
          style: "btn btn-primary",
          html: `${icons.editWhite}&nbsp;&nbsp;<span>Actualizar</span>`,
          callBack: () => Productos.#edit(cell),
        },
        {
          id: "cancel-edit",
          style: "btn btn-secondary",
          html: `${icons.xLg}<span>Cancelar</span>`,
          callBack: () => Productos.#modal.dispose(),
        },
      ],
      built: (idModal) =>
        Productos.#toComplete(idModal, cell.getRow().getData()),
    }).show();
  };

  //punto 4 taller 9
  static #deleteRowButton = (cell, formatterParams, onRendered) => `
      <button id="delete-row" 
              class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Eliminar"
      >${icons.delete}</button>`;

  static #deleteRowClick = (e, cell) => {
    // console.log("Eliminar: ", cell.getRow().getData());
    Productos.#currentOption = "delete";
    (Productos.#modal = new Modal({
      title: "Eliminar un producto",
      content: `
           Confirme la eliminación del producto:<br>
           ${cell.getRow().getData().id} - ${cell.getRow().getData().nombre}<br>
       `,
      buttons: [
        {
          id: "delete",
          style: "btn btn-danger",
          html: `${icons.deleteWhite}<span>Eliminar</span>`,
          callBack: () => Productos.#delete(cell),
        },
        {
          id: "cancel-delete",
          style: "btn btn-secundary",
          html: `${icons.xLg}<span>Cancelar</span>`,
          callBack: () => Productos.#modal.dispose(),
        },
      ],
    })).show();
  };
  //punto 6 taller 9

  static async #edit(cell) {
    // verificar si los datos cumplen con las restricciones del formulario html
    if (!Helpers.okForm("#form-productos")) {
      return;
    }

    // asignar en la variable data, los datos del formulario
    const data = Productos.#getFormData();
    // estructurar la url para enviar la solicitud PUT
    const url = `${urlAPI}/productos/${cell.getRow().getData().id}`;

    try {
      // intentar enviar la solicitud de actualización
      let response = await Helpers.fetchData(url, {
        method: "PUT",
        body: data,
      });

      if (response.message === "ok") {
        Toast.info({ message: "Producto actualizado exitosamente" });
        cell.getRow().update(response.data); // actualizar la fila del producto
        Productos.#modal.dispose();
      } else {
        Toast.info({
          message: "No se pudo actualizar el producto",
          mode: "danger",
          error: response,
        });
      }
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la actualización de productos",
        mode: "danger",
        error: e,
      });
    }
  }

  //punto 7 taller 9
  static #toComplete(idModal, rowData) {
    /* console.log(`Formularios ${idModal} completado`)
      console.log('Datos para el formulario: ', rowData) */
    //punto 13 taller 9
    console.log(Productos.#tipos.data);
    const tipos = Helpers.toOptionList({
      items: Productos.#tipos.data,
      value: "key",
      text: "value",
      selected: Productos.#currentOption === "edit" ? rowData.tipo : "",
    });

    document.querySelector(`#${idModal} #tipo`).innerHTML = tipos;

    //punto 11 taller 9
    if (Productos.#currentOption === "edit") {
      document.querySelector(`#${idModal} #id`).value = rowData.id;
      document.querySelector(`#${idModal} #nombre`).value = rowData.nombre;
      document.querySelector(`#${idModal} #disponibles`).value =
        rowData.disponibles;
      document.querySelector(`#${idModal} #vence`).value = rowData.vence;
      document.querySelector(`#${idModal} #valor-base`).value =
        rowData.valorBase;
      document.querySelector(`#${idModal} #valor-venta`).value =
        rowData.valorVenta;
    }
  }
  //punto 14 taller 9
  static #getFormData() {
    const id = document.querySelector(`#${Productos.#modal.id} #id`).value;
    const nombre = document.querySelector(
      `#${Productos.#modal.id} #nombre`
    ).value;
    const disponibles = document.querySelector(
      `#${Productos.#modal.id} #disponibles`
    ).value;
    const vence = document.querySelector(
      `#${Productos.#modal.id} #vence`
    ).value;
    const valorBase = document.querySelector(
      `#${Productos.#modal.id} #valor-base`
    ).value;
    const valorVenta = document.querySelector(
      `#${Productos.#modal.id} #valor-venta`
    ).value;
    // obtener el texto del elemento seleccionado en la lista de tipos:
    const select = document.querySelector(`#${Productos.#modal.id} #tipo`);
    const tipo = select.options[select.selectedIndex].text;

    return { id, nombre, tipo, disponibles, vence, valorBase, valorVenta };
  }

  //punto 19 taller 9
  static async #delete(cell) {
    const id = cell.getRow().getData().id;
    const url = `${urlAPI}/productos/${id}`;

    try {
      let response = await Helpers.fetchData(url, { method: "DELETE" });

      if (response.message === "ok") {
        Toast.info({ message: "Producto eliminado exitosamente" });
        cell.getRow().delete(); // eliminar la fila del producto
        Productos.#modal.dispose();
      } else {
        Toast.info({
          message: "No se pudo eliminar el producto",
          mode: "danger",
          error: response,
        });
      }
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la eliminación de productos",
        mode: "danger",
        error: e,
      });
    }
  }

  //punto 21 taller 9

  static async #add() {
    // verificar si los datos cumplen con las restricciones del formulario HTML
    if (!Helpers.okForm("#form-productos")) {
      return;
    }

    const data = Productos.#getFormData();

    try {
      // enviar la solicitud de creación con los datos del formulario
      let response = await Helpers.fetchData(`${urlAPI}/productos`, {
        method: "POST",
        body: data,
      });

      if (response.message === "ok") {
        Productos.#table.addRow(response.data); // agregar el producto a la tabla
        Productos.#modal.dispose();
        Toast.info({ message: "Producto agregado exitosamente" });
      } else {
        Toast.info({
          message: "No se pudo agregar el producto",
          mode: "danger",
          error: response,
        });
      }
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la creación de productos",
        mode: "danger",
        error: e,
      });
    }
  }
  //punto 22 taller 9
  static #addRow = async (e) => {
    Productos.#currentOption = "add";
    Productos.#modal = new Modal({
      size: "default",
      title: "Agregar un producto",
      content: Productos.#form,
      buttons: [
        {
          id: "add-productos",
          style: "btn btn-primary",
          html: `${icons.plusSquare}&nbsp;&nbsp;<span>Agregar</span>`,
          callBack: Productos.#add,
        },
        {
          id: "cancel-add-productos",
          style: "btn btn-secondary",
          html: `${icons.xLg}<span>Cancelar</span>`,
          callBack: () => Productos.#modal.dispose(),
        },
      ],
      built: Productos.#toComplete,
    }).show();
  };
}
