# 🧩 Proyecto Sudoku Elite - Java Edition

Este proyecto consiste en el desarrollo de una aplicación completa de Sudoku utilizando **Java 17** y la biblioteca **Swing** para la interfaz gráfica. El objetivo es aplicar conocimientos de programación orientada a objetos, algoritmos de búsqueda y persistencia de datos.

## 🛠️ Características Principales

- **Motor Lógico**: Generación automática de tableros mediante un algoritmo de **Backtracking**.
- **Dificultad Variable**: Niveles Fácil, Medio y Difícil con validación de reglas en tiempo real.
- **Interfaz Premium**: Diseño moderno en modo oscuro con soporte para múltiples temas.
- **Sistema de Deshacer/Rehacer**: Implementación del patrón de diseño **Command**.
- **Persistencia**: Almacenamiento de usuarios y rankings en base de datos **MySQL**.
- **Seguridad**: Cifrado de contraseñas mediante el algoritmo **SHA-256**.

## 🚀 Ejecución

Para compilar y ejecutar el proyecto, asegúrate de tener instalado **Maven** y ejecuta:

```bash
mvn compile exec:java
```

Para generar el archivo ejecutable (JAR):

```bash
mvn clean package
java -jar target/sudoku-elite-app-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 📚 Documentación Técnica

La documentación detallada sobre la arquitectura y el diseño de clases se encuentra en la carpeta `docs/`.
