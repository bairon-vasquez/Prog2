export default class InfoVentas {
  constructor() {
    throw new Error("No utilice el constructor. Use Ventas.init()");
  }
  static async init() {
    try {
      const response = await Helpers.fetchData(`${urlAPI}/ventas`);
      console.log(response.data);

      document.querySelector("main").innerHTML = ` <div class="p-2 w-full">
        <div id="info-ventas" class="m-2"></div>
    </dv>`;

      new Tabulator("#info-ventas", {
        height: "calc(100vh - 190px)", // establecer la altura mejora la velocidad
        data: response.data, // asignar a la tabla los datos obtenidos en el paso 4
        layout: "fitColumns", // ajustar columnas al ancho de la tabla (opcional)
        columns: [
          // definir las columnas de la tabla
          { title: "Nro", field: "venta.numero", width: "150", hozAlign: "center", },
          { title: "FECHA", field: "venta.fecha", hozAlign: "center", },
          { title: "CLIENTE", field: "venta.cliente.nombre", hozAlign: "center", },
          { title: "TOTAL", field: "venta.total", hozAlign: "center" },
        ],
        rowFormatter: function (row) {
          //create and style holder elements
          var holderEl = document.createElement("div");
          var tableEl = document.createElement("div");

          holderEl.style.boxSizing = "border-box";
          holderEl.style.padding = "10px 30px 10px 10px";
          holderEl.style.borderTop = "1px solid #333";
          holderEl.style.borderBotom = "1px solid #333";

          tableEl.style.border = "1px solid #333";

          holderEl.appendChild(tableEl);

          row.getElement().appendChild(holderEl);
          if (row.getData().detalle) {
            var subTable = new Tabulator(tableEl, {
              layout: "fitColumns",
              data: row.getData().detalle,
              columns: [
                { title: "Cant.", field: "cantidad" },
                { title: "Nombre", field: "producto.nombre" },
                { title: "Valor", field: "producto.valorVenta", hozAlign: "center" },
                { title: "Subtotal",field: "subtotal",hozAlign: "center",},
              ],
            });
          } else {
            console.log("No hay detalles encontrados");
          }
        },
      });
    } catch (e) {
      Toast.info({
        message: "Sin acceso a la opción de InfoVentas",
        mode: "danger",
        error: e,
      });
    }
    return this; // esta instrucción queda fuera del bloque try {}
  }
}
