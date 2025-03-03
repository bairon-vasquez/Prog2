
export default class Ventas {
  static #salesTable // referencia a la instancia de Tabulator
  static #modal // referencia a una ventana emergente de confirmación
  static #products // el array con la información de productos
  static #customer // el array de clientes

  constructor() {
      throw new Error('No utilice el constructor. Use Ventas.init()')
  }

  static async #getProducts(){
          let response = await Helpers.fetchData(`${urlAPI}/productos`)
          Ventas.#products = response.data

          //const fechasVencidas = Ventas.#products.filter((productos) => productos.vence > DateTime.now())
          //const productosNoVencidos = Ventas.#products.filter((productos) => productos.vence > DateTime.now().toISO({ includeOffset: false}))
          const productosNoVencidosyDisponibles = Ventas.#products.filter(p => DateTime.fromISO(p.vence).endOf('day') >= DateTime.now().endOf('day') && p.disponibles > 0)
          console.log(productosNoVencidosyDisponibles)
          Ventas.#products = productosNoVencidosyDisponibles

          // en la variable local listProducts mapear Ventas.#products.map para obtener un array con los nombres de los productos
          const listProducts = Ventas.#products.map(({ nombre }) => nombre)
          console.log(listProducts)

          return listProducts
  }

  /**
   * Carga los datos de los clientes y productos para disponer las entradas de datos de la venta y sus líneas de venta
   */
  static async init() {
      try {
          // asignar a Ventas.#customer la data de clientes obtenida mediante fetchData()
          let response = await Helpers.fetchData(`${urlAPI}/clientes`)
          Ventas.#customer = response.data
          
          // asignar a Ventas.#products la data de clientes obtenida mediante fetchData(), excluir los vencidos
          //const listProducts = Ventas.#getProducts()

          
          // inyectar a <main> ./resources/html/ventas.html
          document.querySelector('main').innerHTML = await Helpers.loadPage('./resources/html/ventas.html')
          
          // asignar a la fecha del formulario la fecha y hora actual
          //const fechaHora = new Date().toLocaleString('es-CO', { timeZone: 'America/Bogota'})
         /*  const fechaHora = new Date().toISOString().slice(0, 16)
          document.querySelector('#fecha').value = fechaHora
          console.log(fechaHora) */
          document.querySelector('#form-ventas #fecha').value = DateTime.now().toISO({ includeOffset: false})
      
          // asignar al seleccionable de clientes del formulario datos a partir de Ventas.#customer
          //console.log(Ventas.#customer)
          /* for (let index = 0; index < Ventas.#customer.length; index++) {
              var option = `<option value="${Ventas.#customer[index].identificacion}">${Ventas.#customer[index].nombre}</option>`
              document.querySelector('#cliente').innerHTML += option
          } */
          document.querySelector('#form-ventas #cliente').innerHTML = Helpers.toOptionList({
              items: Ventas.#customer,
              value: 'identificacion',
              text: 'nombre',
              firstOption: 'Seleccione un cliente'
          })

          // asignar a Ventas.#salesTable la instancia de Tabulator
          Ventas.#salesTable = new Tabulator('main #ventas > #tabla-ventas', {
              height: 'calc(70vh - 150px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
              //data: [{ cantidad: null, producto: null, valorUnitario: null, valorTotal: null }],
              layout: 'fitColumns', // ajustar columnas al ancho de la tabla (opcional)
              columns: [
                  { title: 'Cantidad', field: 'cantidad', width: 95, hozAlign: 'center', editor: 'number', cellEdited: Ventas.#updateRow, editorParams: { min: 1, max: 1000 }, validator: ['required', 'min:1', 'max:1000'] },
                  { title: 'Producto', field: 'producto', editor: 'list', validator: 'required', editorParams: { values: await Ventas.#getProducts(), autocomplete: true }, cellEdited: Ventas.#updateRow },
                  { title: 'Vr. unitario', field: 'valorUnitario', hozAlign: 'right', width: 100 },
                  { title: 'Vr. total', field: 'valorTotal', width: 130, bottomCalc: 'sum', hozAlign: 'right' },
                  { formatter: Ventas.#deleteRowButton, width: 40, hozAlign: 'center', cellClick: Ventas.#deleteRowClick }
              ],
              // agregar a la tabla los botones para registrar venta, crear nueva línea de venta y crear venta
              footerElement: `
                  <div class='container-fluid d-flex justify-content-end p-0'>
                      <button id='save-sale' class='btn btn-success btn-sm me-2'>${icons.mineCartLoaded}&nbsp;&nbsp;Registrar venta</button>
                      <button id='add-row' class='btn btn-info btn-sm me-2'>${icons.plusSquare}&nbsp;&nbsp;Nueva línea</button>
                      <button id='new-sale' class='btn btn-warning btn-sm'>${icons.mineCart}&nbsp;&nbsp;Nueva venta</button>
                  </div>
              `.trim()
          })

          Ventas.#salesTable.on('tableBuilt', () => {
              // asignar los listener de los botones del footer de la tabla
              document.querySelector('#save-sale').addEventListener('click', Ventas.#saveSale)
              document.querySelector('#add-row').addEventListener('click', Ventas.#addRow)
              document.querySelector('#new-sale').addEventListener('click', Ventas.#newSale)
          })
      } catch (e) {
          Toast.info({ message: 'Sin acceso a la opción de ventas', mode: 'danger', error: e })
      }

      return this
  }

  /**
   * Cuando se ingresa una cantidad o se elige un producto, se actualiza el precio unitario y el subtotal de la fila
   * @param {Cell} cell
   */
  static #updateRow(cell) {
      // obtener en la variable local rowData, los datos de la línea de venta a partir del método getData() de la fila de la celda
      const rowData = cell.getRow().getData();
      // asignar a la variable local product el producto de Ventas.#products que coincida con el nombre de la fila actual
      const product = Ventas.#products.find((producto) => producto.nombre === rowData.producto)
  
      // Si se encontró el producto...
      if (product) {
          //verificar si la cantidad a vender no es mayor a los disponibles
         /*  Hola: hay varios escenarios posibles, 1)  asignar a la línea de venta la cantidad 0 para obligar al usuario a que 
         elija un valor <= a lo disponible, 2) asignar una cantidad == a lo disponible o 3) al momento de registrar la venta, 
         volver a validar las líneas de venta.
           En cualquiera de los escenarios avisar en un Toast del hecho. */
          if (rowData.cantidad > product.disponibles) {
              Toast.info({message: 'La cantidad no puede ser mayor a los disponibles de la tienda', mode: 'danger'})
              rowData.cantidad = product.disponibles
          }
          //    asignar en rowData.valorUnitario el valor de venta de product
          rowData.valorUnitario = product.valorVenta;
          //    asignar en rowData.valorTotal el subtotal de la línea actual
          rowData.valorTotal = rowData.cantidad * rowData.valorUnitario;
          //    utilizar el método update(rowData) de la fila actual para actualizarla
          cell.getRow().update(rowData);
      }

  }

  /**
   * Dispone el formulario y la tabla de líneas de venta para ingresar una nueva venta
   */
  static #newSale() {
      // utilizar table.getData() para asignar a la variable salesLines la data de la tabla
      const salesLines = Ventas.#salesTable.getData()
      // asigna la la variable someDataInRow true si al menos una fila tiene asignada la cantidad o el producto
      const someDataInRow = salesLines.some(row => row.cantidad || row.producto)
      // si someDataInRow pide confirmación para crear la nueva venta...
      if (someDataInRow) {
          Ventas.#modal = new Modal({
              title: "Nueva venta",
              content: 'Hay una venta en curso. ¿Continuar de todos modos?',
              buttons: [
                  {
                      id: "nueva",
                      style: "btn-danger",
                      html: `${icons.trashTwo.replace('w-10 h-10', 'w-15 h-15')}Nueva venta`,
                      callBack: () => {
                          Ventas.#modal.dispose()
                          Ventas.clearForm()
                      }
                  },
                  {
                      id: "cancel-new",
                      html: `${icons.xLg}&nbsp;Cancelar`,
                      style: "btn btn-secondary",
                      callBack: () => Ventas.#modal.dispose(),
                  }
              ]
          }).show()
      } else {
          Ventas.clearForm()
      }
  }

  /**
   * Limpia las entradas del formulario
   */
  static clearForm() {
      // refresca la fecha y la hora actual en el formulario
      document.querySelector('#form-ventas #fecha').value = DateTime.now().toISO({ includeOffset: false})
      // establecer en la data de la tabla sólo una línea en blanco
      Ventas.#salesTable.setData([{ cantidad: null, producto: null, valorUnitario:null, valorTotal: null}])
      // La lista seleccionable de clientes muestra vuelve al elemento 0: "Seleccione un cliente"
      Ventas.selectableClientsAtIndex0()
  }

  /**
   * Verifica que los datos del formulario y de la tabla sean correctos y envía una solicitud POST con dichos datos
   */
  static async #saveSale() {
      // verifica que hay un cliente seleccionado y avisa y sale si no es así
      const select = document.getElementById('cliente')
      if (select.selectedIndex === 0) {
          Toast.info({message: 'Debe seleccionar un cliente', mode: 'warning'})
          return
      }
      const cliente = select.value

      // verifica que haya líneas de venta y avisa y sale si no es así
      const salesLines = Ventas.#salesTable.getData()
      if (salesLines.length === 0) {
          Toast.info({message:'No hay lineas de venta para guardar', mode: 'warning'})
          return
      }

      console.log(Ventas.#allCellsFilled())

      if (!Ventas.#allCellsFilled()) {
          Toast.info({message: 'Hay lineas incompletas', mode:'warning'})
          return
      }

      //referencia en la variable local salesLines las líneas de venta
      //cambia en el array salesLines los nombres de los productos por los ID de los productos
      salesLines.forEach(row => {
          row.producto = Ventas.#products.find(p => p.nombre === row.producto).id
      })
      
      //estructura un objeto local data así: { venta: { fecha: f, cliente: c }, detalle: salesLines }
      const data = {
          venta: {
              fecha: document.getElementById('fecha').value,
              cliente: cliente
          },
          detalle: salesLines
      }
      console.log(data);

      //intenta enviar la solicitud POST de la venta incluyendo en el body la data
      try {
          let response = await Helpers.fetchData(`${urlAPI}/ventas`, {
              method: 'POST',
              body: data
          })

          if (response.message === 'ok') {
              Ventas.#salesTable.getColumnDefinitions()[1].editorParams.values = await Ventas.#getProducts()
              console.log(response);
              Ventas.clearForm()
              Toast.info({message: 'Venta registrada!', mode: 'success'})
          }else{
              Toast.info({message: 'No se pudo agregar la venta', mode: 'danger', error: response})
          }
      } catch(e) {
          Toast.info({message: 'Error registrando venta', mode: 'danger', error: e})
      }
      //    avisa del éxito o del fracaso de la petición
      // si no
      //    avisa de la incompletitud de los datos
  }

  /**
   * Forza un evento change sobre el seleccionable de clientes para volver al elemento "Seleccione un cliente"
   */
  static selectableClientsAtIndex0() {
      // en la variable local selectList referencia al seleccionable de clientes'
      const selectList = document.getElementById('cliente')
      console.log(selectList)
      // posiciona al seleccionable en el índice 0
      selectList.selectedIndex = 0
      // forza el evento change
      const event = new Event('change')
      selectList.dispatchEvent(event)
  }

  /**
   * agrega un fila a la tabla de líneas de venta
   */
  static #addRow() {
      // Obtener las líneas de venta
      const salesLines = Ventas.#salesTable.getData()

      // Verificar si la tabla no tiene filas o todas las filas tienen datos
      const allRowsFilled = salesLines.every(row => row.cantidad !== null && row.producto !== null)

      if (!allRowsFilled) {
          // Avisar al usuario del por qué no se crea la nueva línea de venta
          Ventas.#modal = new Modal({
              title: 'Advertencia',
              content: 'Complete los datos en todas las filas antes de agregar una nueva línea de venta.'
          }).show()
          return 
      }

      // Adicionar una nueva línea de venta con datos nulos
      Ventas.#salesTable.addData([{ cantidad: null, producto: null, valorUnitario: null, valorTotal: null }])
  }

  /**
   * Crear un button con el icono de eliminar para utilizar en las filas de la tabla de líneas de venta
   */
  static #deleteRowButton = (cell, formatterParams, onRendered) => `
  <button id="delete-row" 
      class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Eliminar"
  >${icons.delete}</button>`

  /**
   * Si se pulsa el botón de eliminar una línea de venta llamar a delete() sobre la celda de la fila actual
   * @param {Event} e
   * @param {Cell} cell
   */
  static #deleteRowClick(e, cell) {
      // Mostrar una confirmación antes de eliminar la fila
      Ventas.#modal = new Modal({
          title: "Eliminar línea de venta",
          content: '¿Está seguro de que desea eliminar esta línea de venta?',
          buttons: [
              {
                  id: "confirm-delete",
                  style: 'btn-danger',
                  html: `${icons.trashTwo}&nbsp;Eliminar`,
                  callBack: () => {
                      Ventas.#modal.dispose();
                      Ventas.#salesTable.deleteRow(cell.getRow());
                      Toast.info({ message: 'Línea de venta eliminada', mode: 'success' })
                  }
              },
              {
                  id: "cancel-delete",
                  html: `${icons.xLg}&nbsp;Cancelar`,
                  style: "btn btn-secondary",
                  callBack: () => Ventas.#modal.dispose(),
              }
          ]
      }).show()
  }

  /**
   *
   * @returns
   */
  static #allCellsFilled() {
      // obtener en la variable local  salesLines las líneas de venta, mediante getData()
      const salesLines = Ventas.#salesTable.getData();
      console.log(salesLines[0].cantidad)
      // retornar la verificación de si todas las filas tienen la cantidad y el producto
      return salesLines.some(row => row.cantidad > 0 && row.producto)
  }
}
