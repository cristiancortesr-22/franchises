# Franchise Management API

API reactiva para gestionar una red de franquicias, sucursales y productos.

## Arquitectura

El proyecto sigue **Clean Architecture** basado en el scaffold de Bancolombia:

```
├── domain/
│   ├── model/          → Entidades de dominio, gateways (interfaces)
│   └── usecase/        → Lógica de negocio
├── infrastructure/
│   ├── entry-points/
│   │   └── reactive-web/  → Handlers, Router, DTOs, Validaciones
│   └── driven-adapters/
│       └── r2dbc-postgresql/  → Adaptadores de persistencia, Circuit Breaker
├── applications/
│   └── app-service/    → Configuración Spring Boot, punto de entrada
└── deployment/         → Dockerfile, scripts SQL
```

## Stack Tecnológico

- Java 17
- Spring Boot 3.3.5 + Spring WebFlux (programación reactiva)
- R2DBC PostgreSQL (acceso no bloqueante a BD)
- Resilience4j (Circuit Breaker)
- MapStruct (mapeo de objetos)
- SpringDoc OpenAPI (documentación Swagger)
- Gradle 8.12.1
- Docker

## Requisitos Previos

- Java 17+
- PostgreSQL 16+
- Gradle 8+ (incluido via wrapper)
- Docker (opcional)

## Ejecución Local

### 1. Crear la base de datos

```sql
CREATE DATABASE franchise_db;
```

### 2. Ejecutar el script de tablas

```bash
psql -U postgres -d franchise_db -f deployment/script.sql
```

### 3. Iniciar la aplicación

```bash
DB_HOST=localhost DB_PORT=5432 DB_DATABASE=franchise_db DB_USERNAME=postgres DB_PASSWORD=1234 DB_DRIVER=postgresql ./gradlew bootRun
```

La aplicación inicia en `http://localhost:8080/api/v1`

### 4. Documentación API (Swagger)

- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/v1/v3/api-docs

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/franchise` | Crear franquicia |
| PUT | `/franchise/{id}` | Actualizar nombre de franquicia |
| POST | `/branch` | Agregar sucursal a franquicia |
| PUT | `/branch/{id}` | Actualizar nombre de sucursal |
| POST | `/product` | Agregar producto a sucursal |
| DELETE | `/product/{id}` | Eliminar producto de sucursal |
| PUT | `/product/{id}/stock` | Modificar stock de producto |
| PUT | `/product/{id}/name` | Actualizar nombre de producto |
| GET | `/product/{franchiseId}/top` | Producto con mayor stock por sucursal |

## Ejemplos de Uso

### Crear Franquicia
```bash
curl -X POST http://localhost:8080/api/v1/franchise \
  -H "Content-Type: application/json" \
  -d '{"name": "Mi Franquicia"}'
```

### Agregar Sucursal
```bash
curl -X POST http://localhost:8080/api/v1/branch \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Centro", "franchiseId": 1}'
```

### Agregar Producto
```bash
curl -X POST http://localhost:8080/api/v1/product \
  -H "Content-Type: application/json" \
  -d '{"name": "Producto A", "stock": 100, "branchId": 1}'
```

## Tests

```bash
./gradlew test
```

Los tests unitarios cubren los 3 use cases del dominio con JUnit 5 + Mockito + StepVerifier.

## Docker

### Construir imagen

```bash
./gradlew build -x test
docker build -t franchise-api -f deployment/Dockerfile .
```

### Ejecutar contenedor

```bash
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_DATABASE=franchise_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=1234 \
  -e DB_DRIVER=postgresql \
  franchise-api
```

## Despliegue en AWS con Terraform

La infraestructura se gestiona con Terraform y despliega los siguientes recursos:

- VPC con subnets públicas y privadas
- RDS PostgreSQL 16 (db.t3.micro)
- ECR (registro de imágenes Docker)
- ECS Fargate (contenedor de la aplicación)
- Application Load Balancer (ALB)
- Security Groups, NAT Gateway, CloudWatch Logs

### Requisitos

- AWS CLI configurado con credenciales
- Terraform >= 1.5.0

### Desplegar

```bash
cd infrastructure/terraform
cp terraform.tfvars.example terraform.tfvars
# Editar terraform.tfvars con los valores reales (especialmente db_password)

terraform init
terraform plan
terraform apply
```

### Push de imagen Docker a ECR

```bash
# Obtener URL del ECR del output de Terraform
ECR_URL=$(terraform output -raw ecr_repository_url)

# Login en ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $ECR_URL

# Build y push
./gradlew build -x test
docker build -t franchise-api -f deployment/Dockerfile .
docker tag franchise-api:latest $ECR_URL:latest
docker push $ECR_URL:latest

# Forzar nuevo despliegue en ECS
aws ecs update-service --cluster franchises-dev-cluster --service franchises-dev-service --force-new-deployment
```

### Acceder a la API

Una vez desplegado, la API estará disponible en:
```
http://<ALB_DNS_NAME>/api/v1/swagger-ui.html
```

El DNS del ALB se obtiene con:
```bash
terraform output alb_dns_name
```

### Destruir infraestructura

```bash
terraform destroy
```
