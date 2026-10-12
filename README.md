# Hospital Vida Salud — T2

Sistema académico de gestión hospitalaria con microservicios, Spring Boot,
Spring Cloud Gateway, Eureka, WebFlux, R2DBC, SQL Server y RabbitMQ.

## Arquitectura

| Servicio | Puerto | Función |
|---|---:|---|
| eureka-server | 8761 | Registro y descubrimiento |
| api-gateway | 8080 | Entrada central a la API |
| paciente-service | 8081 | Gestión de pacientes |
| cita-service | 8082 | Gestión de citas |
| notificacion-service | 8083 | Consumo de eventos de citas |

Base de datos: HOSPITAL_VIDA_SALUD.

## Tecnologías

- Java 21
- Spring Boot y Spring Cloud Gateway
- Eureka Discovery Server
- Spring WebFlux y Project Reactor
- Spring Data R2DBC
- SQL Server
- RabbitMQ
- Maven y Gradle
- GitHub Actions

## API de pacientes

| Método | Ruta | Acción |
|---|---|---|
| GET | /api/pacientes | Listar pacientes |
| GET | /api/pacientes/{id} | Consultar paciente |
| POST | /api/pacientes | Registrar paciente |
| PUT | /api/pacientes/{id} | Actualizar paciente |

Se comprueba la información requerida y se evita duplicar pacientes según
el tipo y número de documento.

## API de citas

| Método | Ruta | Acción |
|---|---|---|
| GET | /api/citas | Listar citas |
| GET | /api/citas/{id} | Consultar cita |
| POST | /api/citas | Registrar cita |
| PUT | /api/citas/{id}/estado?estado=CONFIRMADA | Cambiar estado |

Estados contemplados: PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA y AUSENTE.
El servicio valida las transiciones de estado implementadas.

## Flujo de RabbitMQ

1. Se registra una cita pendiente.
2. Se confirma la cita.
3. cita-service publica un evento de confirmación.
4. notificacion-service consume el evento de la cola cita.confirmada.queue.
5. El evento se muestra en la consola como demostración de notificación.

Exchange: hospital.exchange.
Routing key: cita.confirmada.

La demostración no envía correos ni SMS reales.

## Configuración local

Requisitos: JDK 21, SQL Server, la base HOSPITAL_VIDA_SALUD y RabbitMQ
disponible en el puerto 5672.

Las propiedades de conexión utilizan estas variables de entorno:
- DB_USERNAME
- DB_PASSWORD
- RABBITMQ_USERNAME
- RABBITMQ_PASSWORD

Configura los valores reales localmente y nunca publiques contraseñas
ni tokens en el repositorio.

## Ejecutar los servicios

Desde la carpeta de cada proyecto Maven (eureka-server, api-gateway
o paciente-service), ejecuta:

    .\mvnw.cmd spring-boot:run

Desde la carpeta de cada proyecto Gradle (cita-service o
notificacion-service), ejecuta:

    .\gradlew.bat bootRun

Inicia primero SQL Server, RabbitMQ y Eureka; después, los otros servicios.

## Ejecutar pruebas

En cada proyecto Maven:

    .\mvnw.cmd test

En cada proyecto Gradle:

    .\gradlew.bat test

Ejecuta cada comando desde la carpeta correspondiente.

## Integración continua

El workflow .github/workflows/ci.yml ejecuta las pruebas de los cinco
proyectos al hacer push o abrir un pull request hacia main o master.

Consulta los resultados en la pestaña Actions de GitHub.

Se documentaron tres ejecuciones: la inicial exitosa, un fallo de prueba
introducido intencionalmente y la restauración exitosa de la prueba original.

## Seguridad

- No publicar credenciales ni tokens.
- No incluir archivos .env con valores reales.
- target y build se excluyen mediante .gitignore.
- Usar únicamente datos ficticios en las demostraciones.

## Repositorio

https://github.com/carlosantonio-cpu/HOSPITAL_VIDA_SALUD_T2