# API de Gestión de Pólizas

API REST desarrollada con **Java 21, Spring Boot y MySQL** para la gestión de pólizas de seguros y sus riesgos asociados.

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- MySQL / MariaDB
- Postman para pruebas de endpoints

## Requisitos previos

- Java 21 instalado.
- MySQL o MariaDB en ejecución.
- Base de datos `gestion_polizas` creada.
- Maven Wrapper incluido en el proyecto.

## Configuración de la base de datos

Crear la base de datos:

```sql
CREATE DATABASE gestion_polizas;
```

Verificar la configuración de `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_polizas
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
api.key=${API_KEY:123456}
```

Ajustar las credenciales según el entorno local.

La API Key utiliza `123456` como valor predeterminado. También puede configurarse mediante la variable de entorno `API_KEY`.

## Ejecución del proyecto

Desde la raíz del proyecto, ejecutar en Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

La API estará disponible en:

`http://localhost:8080`

Para ejecutar las pruebas automatizadas:

```powershell
.\mvnw.cmd test
```

Para ejecutar la verificación completa:

```powershell
.\mvnw.cmd clean verify
```

## Autenticación

Todos los endpoints requieren el encabezado:

| Header | Valor de prueba |
|---|---|
| `x-api-key` | `123456` |

La clave se configura mediante la propiedad `api.key`. Para un entorno real, debe manejarse mediante variables de entorno o un mecanismo seguro de configuración.

## Endpoints disponibles

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/polizas` | Lista todas las pólizas. |
| GET | `/polizas?tipo=COLECTIVA` | Filtra por tipo. |
| GET | `/polizas?estado=ACTIVA` | Filtra por estado. |
| GET | `/polizas?tipo=COLECTIVA&estado=ACTIVA` | Filtra por tipo y estado. |
| GET | `/polizas/{id}/riesgos` | Consulta los riesgos de una póliza. |
| POST | `/polizas/{id}/renovar` | Renueva una póliza aplicando el IPC. |
| POST | `/polizas/{id}/cancelar` | Cancela una póliza y sus riesgos asociados. |
| POST | `/polizas/{id}/riesgos` | Agrega un riesgo a una póliza colectiva. |
| POST | `/riesgos/{id}/cancelar` | Cancela un riesgo individual. |
| POST | `/core-mock/evento` | Simula el envío de un evento al CORE mediante logs. |

## Configuración de Postman

Para consumir los endpoints localmente, utiliza:

- **Base URL:** `http://localhost:8080`
- **API Key:** `123456`

Agrega estos encabezados a todas las solicitudes:

| Header | Valor |
|---|---|
| `x-api-key` | `123456` |
| `Content-Type` | `application/json` (para solicitudes POST con JSON) |

Los ejemplos siguientes utilizan IDs de referencia. Deben reemplazarse por identificadores existentes en la base de datos y con el estado adecuado para cada prueba.

## Ejemplos de consumo y pruebas

### 1. Listar pólizas

**Método:** `GET`

**Endpoint:**

```http
http://localhost:8080/polizas
```

Lista todas las pólizas registradas.

**Prueba con filtros:**

```http
http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA
```

**Escenarios de prueba:**

| Escenario | Resultado esperado |
|---|---|
| Consultar todas las pólizas con API Key válida | `200 OK` |
| Filtrar por tipo y estado | `200 OK` |
| Filtro sin coincidencias | `200 OK`, lista vacía |
| No enviar API Key | `401 Unauthorized` |
| Enviar API Key incorrecta | `401 Unauthorized` |

### 2. Consultar riesgos de una póliza

**Método:** `GET`

**Endpoint:**

```http
http://localhost:8080/polizas/2/riesgos
```

Consulta los riesgos asociados a la póliza indicada.

**Escenarios de prueba:**

| Escenario | Resultado esperado |
|---|---|
| Consultar una póliza existente | `200 OK` |
| Consultar una póliza existente con riesgos | `200 OK`, lista de riesgos |
| No enviar API Key | `401 Unauthorized` |
| Enviar API Key incorrecta | `401 Unauthorized` |

Para comprobar la consulta, utiliza una póliza que tenga al menos un riesgo asociado.

### 3. Renovar una póliza

**Método:** `POST`

**Endpoint:**

```http
http://localhost:8080/polizas/1/renovar
```

La renovación incrementa el canon y la prima según el IPC registrado en la póliza y actualiza su estado a `RENOVADA`.

El IPC se interpreta como porcentaje; por ejemplo, `5.2` representa un incremento del 5,2 %.

**Escenarios de prueba:**

| Escenario | Resultado esperado |
|---|---|
| Renovar una póliza existente no cancelada | `200 OK` |
| Renovar una póliza cancelada | `400 Bad Request` |
| Renovar una póliza inexistente | `404 Not Found` |
| No enviar API Key | `401 Unauthorized` |

**Validación esperada:**

- El estado debe cambiar a `RENOVADA`.
- El canon debe incrementarse según el IPC.
- La prima debe incrementarse según el IPC.

Mensaje esperado al intentar renovar una póliza cancelada:

```json
{
  "mensaje": "No se puede renovar una póliza cancelada"
}
```

### 4. Cancelar una póliza

**Método:** `POST`

**Endpoint:**

```http
http://localhost:8080/polizas/1/cancelar
```

La póliza pasa a estado `CANCELADA` y sus riesgos asociados pasan a estado `CANCELADO`.

**Escenarios de prueba:**

