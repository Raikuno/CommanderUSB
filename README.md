[![CC BY 4.0][cc-by-shield]][cc-by]
# Usb Commander
This work is licensed under a
[Creative Commons Attribution 4.0 International License][cc-by].

[![CC BY 4.0][cc-by-image]][cc-by]

[cc-by]: http://creativecommons.org/licenses/by/4.0/
[cc-by-image]: https://i.creativecommons.org/l/by/4.0/88x31.png
[cc-by-shield]: https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg

La finalidad de este proyecto es la de ofrecer un sistema de supervisión y un cierto grado de control para aquellos equipos que, 
ya sea por contener información sensible o por cualquier otro motivo, no se desee que se permita una conexión USB con dispositivos de almacenamiento externo.

El principio fundamental sobre el que trabaja esta aplicación es que cualquiera que realmente busque saltarse una medida de seguridad, terminará lograndolo, por ello, USB Commander se centra en ofrecer una forma rápida, sencilla y efectiva para conocer el estado de las unidades de memoria USB conectadas a las máquinas contraladas por la aplicación, o si se encuentra una unidadUSB conectada en primer lugar.

## Estructura
USB Commander se divide en dos aplicaciones:
- **Cliente**: Como su nombre indica, esta aplicación se instala en aquellos ordenadores a los que se quiera iniciar el seguimiento y se encargará de enviar la información a la aplicación de servidor. A pesar de ser USB Commander una aplicación dedicada a la supervisión, la aplicación de cliente ofrece medidas de seguridad para proteger el sistema, bloqueando el montaje de memoriass usb y desmontando aquellas que se detecten montadas.
- **Servidor**: Esta aplicación será la que deba ejecutarse en el equipo que hara de servidor. El servidor se encargará de recibir la información de estado de las aplicaciones de cliente, además de mostrarla en una interfaz web. La aplicación de servidor ofrece un sistema de cuentas que permite dividir tareas en diferentes cuentas llevadas por diferentes personas, que podrán trabajar de forma simultanea mediante la interfaz web que ofrece la aplicación.
<br>

#

En el caso de que se desee modificar el código de la aplicación, en la carpeta de `Documentation` se encuentra la documentación del código de la aplicación de cliente y servidor en formato pdf y html.
<br><br>
- [Instalación de la aplicación de servidor](./server_app_installation.md)
- [Uso de la aplicación de servidor](./server_app_usage.md)
<br>

- [Instalación de la aplicación de cliente](./client_app_installation.md)
- [Uso de la aplicación de cliente](./client_app_usage.md)

