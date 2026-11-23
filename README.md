# CAPEBE Vuelos · App Android + microservicio Spring Boot (DSY1105, Evaluación Parcial 4)

App Android (Kotlin, Jetpack Compose, Material 3) del **Club Aéreo del Personal de BancoEstado (CAPEBE)**.
Los socios reservan vuelos recreativos gratuitos; el administrador supervisa socios y reservas.
Los datos de vuelos son ficticios (Ley 19.628).

## 1. Nombre del proyecto
**CAPEBE Vuelos** (`com.example.myappnavigation`)

## 2. Integrantes
- Fransisco
- Javier

Repositorio de la app: _(enlace GitHub)_ · Repositorio del microservicio: _(enlace GitHub)_ · Tablero Trello: _(enlace)_

## 3. Funcionalidades
**Socio**
- Registro e inicio de sesión (formularios validados; la cuenta vive en el microservicio, contraseña con scrypt y sesión con JWT).
- Vuelos disponibles en tarjetas, con filtro por fecha (calendario nativo → `GET /vuelos?fecha=`).
- Detalle del vuelo con el **clima actual de la base** (API externa Open-Meteo).
- Aceptar un vuelo (`PUT /reserva`), cambiarlo por otro, firmar el consentimiento y **cancelar la reserva** (`DELETE /reserva`).
- Pase de abordaje con código QR (ZXing) y opción de agendar en el calendario nativo.
- Confirmación por correo (SMTP o app de correo nativa).

**Administrador** (el rol lo decide el backend por correo, variable `ADMIN_EMAILS`)
- Panel con socios registrados, reservas con estado del consentimiento y vuelos programados.

**CRUD cubierto desde la interfaz:** crear (registro, reserva), leer (vuelos, reserva, panel admin), actualizar (cambiar vuelo, firmar consentimiento), eliminar (cancelar reserva).

## 4. Endpoints utilizados

### Microservicio propio (Spring Boot, `backend-springboot`) — base `http://10.0.2.2:3000/`
| Método | Ruta | Uso | Acceso |
|---|---|---|---|
| POST | `/auth/register` | Crear cuenta | público |
| POST | `/auth/login` | Iniciar sesión (devuelve token y `usuario.rol`) | público |
| GET | `/vuelos?fecha=&disponibles=` | Listar/filtrar vuelos | público |
| GET | `/reserva` | Reserva del socio | token |
| PUT | `/reserva` `{vueloId}` | Aceptar o cambiar vuelo | socio |
| PUT | `/reserva/consentimiento` `{firmado}` | Firmar consentimiento | socio |
| DELETE | `/reserva` | Cancelar reserva | socio |
| GET | `/admin/socios` | Socios registrados | administrador |
| GET | `/admin/reservas` | Todas las reservas | administrador |

### API externa — Open-Meteo (sin API key)
`GET https://api.open-meteo.com/v1/forecast?latitude=..&longitude=..&current=temperature_2m,wind_speed_10m,weather_code`
Se consume con Retrofit (`ClimaApi`) y se muestra en el detalle del vuelo. Es independiente del microservicio propio.

## 5. Pasos para ejecutar
**Microservicio** (JDK 17+ y Maven 3.9+)
```bash
cd backend-springboot
cp .env.example .env        # cambia JWT_SECRET
mvn spring-boot:run         # http://localhost:3000 (SQLite, crea data/vuelos.db y 6 vuelos)
```
**App**
1. Abrir el proyecto en Android Studio y sincronizar Gradle.
2. Con el backend corriendo, ejecutar en el **emulador** (usa `10.0.2.2`). En un celular físico cambia `BACKEND_URL` en `data/remote/ApiConfig.kt` por la IP de tu computador.
3. Registrar una cuenta de socio e iniciar sesión. Para el rol administrador, registrar primero `cashupinadmin@gmail.com` (valor de `ADMIN_EMAILS`).
4. (Opcional) Envío real de correo: copiar `local.properties.example` como `local.properties` y completar `smtp.remitente` y `smtp.clave`. Sin eso se abre la app de correo del dispositivo.

**Pruebas unitarias**
```bash
./gradlew testDebugUnitTest          # JUnit + MockK + coroutines-test
```
Cobertura: en Android Studio, clic derecho en `app/src/test` → *Run Tests with Coverage*; revisar los paquetes `data.repository` y `ui.viewmodel`.
Cubren repositorios (éxito, errores HTTP, sin red, sin sesión, 403), ViewModels (login, registro, vuelos, sesión/reserva, admin, clima) y utilidades.

**APK firmado**
```bash
./gradlew assembleRelease
# → app/build/outputs/apk/release/app-release.apk
```
La firma se configura en `app/build.gradle.kts` (`signingConfigs.release`) con `keystore.properties` y la llave `app/keystore/capebe-release.jks` (alias `capebe`). Verificación: `apksigner verify --print-certs app-release.apk`.

## 6. Captura del APK firmado y .jks
_(agregar aquí las capturas: `app-release.apk` generado y `capebe-release.jks`, con el resultado de `apksigner verify`)_

## Arquitectura (MVVM)
```
data/remote/       Modelos (DTO), VuelosApi (backend), ClimaApi (externa), ApiConfig, Servicios (Retrofit)
data/repository/   VuelosRepository, ClimaRepository, Resultado (éxito/error)
data/database/     Room: sesión iniciada (token, rol, correo) y caché de vuelos
data/local/        entidades y DAO de Room (SesionEntity/SesionDao, VueloEntity/VueloDao)
ui/viewmodel/      Login, Register, Vuelos, Sesion, Admin, Clima
ui/screen/         Inicio, Login, Registro, Principal, Vuelos, Detalle, Consentimiento, Pase, Admin
util/              Validaciones, DescripcionClima, GeneradorQr, CorreoSmtp/CorreoNativo, CalendarioNativo
```

## Persistencia local (Room)

- **Sesión**: al iniciar sesión se guarda token, rol, correo y nombre en la tabla `sesion`. Al abrir la app se restaura y se entra directo al menú del rol; si el servidor responde 401 (el token dura 7 días) se borra y se vuelve al login. Cerrar sesión borra la fila.
- **Vuelos**: `GET /vuelos` actualiza la tabla `vuelos`. Sin conexión (o con error 5xx) se muestra la última copia guardada, con un aviso en pantalla.
- La base está excluida de las copias de seguridad de Android porque contiene el token.
- La base pasó a la versión 2 (se recrea al actualizar; la tabla de usuarios anterior no se usaba).
- Prueba de los DAO en emulador: `./gradlew connectedAndroidTest`.

## Cobertura de pruebas

`./gradlew jacocoTestReport` corre los tests unitarios y deja el reporte en `app/build/reports/jacoco/index.html` (se excluyen pantallas Compose, tema, navegación y Room generado).

## Pruebas de Aceptacion
- Flujo de inicio de sesion y registro validado.
- Consulta de vuelos disponibles y descarga de pase de abordaje con QR verificada.
- Sincronizacion de clima en tiempo real operativa.
