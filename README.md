Shield: [![CC BY-SA 4.0][cc-by-sa-shield]][cc-by-sa]

![Build and Test status:](https://github.com/mvargas33/Eolian-Auriga-backend/actions/workflows/mvnCI.yml/badge.svg)

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License][cc-by-sa]. 

[cc-by-sa]: http://creativecommons.org/licenses/by-sa/4.0/
[cc-by-sa-image]: https://licensebuttons.net/l/by-sa/4.0/88x31.png
[cc-by-sa-shield]: https://img.shields.io/badge/License-CC%20BY--SA%204.0-lightgrey.svg

Our aim as a team is to share this software as an academic material, and contribute to solar car's community. We also want to give attribution to the people who put the effort to make this software possible. You may license your contributions to adaptations of this BY-SA 4.0 work under [GPLv3 Licence][GPLv3], it's BY-SA 4.0 compatible.

[GPLv3]: https://www.gnu.org/licenses/gpl-3.0.html

# Eolian Auriga Telemetry System
The telemetry system runs in a Raspberry Pi inside the car. On one hand, it uses a Java Application to receive data from the car and other logistics tasks. On the other hand, it runs a VueJS application to visualize data and interact with the users. The system uses a middleware written in JS to communicate back-end and front-end.

# Dependencies and configuration

Para correr la aplicación se necesita:
* jdk 8u212 (o menor).
* Apache Maven

En un inicio el proyecto importaba sus dependencias del mismo repositorio. En un punto se añadió soporte para Apache Maven. Aunque de todas formas se dejaron las librerias utilizadas en ese entonces dentro de la carpeta `libs`.

(TODO: Queda pendiente comprobar que ya no se necesitan las librerías y con el archivo `pom.xml` es suficiente para tener una aplicación autocontenida).

(TODO: Documentar que hay que configurar el setting.xml del mvn para dejar la libreria de las xbee en la whitelist)

# Como correr la aplicación

Crear ejecutable:

`mvn clean compile assembly:single`

Ejecutar archivo generado:

`java -jar OUT.jar --in <1> --out <2> --xbee <3>`

1. Path (absoluto o relativo). Especifica desde donde cargar los archivos .csv de los componentes.
2. Path (absoluto o relativo). Especifica donde guardar los datos leídos.
3. Puerto donde esta conectada la xbee.


Actualmente el proyecto usa un plugin de Maven para empaquetarse, el cuál entrega un archivo `.jar` ejecutable. El archivo que se ejecuta al correr el archivo esta definido en el archivo `pom.xml` en el path `project.build.pluginManagement.plugins.plugin.configuration.archive.manifest.mainClass`.

# Generar javadocs

Desde el directorio `src/main/java` ejecutar el comando `javadoc -d ../../../docs -subpackages *`. Se generará una carpeta `docs` en el directorio root del proyecto donde se puede ver la documentación como una página web al abrir el archivo `index.html`.