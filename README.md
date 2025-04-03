# 📌 API de Franquicias

## 📖 Descripción
Este proyecto consiste en la construcción de una API para gestionar una lista de franquicias.

### 📌 Características
Una franquicia está compuesta por:
- **Nombre**
- **Listado de sucursales**

Cada **sucursal** está compuesta por:
- **Nombre**
- **Listado de productos ofertados**

Cada **producto** incluye:
- **Nombre**
- **Cantidad de stock**

### ✅ Criterios de Aceptación
1. Exponer un endpoint para agregar una nueva franquicia.
2. Exponer un endpoint para agregar una nueva sucursal a una franquicia.
3. Exponer un endpoint para agregar un nuevo producto a una sucursal.
4. Exponer un endpoint para eliminar un producto de una sucursal.
5. Exponer un endpoint para modificar el stock de un producto.
6. Exponer un endpoint que permita mostrar cuál es el producto con más stock por sucursal dentro de una franquicia específica. El endpoint debe devolver un listado de productos indicando a qué sucursal pertenece.

---

## 🚀 Requisitos
Antes de ejecutar el proyecto, asegúrate de tener instalados los siguientes componentes:

- 🐳 **Docker** (para contenedores de base de datos)
- 🐧 **WSL** (Windows Subsystem for Linux, en caso de usar Windows)
- 🛢️ **PostgreSQL**
- ☕ **Java 17 o superior**
- 📦 **Gradle** (para la gestión de dependencias)

## 🛠️ Tecnologías Utilizadas
Este proyecto utiliza las siguientes tecnologías:

- **Lenguaje**: Java Reactivo 17
- **Framework**: Spring Boot
- **Reactivo**: Spring WebFlux
- **ORM**: R2DBC (Reactive Relational Database Connectivity)
- **Utilidad**: Lombok (para reducir código repetitivo)
- **Documentación**: Postman
- **Pruebas unitarias**: JUnit 5
- **Arquitectura**: Arquitectura Limpia
- **Contenedores**: Docker

---

## ⚡ Ejecución del Proyecto

### 📥 Clonar el Repositorio
```bash
https://github.com/CrisAcosMoya/API-Franchise.git
```

### 🐘 Levantar Contenedores Adicionales
Asegúrate de ejecutar los siguientes contenedores para que el proyecto funcione correctamente:

#### PostgreSQL (Base de datos)
```bash
docker run --name postgres_db \
-e POSTGRES_PASSWORD=1234 \
-e POSTGRES_USER=root \
-e POSTGRES_DB=base \
-d -p 5432:5432 \
-v C:\Users\docker\volumes\postgres:/var/lib/postgresql/data \
-d postgres
```

### 🛢️ Ejecutar Script en la Base de Datos
El script necesario para la configuración inicial de la base de datos se encuentra en:
```
applications/app-service/src/main/resources
```

### ▶️ Ejecutar el Proyecto en Local
Asegúrate de que el puerto configurado sea **8080**. Luego de que la base de datos esté funcionando correctamente, ejecuta el servidor Spring Boot con:
```bash
mvn spring-boot:run
```

---

## 📌 Acceso a la Colección de Postman
Para realizar pruebas con la API, puedes acceder a la colección de Postman ubicada en la raíz del repositorio con el siguiente nombre:
```
REST API Franquicias.postman_collection
```

¡Listo! Ahora puedes probar la API de franquicias. 🚀


