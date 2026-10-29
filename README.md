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
api.key=123456
```

Ajustar las credenciales según el entorno local.

## Ejecución del proyecto

Desde la raíz del proyecto, ejecutar en Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

La API estará disponible en:

`http://localhost:8080`

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

## Ejemplos de solicitudes

### Agregar un riesgo

`POST /polizas/4/riesgos`

Encabezados:

```text
x-api-key: 123456
Content-Type: application/json
```

Body:

```json
{
  "descripcion": "Vehículo asegurado"
}
```

### Renovar una póliza

`POST /polizas/1/renovar`

La renovación incrementa el canon y la prima según el IPC registrado en la póliza y actualiza su estado a `RENOVADA`. El IPC se interpreta como porcentaje; por ejemplo, `5.2` representa un incremento del 5,2 %.

### Cancelar una póliza

`POST /polizas/1/cancelar`

La póliza pasa a estado `CANCELADA` y sus riesgos asociados pasan a estado `CANCELADO`.

### Simular evento al CORE

`POST /core-mock/evento`

Body:

```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

El endpoint registra en los logs el intento de envío del evento. No realiza una integración real con un sistema CORE externo.

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

- `400 Bad Request`: datos inválidos o reglas de negocio incumplidas.
- `401 Unauthorized`: API Key ausente o inválida.
- `404 Not Found`: póliza inexistente al consultar sus riesgos.

## Notas

- Hibernate actualiza el esquema de la base de datos mediante `spring.jpa.hibernate.ddl-auto=update`.
- Los datos de prueba deben crearse según las necesidades de cada ejecución.
- El endpoint de CORE es una simulación para el reto técnico.
