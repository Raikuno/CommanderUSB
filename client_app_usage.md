[volver](./README.md)
# Uso
La aplicación de cliente actuará de manera pasiva, no ofrece ningún tipo de interfaz más haya del instalador. Cualquier acción o cambio de configuración deberá ser realizado desde el servidor.<br>
La aplicación de cliente se encargará principalmente de dos tareas fundamentales, siendo estas controlar que no se inserten memorias usb en el equipo y enviar información al servidor sobre el estado de la máquina.<br>
El control de uso de memorias usb se lleva a cabo evitando el montaje de estas de forma automática, algo que ocurria en circunstancias normales en un entorno de windows. Esto lo lleva a cabo mmediante modificaciones en la entrada de registro de windows:  Computer\HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\USBSTOR\Start. Este valor, por defecto, suele tenerr un valor de 3, habilitando el montaje automático de las memorias usb. Usb Commander, se encargá de cambiar el valor a 4, lo que evita que las memorias se monten automáticamente. La aplicación revisará constantemente las entradas del registro de windows, de tal forma que si se produjese alguna modificación o se montase alguna unidad de memoria usb, se bloquearía nuevamente en el registro de windows, se desmontarían todas las unidades de memoria usb, se generaría un registro con la información previa a la aplicación de medidas de seguridad, y se enviarían todos los registros almacenados en la entrada dedicada a la aplicación del visor de eventos de windows.<br>

Con respecto a los registros generados por la aplicación, estos son almacenados en una entrada dedicada en el visor de eventos de windows, como ya se ha mencionado anteriormente. Esta decisión fue tomada debido a la seguridad que aporta esta herramienta, pues un usuario no podrá normalmente crear un registro en nombre de la aplicación ni podrá eliminar registros ya existentes, puese necesitaría eliminar todos los registros generados, por lo que resultaría obvio al observar la interfaz de la aplicación de Servidor que se ha producido una manipulación en los registros. De igual manera, el almacenamiento de registros es temporal y una medida de seguridad alternativa en caso de que la máquina sea incapáz de conectarse al servidor por algun motivo, pues al momento de generar un registro, sea este generado por una infracción de seguridad provocada por un usuario malintencionado, o por ser el registro regularmente generado cada cierta cantidad de tiempo, cada vez que se genera una entrada de registro, se envían todas las entradas almacenadas en el equipo al servidor, vaciando la entrada pertinente del visor de eventos de windows para evitar almacenar datos obsoletos.<br>

## Registros
Los registros generados por la aplicación de cliente de UsbCommander, registrar la siguiente información:
<table>
<thead>
    <tr>
        <th>usbValue</th>
        <th>usbAllowed</th>
        <th>usbList</th>
        <th>creationDate</th>
        <th>errorMessage</th>
        <th>code</th>
    </tr>
</thead>
<tbody>
    <tr>
        <td>Valor en el registro del estado del montaje de memorias usb externas (3 o 4)</td>
        <td>Campo booleano que indica si se permitían o no montar memorias usb al momento de generar el registro</td>
        <td>Lista de dispositivos usb conectados, incluyendo nombre y letra del punto de montaje</td>
        <td>Fecha de creación del registro</td>
        <td>Mensaje de error en caso de que el registro sea de tipo error</td>
        <td>El tipo de registro generador</td>
    </tr>
</tbody>
</table>
Los tipos de registros que la aplicación puede generar por el momento son los siguientes:

<table>
<tr>
    <th>Código</th>
    <th>Descripción</th>
    <th>Condiciones</th>
</tr>
<tr>
    <td>
        1001
    </td>
    <td>
        Registro de carácter informativo. No contiene ninguna actividad que requiera revisión
    </td>
    <td>
        Puede ser generado en los registros regulares
    </td>
</tr>

<tr>
    <td>
        1002
    </td>
    <td>
        Indica que alguien a modificado la configuración de la aplicación. Requiere de revisión urgente
    </td>
    <td>
        Puede ser generado en los registros generados por el código dedicado a observar las entradas del registro de windows
    </td>
</tr>
<tr>
    <td>
        1003
    </td>
    <td>
        Indica que alguien a modificado una entrada de registro relacionada con el montaje de memorias usb. Requiere de revisión urgente
    </td>
    <td>
        Puede ser generado en los registros generados por el código dedicado a observar las entradas del registro de windows o por los registros regulares
    </td>
</tr>

<tr>
    <td>
        1005
    </td>
    <td>
        Indica que una memoria se encuentra conectada a la máquina, independientemente de si esta montada o no
    </td>
    <td>
        Puede ser generado en los registros regulares
    </td>
</tr>
<tr>
    <td>
        1006
    </td>
    <td>
        Representa un problema de conexión. 
    </td>
    <td>
        Puede ser generado al producirse un error relacionado a la conexión con el servidor de la aplicación
    </td>
</tr>
<tr>
    <td>
        1007
    </td>
    <td>
        Representa un error en la aplicación. Contiene un mensaje de error 
    </td>
    <td>
        Puede ser generado al producirse un error en la aplicación
    </td>
</tr>
</table>


<br><br>

Todos los valores que requieren de persistencia para asegurar el funcionamiento de la aplicación, es decir, la configuración de la aplicación. Se almacena en el propio registro de windows en la ruta: Computer\HKEY_LOCAL_MACHINE\SOFTWARE\UsbCmmdr
Los valores almacenados son los siguientes: dirección ip del servidor, el puerto del socket del servidor y la frecuenciia con la que se generarán los registros de estado regulares.
Tanto el puerto como la ip se almacenan al momento de instalar la aplicación, sin embargo, la frecuencia de los registros regulares se almacena al iniciar la aplicaciónn por primera vez.