  export default class Clientes {
  static #table;
  static #modal;
  static #currentOption;
  static #form;

  constructor() {
    throw new Error("No utilice el constructor. Use Clientes.init()");
  }
  static async init() {
    try {
      Clientes.#form = await Helpers.loadPage("./resources/html/clientes.html");
      const response = await Helpers.fetchData(`${urlAPI}/clientes`);
      console.log(`message: "${response.message}"`);
      response.data.forEach((cliente) => console.log(cliente));

      document.querySelector("main").innerHTML = `
            <div class="p-2 w-full">
                <div id="clientes" class="m-2"></div>
            </dv>
            `;

      Clientes.#table = new Tabulator("#clientes", {
        height: "calc(100vh - 190px)", // establecer la altura mejora la velocidad
        data: response.data, // asignar a la tabla los datos obtenidos en el paso 4
        layout: "fitColumns", // ajustar columnas al ancho de la tabla (opcional)
        columns: [
          // definir las columnas de la tabla
          {
            title: "IDENTIFICACIÓN",
            field: "identificacion",
            width: 150,
            hozAlign: "center",
          },
          { title: "NOMBRE", field: "nombre", hozAlign: "center" },
          { title: "TELÉFONO", field: "telefono", hozAlign: "center" },

          {
            formatter: Clientes.#editRowButton,
            width: 40,
            hozAlign: "center",
            cellClick: Clientes.#editRowClick,
          },

          {
            formatter: Clientes.#deleteRowButton,
            width: 40,
            hozAlign: "center",
            cellClick: Clientes.#deleteRowClick,
          },
        ],

        // agregar un botón al final de la tabla
        footerElement: `
                    <div class='container-fluid d-flex justify-content-end p-0'>
                        <button id='add-clientes' class='btn btn-info btn-sm'>
                           ${icons.plusSquare}&nbsp;&nbsp;Nuevo cliente</button>
                    </div>
                `.trim(),
      });
      //Botón nuevo cliente
      Clientes.#table.on("tableBuilt", () =>
        document
          .querySelector("#add-clientes")
          .addEventListener("click", Clientes.#addRow)
      );
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la opción de clientes",
        mode: "danger",
        error: e,
      });
    }

    return this; // esta instrucción queda fuera del bloque try {}
  }
  static #editRowButton = () => `
  <button id="edit-row" 
          class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar"
  >${icons.edit}</button>`;

  //luego se debe comentar
  static #editRowClick = (e, cell) => {
    //console.log("Editar: ", cell.getRow().getData());
    Clientes.#currentOption = "edit";
    Clientes.#modal = new Modal({
      size: "default",
      title: "Actualizar cliente",
      content: Clientes.#form,
      buttons: [
        {
          id: "edit",
          style: "btn btn-primary",
          html: `${icons.editWhite}&nbsp;&nbsp;<span>Actualizar</span>`,
          callBack: () => Clientes.#edit(cell),
        },
        {
          id: "cancel-edit",
          style: "btn btn-secondary",
          html: `${icons.xLg}<span>Cancelar</span>`,
          callBack: () => Clientes.#modal.dispose(),
        },
      ],
      built: (idModal) =>
        Clientes.#toComplete(idModal, cell.getRow().getData()),
    }).show();
  };

  static #deleteRowButton = (cell, formatterParams, onRendered) => `
      <button id="delete-row" 
              class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Eliminar"
      >${icons.delete}</button>`;

  static #deleteRowClick = (e, cell) => {
    //console.log('Eliminar', cell.getRow().getData())
    Clientes.#currentOption = "delete";
    (Clientes.#modal = new Modal({
      size: "default",
      title: "Eliminar un cliente",
      content: `
                Confirme la eliminación del cliente:<br>
                ${cell.getRow().getData().identificacion} - ${
        cell.getRow().getData().nombre
      }<br>
            `,
      buttons: [
        {
          id: "delete",
          style: "btn btn-danger",
          html: `${icons.deleteWhite}<span>Eliminar</span>`,
          callBack: () => Clientes.#delete(cell),
        },
        {
          id: "cancel-delete",
          style: "btn btn-secundary",
          html: `${icons.xLg}<span>Cancelar</span>`,
          callBack: () => Clientes.#modal.dispose(),
        },
      ],
    })).show();
  };
  static async #edit(cell) {
    //console.log('Enviar petición PUT para: ', cell.getRow().getData()); --- modificar ---
    //taller 9 - 15
    if (!Helpers.okForm("#form-clientes")) {
      return;
    }

    // asignar en la variable data, los datos del formulario
    const data = Clientes.#getFormData();
    // estructurar la url para enviar la solicitud PUT
    const url = `${urlAPI}/clientes/${cell.getRow().getData().identificacion}`;

    try {
      // intentar enviar la solicitud de actualización
      let response = await Helpers.fetchData(url, {
        method: "PUT",
        body: data,
      });

      if (response.message === "ok") {
        Toast.info({ message: "Cliente actualizado exitosamente" });
        cell.getRow().update(response.data); // actualizar la fila del cliente
        Clientes.#modal.dispose();
      } else {
        Toast.info({
          message: "No se pudo actualizar el cliente",
          mode: "danger",
          error: response,
        });
      }
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la actualización de clientes",
        mode: "danger",
        error: e,
      });
    }
  }

  static async #delete(cell) {
    const identificacion = cell.getRow().getData().identificacion;
    const url = `${urlAPI}/clientes/${identificacion}`;

    try {
      // enviar la solicitud de eliminación
      let response = await Helpers.fetchData(url, {
        method: "DELETE",
      });

      if (response.message === "ok") {
        Toast.info({ message: "Cliente eliminado exitosamente" });
        cell.getRow().delete(); //elimina la fila del producto
        Clientes.#modal.dispose();
      } else {
        Toast.info({
          message: "No se pudo eliminar el cliente",
          mode: "danger",
          error: response,
        });
      }
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la eliminación de clientes",
        mode: "danger",
        error: e,
      });
    }
  }

  static async #add() {
    // verificar si los datos cumplen con las restricciones del formulario HTML
    if (!Helpers.okForm("#form-clientes")) {
      return;
    }

    const data = Clientes.#getFormData();

    try {
      // enviar la solicitud de creación con los datos del formulario
      let response = await Helpers.fetchData(`${urlAPI}/clientes`, {
        method: "POST",
        body: data,
      });

      if (response.message === "ok") {
        Clientes.#table.addRow(response.data); // agregar el Cliente a la tabla
        Clientes.#modal.dispose();
        Toast.info({ message: "Cliente agregado exitosamente" });
      } else {
        Toast.info({
          message: response.message /* 'No se pudo agregar el cliente' */,
          mode: "danger",
          error: response,
        });
      }
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la creación de clientes",
        mode: "danger",
        error: e,
      });
    }
  }

  static #toComplete(idModal, rowData) {
    /* console.log(`Formularios ${idModal} completado`)
      console.log('Datos para el formulario: ', rowData) */
    if (Clientes.#currentOption === "edit") {
      document.querySelector(`#${idModal} #identificacion`).disabled = true;
      document.querySelector(`#${idModal} #nombre`).value = rowData.nombre;
      document.querySelector(`#${idModal} #telefono`).value = rowData.telefono;
    }
  }

  static #getFormData() {
    const identificacion = document.querySelector(
      `#${Clientes.#modal.id} #identificacion`
    ).value;
    const nombre = document.querySelector(
      `#${Clientes.#modal.id} #nombre`
    ).value;
    const telefono = document.querySelector(
      `#${Clientes.#modal.id} #telefono`
    ).value;

    return { identificacion, nombre, telefono };
  }
  static #addRow = async (e) => {
    Clientes.#currentOption = "add";
    Clientes.#modal = new Modal({
      size: "default",
      title: "Agregar un cliente",
      content: Clientes.#form,
      buttons: [
        {
          id: "add-clientes",
          style: "btn btn-primary",
          html: `${icons.plusSquare}&nbsp;&nbsp;<span>Agregar</span>`,
          callBack: Clientes.#add,
        },
        {
          id: "cancel-add-clientes",
          style: "btn btn-secondary",
          html: `${icons.xLg}<span>Cancelar</span>`,
          callBack: () => Clientes.#modal.dispose(),
        },
      ],
      built: Clientes.#toComplete,
    }).show();
  };
}
