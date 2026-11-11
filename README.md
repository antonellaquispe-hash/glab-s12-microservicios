# Laboratorio S12: comunicación entre microservicios

Solución del laboratorio Spring Boot con Eureka, cuatro servicios independientes, persistencia aislada y llamadas HTTP mediante OpenFeign. Pedido guarda únicamente los identificadores de cliente y producto; su consulta obtiene el detalle en tiempo real sin relaciones JPA entre servicios.

## Requisitos

- Java 17 y Maven 3.9 o superior. Este entorno solo tenía Java 8 y no tenía Maven ni Docker, por lo que la compilación local no fue posible.
- Cada servicio usa su propio archivo de base de datos H2, persistido en `data/`; no requiere MySQL ni credenciales.
- Docker y Docker Compose son opcionales.

## Puesta en marcha

Desde la raíz, ejecutar `mvn clean package`. Después, iniciar en este orden con `mvn -pl eureka-server spring-boot:run`, y luego categoria-service, producto-service, cliente-service y pedido-service. Eureka está en http://localhost:8761; los servicios usan los puertos 8082, 8083, 8084 y 8085 respectivamente.

Para Docker, primero ejecutar `mvn clean package` y después `docker compose -f compose.yaml up --build`. Compose asigna los JAR ya compilados y conecta cada servicio con Eureka por nombre.

## Prueba de punta a punta

Crear una categoría, cliente y producto antes del pedido. Utilizar los IDs devueltos en lugar de asumir que sean 1.

```http
POST http://localhost:8082/api/categorias
Content-Type: application/json
{"nombre":"Tecnología","estado":"ACTIVO"}

POST http://localhost:8084/api/clientes
Content-Type: application/json
{"nombre":"Carlos","apellidos":"Ramirez","direccion":"Lima","estado":"ACTIVO","nroDni":"12345678"}

POST http://localhost:8083/api/productos
Content-Type: application/json
{"nombre":"Laptop","precio":2500,"stock":10,"categoriaId":1}

POST http://localhost:8085/api/pedidos
Content-Type: application/json
{"clienteId":1,"productoId":1,"cantidad":2}

GET http://localhost:8085/api/pedidos/1
```

La respuesta de pedido contiene los datos del pedido, cliente y producto obtenidos mediante OpenFeign y registrados en Eureka.

## Endpoints

- Categoría: `GET/POST /api/categorias`, `GET/PUT/DELETE /api/categorias/{id}`.
- Producto: `GET/POST /api/productos`, `GET/PUT/DELETE /api/productos/{id}` y `GET /api/productos/{id}/detalle` para agregar la categoría vía Feign.
- Cliente: CRUD en `/api/clientes`; el DNI debe tener ocho dígitos exactos.
- Pedido: `GET/POST /api/pedidos`, `GET /api/pedidos/{id}` y `PATCH /api/pedidos/{id}/estado?valor=ATENDIDO`. Estados permitidos: PENDIENTE, ATENDIDO y ANULADO.
- POST exitoso responde 201, GET exitoso 200, validación 400 y recurso inexistente 404. Errores internos o indisponibilidad de servicios se reportan como 5xx.

## Conclusiones

1. Eureka permite localizar servicios por nombre sin codificar sus direcciones físicas.
2. OpenFeign simplifica las llamadas HTTP y mantiene la comunicación en los límites REST.
3. Cada servicio conserva su propio modelo y almacenamiento, evitando acoplar entidades y bases de datos.
4. Los DTO y las validaciones rechazan datos inválidos antes de persistirlos.
5. Enriquecer los pedidos da una vista integrada y requiere que cliente y producto estén disponibles.

Cada servicio almacena datos de forma aislada. Para un despliegue real, externalizar credenciales y configurar un motor de base de datos por servicio. El primer intento de llamada entre servicios puede requerir reintento mientras Eureka registra las instancias.
