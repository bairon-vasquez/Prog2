# Cómo se importaron estas utilidades

Enseguida se describe cómo se importaron las utilidades de terceros necesarias para el proyecto:

## Bootstrap
Se ingresó a [Bootstrap 5.3.2](https://github.com/twbs/bootstrap) > Code > [Download ZIP](https://github.com/twbs/bootstrap/archive/refs/heads/main.zip).

Luego de descomprimir el archivo descargado, se copiaron los archivos *./dist/js/bootstrap.min.js*  y *./dist/js/bootstrap.min.js.map* en *./resources/utils/bootstrap*.

## Bootswatch
Se ingresó a [Bootswatch](https://bootswatch.com/) para dar una mirada a los temas alternivos a los oficiales de Bootstrap. Seguidamente, en la misma página, se eligió [GitHub](https://github.com/thomaspark/bootswatch/) > Code > [Download ZIP](https://github.com/thomaspark/bootswatch/archive/refs/heads/v5.zip).

Luego de descomprimir el archivo descargado, se copió de la carpeta *dist/flatly/* en *./resources/css/* los archivos bootstrap.min.css y bootstrap.min.css.map

Por supuesto, puede elegir cualquier otro tema disponible en la carpeta ./dist

## Popper
Se ingresó a [@popperjs/core CDN files](https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/), se pusó clic derecho sobre *popper.min.js* se eligió la opción *guardar enlace como* y finalmente se eligió la carpeta *./resources/utils/popper*. Igual procedimiento de descarga se hizo con el archivo *popper.min.js.map*.

## Tabulator
De [GitHub](https://github.com/olifolkerd/tabulator) > Code > [Download ZIP](https://github.com/olifolkerd/tabulator/archive/refs/heads/master.zip).

Luego de descomprimir el archivo descargado, se copió la carpeta *dist* en *./resources/utils/* y se cambió el nombre *dist* por *tabulator*.

## Luxon
En la página de [Luxon](https://moment.github.io/luxon/#/install?id=es6) se dio clic derecho en [Download minified](https://moment.github.io/luxon/es6/luxon.min.js) > *Guardar enlace como*. Esta acción abre un cuadro de diálogo que permite guardar el archivo *luxon.min.js* en la carpeta *./resources/utils/luxon/* 

## Librería propia
Los archivos JS que se encuentran en la carpeta *own*, corresponden a utilidades creadas por Carlos Cuesta Iglesias, algunas veces tomando como base otras fuentes que son referenciadas en el código.
