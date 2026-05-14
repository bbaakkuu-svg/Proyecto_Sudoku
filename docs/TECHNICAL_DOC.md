# 📖 Documentación Técnica - Sudoku Elite (RA4)

Este documento proporciona una visión profunda de la arquitectura y el diseño del sistema Sudoku Elite.

## 🏛️ Arquitectura del Sistema

Sudoku Elite está diseñado siguiendo una arquitectura monolítica modular, organizada en paquetes que separan las responsabilidades:

- **Logic (`com.sudoku.elite`)**: El motor central del juego, validación de reglas y generación de tableros.
- **UI (`com.sudoku.elite`)**: Interfaz gráfica construida con Java Swing, implementando el patrón *Observer* implícito mediante callbacks.
- **Persistence (`com.sudoku.elite`)**: Capa de acceso a datos (DAO) para MySQL y gestión de sesiones.

## 🚀 Guía de Instalación y Ejecución

### Requisitos Previos
- **JDK 17** o superior.
- **Maven 3.8+**.
- **MySQL 8.0** (opcional para el modo con persistencia).

### Instalación
1. Clonar el repositorio.
2. Configurar las variables de entorno para la base de datos (ver `DatabaseConnection.java`).
3. Ejecutar `mvn clean install`.

### Ejecución
```bash
mvn exec:java
```

## 🛠️ Stack Tecnológico
- **Core**: Java 17.
- **GUI**: Swing + AWT.
- **Build Tool**: Maven.
- **Persistence**: MySQL + HikariCP.
- **Testing**: JUnit 5 + JaCoCo.

## 📈 Calidad del Código (RA2.c)
El proyecto mantiene un estándar de calidad mediante:
1. **Javadoc**: Documentación técnica detallada en cada clase y método público.
2. **CI/CD**: Integración con GitHub Actions para validación automática de documentación.
3. **Patrones de Diseño**: Uso extensivo de *Command*, *DAO* y *Singleton*.

---
*Para ver el modelado visual del sistema, consulte el documento de [Modelado UML](DOCUMENTATION.md).*