| Escenario | Resultado esperado |
|---|---|
| Cancelar una póliza existente | `200 OK` |
| Cancelar una póliza inexistente | `404 Not Found` |
| No enviar API Key | `401 Unauthorized` |
| Enviar API Key incorrecta | `401 Unauthorized` |

**Validación esperada:**

Después de cancelar la póliza, consultar sus riesgos mediante:

```http
GET http://localhost:8080/polizas/1/riesgos
```

Todos los riesgos asociados deben tener estado `CANCELADO`.

> Esta operación modifica los registros. Utiliza una póliza preparada para pruebas.

### 5. Agregar un riesgo a una póliza

**Método:** `POST`

**Endpoint:**

```http
http://localhost:8080/polizas/4/riesgos
```

Permite agregar un riesgo únicamente si la póliza existe, es de tipo `COLECTIVA` y no está cancelada.

**Headers:**

```text
x-api-key: 123456
Content-Type: application/json
```

**Body:**

```json
{
  "descripcion": "Vehículo asegurado"
}
```

**Escenarios de prueba:**

| Escenario | Resultado esperado |
|---|---|
| Agregar riesgo a una póliza colectiva activa | `201 Created` |
| Enviar descripción vacía | `400 Bad Request` |
| Enviar descripción con espacios en blanco | `400 Bad Request` |
| Agregar riesgo a una póliza individual | `400 Bad Request` |
| Agregar riesgo a una póliza cancelada | `400 Bad Request` |
| Utilizar una póliza inexistente | `404 Not Found` |
| No enviar API Key | `401 Unauthorized` |

**Mensajes esperados para las validaciones:**

Descripción obligatoria:

```json
{
  "mensaje": "La descripción del riesgo es obligatoria"
}
```

Póliza individual:

```json
{
  "mensaje": "Solo se pueden agregar riesgos a pólizas colectivas"
}
```

Póliza cancelada:

```json
{
  "mensaje": "No se pueden agregar riesgos a una póliza cancelada"
}
```

### 6. Cancelar un riesgo

**Método:** `POST`

**Endpoint:**

```http
http://localhost:8080/riesgos/1/cancelar
```

Cambia el estado del riesgo indicado a `CANCELADO`.

**Escenarios de prueba:**

| Escenario | Resultado esperado |
|---|---|
| Cancelar un riesgo existente | `200 OK` |
| Cancelar un riesgo inexistente | `404 Not Found` |
| No enviar API Key | `401 Unauthorized` |
| Enviar API Key incorrecta | `401 Unauthorized` |

Para verificar el resultado, consulta los riesgos de la póliza a la que pertenece el riesgo.

### 7. Simular evento al CORE

**Método:** `POST`

**Endpoint:**

```http
http://localhost:8080/core-mock/evento
```

**Headers:**

```text
x-api-key: 123456
Content-Type: application/json
```

**Body:**

```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

El endpoint registra en los logs el intento de envío del evento.

**Escenarios de prueba:**

| Escenario | Resultado esperado |
|---|---|
| Enviar evento con API Key válida | `200 OK` |
| No enviar API Key | `401 Unauthorized` |
| Enviar API Key incorrecta | `401 Unauthorized` |

**Respuesta exitosa:**

```json
{
  "mensaje": "Evento recibido por el CORE simulado"
}
```

El endpoint no realiza una integración real con un sistema CORE externo; su propósito es simular la recepción del evento y registrar la operación.

## Reglas de negocio

- Las pólizas pueden ser de tipo `INDIVIDUAL` o `COLECTIVA`.
- Los riesgos solo pueden agregarse a pólizas colectivas.
- No se permite renovar una póliza cancelada.
- Al cancelar una póliza, todos sus riesgos asociados se cancelan.
- No se permite agregar riesgos a una póliza cancelada.
- La descripción del riesgo es obligatoria.
- Todos los endpoints requieren una API Key válida.

## Manejo de errores

Las respuestas de error utilizan un objeto JSON con la propiedad `mensaje`.

Ejemplo:

```json
{
  "mensaje": "La descripción del riesgo es obligatoria"
}
```

| Código HTTP | Descripción |
|---|---|
| `400 Bad Request` | Datos inválidos o reglas de negocio incumplidas. |
| `401 Unauthorized` | API Key ausente o inválida. |
| `404 Not Found` | Recurso solicitado inexistente, según la operación. |

## Recomendaciones para ejecutar las pruebas

1. Ejecutar primero los endpoints GET para identificar los IDs y estados actuales.
2. Validar la seguridad utilizando una API Key correcta, una incorrecta y una solicitud sin el encabezado.
3. Agregar un riesgo a una póliza colectiva activa y comprobar su creación.
4. Probar las validaciones de negocio con pólizas individuales y canceladas.
5. Renovar una póliza activa y comparar los valores de canon y prima antes y después.
6. Cancelar una póliza con varios riesgos y verificar que todos queden cancelados.
7. Probar la cancelación individual de un riesgo.
8. Verificar la recepción del evento mediante el endpoint del CORE simulado.

Se recomienda utilizar registros preparados para pruebas en las operaciones que modifican información, como renovar y cancelar pólizas.

## Notas

- Hibernate actualiza el esquema de la base de datos mediante `spring.jpa.hibernate.ddl-auto=update`.
- Los datos de prueba deben crearse según las necesidades de cada ejecución.
- El endpoint de CORE es una simulación para el reto técnico.
- Los ejemplos de IDs son ilustrativos y deben ajustarse a los datos disponibles en la base de datos.